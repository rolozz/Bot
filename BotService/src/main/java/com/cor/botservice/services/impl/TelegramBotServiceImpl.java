package com.cor.botservice.services.impl;

import com.cor.botservice.client.nasa.ApodService;
import com.cor.botservice.client.weather.WeatherService;
import com.cor.botservice.dto.ApodResponse;
import com.cor.botservice.dto.ResponseFromDataBaseDto;
import com.cor.botservice.mappers.DataBaseMapper;
import com.cor.botservice.messaging.KafkaConsumerService;
import com.cor.botservice.messaging.KafkaProducerService;
import com.cor.botservice.services.TelegramBotService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class TelegramBotServiceImpl implements TelegramBotService {

    final ApodService apodService;
    final KafkaProducerService kafkaProducerService;
    final DataBaseMapper dataBaseMapper;
    final RedisTemplate<Long, ResponseFromDataBaseDto> template;
    final WeatherService weatherService;
    final RetryTemplate retryTemplate;

    @Override
    public void handleInlineQuery(InlineQuery inlineQuery, TelegramLongPollingBot bot) {
        User user = inlineQuery.getFrom();
        kafkaProducerService.send(dataBaseMapper.request(user));
        log.info("Создан объект RequestToDataBase: {}", user);
        final var weather =  "Напиши город Маркуше, геолокацию ж вы ,суки, скрываете";

        Long checkId = user.getId();
        ResponseFromDataBaseDto response = fromRedis(checkId);
        if(response.getCity() != null) {
            InlineQueryResultArticle predictionResult = createPredictionResult(response.getPrediction());
            InlineQueryResultArticle randomApodResult = createRandomApodResult();
            InlineQueryResultArticle randomWeather = createWeather(response.getCity());

            AnswerInlineQuery answer = new AnswerInlineQuery();
            answer.setInlineQueryId(inlineQuery.getId());
            answer.setResults(List.of(predictionResult, randomApodResult, randomWeather));
            answer.setCacheTime(0);

            executeSafely(answer, bot);
        }else {
            InlineQueryResultArticle predictionResult = createPredictionResult(response.getPrediction());
            InlineQueryResultArticle randomApodResult = createRandomApodResult();
            InlineQueryResultArticle randomWeather = createWeather(weather);

            AnswerInlineQuery answer = new AnswerInlineQuery();
            answer.setInlineQueryId(inlineQuery.getId());
            answer.setResults(List.of(predictionResult, randomApodResult, randomWeather));
            answer.setCacheTime(0);

            executeSafely(answer, bot);
        }
    }

    private InlineQueryResultArticle createPredictionResult(String joke) {
        InlineQueryResultArticle predictionResult = new InlineQueryResultArticle();
        predictionResult.setId("1");
        predictionResult.setTitle("Получить на лицо ✨");

        InputTextMessageContent predictionContent = new InputTextMessageContent();
        predictionContent.setMessageText(formatAsQuote(joke));
        predictionResult.setInputMessageContent(predictionContent);
        predictionResult.setThumbnailUrl("https://i.postimg.cc/SQGYpbFg/owl.jpg");
        return predictionResult;
    }

    private InlineQueryResultArticle createRandomApodResult() {
        InlineQueryResultArticle randomApodResult = new InlineQueryResultArticle();
        randomApodResult.setId("2");
        randomApodResult.setTitle("Получить случайное фото 🎲");

        randomApodResult.setThumbnailUrl("https://i.postimg.cc/1zNNxgsF/AP2.jpg");

        ApodResponse randomApod = apodService.getRandomPhoto().block();
        String messageText;

        if (randomApod != null && randomApod.getUrl() != null) {
            messageText = "📷 *Случайное фото от NASA*\n\n"
                    + "*Название:* " + randomApod.getTitle() + "\n"
                    + "*Дата:* " + randomApod.getDate() + "\n\n"
                    + "*" + randomApod.getUrl() + "*\n\n"
                    + randomApod.getExplanation();
        } else {
            messageText = "📷 К сожалению, случайное фото дня недоступно.";
        }

        InputTextMessageContent content = createInputTextMessageContent(messageText);
        randomApodResult.setInputMessageContent(content);

        return randomApodResult;
    }

    private InlineQueryResultArticle createWeather(String city){
        InlineQueryResultArticle weatherResult = new InlineQueryResultArticle();
        weatherResult.setId("3");
        weatherResult.setTitle("Погода в твоей дыре");
        weatherResult.setThumbnailUrl("https://i.postimg.cc/MTvvycnK/671ba6ce4a381565102388.webp");
        Mono<String> weatherMono = weatherService.getWeather(city);
        String messageText;

        messageText = weatherMono.block();

        if (messageText != null) {
            messageText = "Погода: " + "\n"
                    + messageText;
        } else {
            messageText = "🌍 Не удалось получить данные о погоде для города " + city;
        }

        InputTextMessageContent content = createInputTextMessageContent(messageText);
        weatherResult.setInputMessageContent(content);

        return weatherResult;
    }

    private ResponseFromDataBaseDto fromRedis(Long id) {
        return retryTemplate.execute(context -> {
            ResponseFromDataBaseDto response = template.opsForValue().get(id);
            if (response != null) {
                log.info("Ответ найден в Redis для ID: {}. Попытка № {}", id, context.getRetryCount());
                template.delete(id);
                return response;
            } else {
                log.warn("Ответ не найден в Redis для ID: {}. Попытка № {}", id, context.getRetryCount());
                throw new RuntimeException("Ответ не найден в Redis");
            }
        });
    }

    private void executeSafely(Object message, TelegramLongPollingBot bot) {
        try {
            if (message instanceof BotApiMethod<?> botApiMethod) {
                bot.execute(botApiMethod);
            } else if (message instanceof SendPhoto sendPhoto) {
                bot.execute(sendPhoto);
            }
        } catch (TelegramApiException e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }
    }

    private String formatAsQuote(String text) {
        return "❝ " + text + " ❞";
    }

    private InputTextMessageContent createInputTextMessageContent(String messageText) {
        InputTextMessageContent content = new InputTextMessageContent();
        content.setMessageText(messageText);
        content.setParseMode("Markdown");
        return content;
    }
}
