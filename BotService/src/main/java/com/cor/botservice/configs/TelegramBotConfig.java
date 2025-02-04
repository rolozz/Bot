package com.cor.botservice.configs;

import com.cor.botservice.components.MyInlineBot;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfig {

    @Bean
    public TelegramBotsApi telegramBotsApi(MyInlineBot myInlineBot) throws TelegramApiException {
        final var botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(myInlineBot);
        return botsApi;
    }

}
