package com.cor.botservice.services;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

public interface UtilityTGService {
    void handleMessage(Update update, AbsSender bot);
}
