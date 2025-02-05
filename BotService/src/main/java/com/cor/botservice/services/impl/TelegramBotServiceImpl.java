package com.cor.botservice.services.impl;

import com.cor.botservice.dto.ApodResponse;
import com.cor.botservice.services.ApodService;
import com.cor.botservice.services.TelegramBotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotServiceImpl implements TelegramBotService {

    private static final Random RANDOM = new Random();
    private final ApodService apodService;

    public void handleInlineQuery(InlineQuery inlineQuery, TelegramLongPollingBot bot) {
        InlineQueryResultArticle predictionResult = createPredictionResult();
        InlineQueryResultArticle apodResult = createApodResult();
        InlineQueryResultArticle randomApodResult = createRandomApodResult();

        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQuery.getId());
        answer.setResults(List.of(predictionResult, apodResult, randomApodResult));
        answer.setCacheTime(1);

        executeSafely(answer, bot);
    }

    private InlineQueryResultArticle createPredictionResult() {
        InlineQueryResultArticle predictionResult = new InlineQueryResultArticle();
        predictionResult.setId("1");
        predictionResult.setTitle("Получить предсказание ✨");

        InputTextMessageContent predictionContent = new InputTextMessageContent();
        predictionContent.setMessageText(formatAsQuote(generatePrediction()));
        predictionResult.setInputMessageContent(predictionContent);
        predictionResult.setThumbnailUrl("https://i.postimg.cc/SQGYpbFg/owl.jpg");
        return predictionResult;
    }

    private InlineQueryResultArticle createApodResult() {
        InlineQueryResultArticle apodResult = new InlineQueryResultArticle();
        apodResult.setId("2");
        apodResult.setTitle("Получить фото дня NASA 📷");

        apodResult.setThumbnailUrl("https://i.postimg.cc/mkVP3CNv/ND.jpg");

        ApodResponse apod = getApodPhoto();
        String messageText;

        if (apod != null && apod.getUrl() != null) {
            messageText = "📷 *Фото дня от NASA*\n\n"
                    + "*" + apod.getUrl() + "*\n\n"
                    + apod.getExplanation();
        } else {
            messageText = "🌌 К сожалению, фото дня недоступно.";
        }

        InputTextMessageContent content = createInputTextMessageContent(messageText);
        apodResult.setInputMessageContent(content);

        return apodResult;
    }

    private InlineQueryResultArticle createRandomApodResult() {
        InlineQueryResultArticle randomApodResult = new InlineQueryResultArticle();
        randomApodResult.setId("3");
        randomApodResult.setTitle("Получить случайное фото 🎲");

        randomApodResult.setThumbnailUrl("https://i.postimg.cc/1zNNxgsF/AP2.jpg");

        ApodResponse randomApod = getRandomApodPhoto();
        String messageText;

        if (randomApod != null && randomApod.getUrl() != null) {
            messageText = "📷 *Случайное фото от NASA*\n\n"
                    + "*" + randomApod.getUrl() + "*\n\n"
                    + randomApod.getExplanation();
        } else {
            messageText = "📷 К сожалению, случайное фото дня недоступно.";
        }

        InputTextMessageContent content = createInputTextMessageContent(messageText);
        randomApodResult.setInputMessageContent(content);

        return randomApodResult;
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

    private String generatePrediction() {
        List<String> predictions = List.of(
                "Сегодня твой день!",
                "Ожидай приятный сюрприз.",
                "Будь осторожен в решениях.",
                "Скоро исполнится твое желание!"
        );
        return predictions.get(RANDOM.nextInt(predictions.size()));
    }

    private ApodResponse getApodPhoto() {
        return apodService.getTodayPhoto().block();
    }

    private ApodResponse getRandomApodPhoto() {
        return apodService.getRandomPhoto().block();
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
