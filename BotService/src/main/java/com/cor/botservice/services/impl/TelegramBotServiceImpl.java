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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Random;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramBotServiceImpl implements TelegramBotService {

    private static final int MAX_CAPTION_LENGTH = 1024;
    private static final Random RANDOM = new Random();
    private final ApodService apodService;

    @Override
    public void handleCommand(Long chatId, String command, TelegramLongPollingBot bot) {
        switch (command) {
            case "/start" -> sendOptionsKeyboard(chatId, bot);
            case "/предсказание" -> sendPrediction(chatId, bot);
            case "/фото_дня" -> sendPhotoOfDay(chatId, bot);
            case "/случайное_фото" -> sendRandomPhoto(chatId, bot);
            default -> sendTextMessage(chatId, "Неизвестная команда", bot);
        }
    }

    public void handleInlineQuery(InlineQuery inlineQuery, TelegramLongPollingBot bot) {
        InlineQueryResultArticle predictionResult = new InlineQueryResultArticle();
        predictionResult.setId("1");
        predictionResult.setTitle("Получить предсказание ✨");
        predictionResult.setDescription("Нажмите, чтобы получить случайное предсказание");

        InputTextMessageContent predictionContent = new InputTextMessageContent();
        predictionContent.setMessageText(generatePrediction());
        predictionResult.setInputMessageContent(predictionContent);

        InlineQueryResultArticle apodResult = new InlineQueryResultArticle();
        apodResult.setId("2");
        apodResult.setTitle("Получить фото дня 📷");
        apodResult.setDescription("Нажмите, чтобы увидеть фото дня");


        ApodResponse apod = getApodPhoto();
        if (apod != null && apod.getUrl() != null) {
            String caption = "🌌 " + apod.getExplanation();
            if (caption.length() > MAX_CAPTION_LENGTH) {
                caption = caption.substring(0, MAX_CAPTION_LENGTH-2);
            }

            InputTextMessageContent apodContent = new InputTextMessageContent();
            apodContent.setMessageText(caption + "\n" + apod.getUrl());
            apodResult.setInputMessageContent(apodContent);
        } else {
            InputTextMessageContent apodContent = new InputTextMessageContent();
            apodContent.setMessageText("🌌 К сожалению, фото дня недоступно.");
            apodResult.setInputMessageContent(apodContent);
        }

        InlineQueryResultArticle randomApodResult = new InlineQueryResultArticle();
        randomApodResult.setId("3");
        randomApodResult.setTitle("Получить случайное фото 🎲");
        randomApodResult.setDescription("Нажмите, чтобы увидеть случайное фото");

        ApodResponse randomApod = getRandomApodPhoto();
        if (randomApod != null && randomApod.getUrl() != null) {
            String caption = "📷 " + randomApod.getExplanation();
            if (caption.length() > MAX_CAPTION_LENGTH) {
                caption = caption.substring(0, MAX_CAPTION_LENGTH-2);
            }

            InputTextMessageContent randomApodContent = new InputTextMessageContent();
            randomApodContent.setMessageText(caption + "\n" + randomApod.getUrl());
            randomApodResult.setInputMessageContent(randomApodContent);
        } else {
            InputTextMessageContent randomApodContent = new InputTextMessageContent();
            randomApodContent.setMessageText("📷 К сожалению, случайное фото дня недоступно.");
            randomApodResult.setInputMessageContent(randomApodContent);
        }

        AnswerInlineQuery answer = new AnswerInlineQuery();
        answer.setInlineQueryId(inlineQuery.getId());
        answer.setResults(List.of(predictionResult, apodResult, randomApodResult));
        answer.setCacheTime(1);

        executeSafely(answer, bot);
    }

    @Override
    public void handleCallback(Long chatId, String callbackData, TelegramLongPollingBot bot) {
        handleCommand(chatId, callbackData, bot);
    }

    private void sendPrediction(Long chatId, TelegramLongPollingBot bot) {
        sendTextMessage(chatId, "Ваше предсказание: " + generatePrediction(), bot);
    }

    private void sendPhotoOfDay(Long chatId, TelegramLongPollingBot bot) {
        apodService.getTodayPhoto().subscribe(
                response -> sendPhotoMessage(chatId, response.getUrl(), response.getExplanation(), bot),
                error -> log.error("Ошибка при получении фото дня: {}", error.getMessage())
        );
    }

    private void sendRandomPhoto(Long chatId, TelegramLongPollingBot bot) {
        apodService.getRandomPhoto().subscribe(
                response -> sendPhotoMessage(chatId, response.getUrl(), response.getExplanation(), bot),
                error -> log.error("Ошибка при получении случайного фото: {}", error.getMessage())
        );
    }

    private void sendOptionsKeyboard(Long chatId, TelegramLongPollingBot bot) {
        SendMessage message = new SendMessage(chatId.toString(), "Выберите действие:");
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(List.of(
                List.of(createButton("🔮 Предсказание", "/предсказание")),
                List.of(createButton("📷 Фото дня", "/фото_дня")),
                List.of(createButton("🎲 Случайное фото", "/случайное_фото"))
        ));
        message.setReplyMarkup(markup);
        executeSafely(message, bot);
    }

    private InlineKeyboardButton createButton(String text, String callbackData) {
        InlineKeyboardButton button = new InlineKeyboardButton(text);
        button.setCallbackData(callbackData);
        return button;
    }

    private void sendTextMessage(Long chatId, String text, TelegramLongPollingBot bot) {
        executeSafely(new SendMessage(chatId.toString(), text), bot);
    }

    private void sendPhotoMessage(Long chatId, String photoUrl, String caption, TelegramLongPollingBot bot) {
        SendPhoto message = new SendPhoto();
        message.setChatId(chatId.toString());
        message.setPhoto(new InputFile(photoUrl));
        message.setCaption(caption);
        executeSafely(message, bot);
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

}
