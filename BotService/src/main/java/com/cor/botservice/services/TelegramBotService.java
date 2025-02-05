package com.cor.botservice.services;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public interface TelegramBotService {

    void handleInlineQuery(InlineQuery inlineQuery, TelegramLongPollingBot bot);


}
