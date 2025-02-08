package com.cor.botservice.components.bots.telegram;


import com.cor.botservice.services.TelegramBotService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@Component
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MyInlineBot extends TelegramLongPollingBot {


    private final TelegramBotService telegramBotService;

    private final String botUsername;
    private final String botToken;


    @Autowired
    public MyInlineBot(TelegramBotService telegramBotService,
                       @Value("${telegram.bot.username}") String botUsername,
                       @Value("${telegram.bot.token}") String botToken) {
        this.telegramBotService = telegramBotService;
        this.botUsername = botUsername;
        this.botToken = botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasInlineQuery()) {
            telegramBotService.handleInlineQuery(update.getInlineQuery(), this);
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}