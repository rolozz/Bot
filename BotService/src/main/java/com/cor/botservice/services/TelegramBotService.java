package com.cor.botservice.services;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public interface TelegramBotService {
    void handleCommand(Long chatId, String command, TelegramLongPollingBot bot);

    void handleCallback(Long chatId, String callbackData, TelegramLongPollingBot bot);

    void handleInlineQuery(InlineQuery inlineQuery, TelegramLongPollingBot bot);
}
