package com.cor.botservice.services.impl;

import com.cor.botservice.client.utility.CommitService;
import com.cor.botservice.dto.CityRequest;
import com.cor.botservice.services.UtilityTGService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.bots.AbsSender;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class UtilityTGServiceImpl implements UtilityTGService {

    private final CommitService commitService;

    private final Map<Long, Boolean> userStateMap = new ConcurrentHashMap<>();

    @Override
    public void handleMessage(Update update, AbsSender bot) {
        if (!update.hasMessage()) return;

        Message message = update.getMessage();
        if (message.getFrom() == null || message.getText() == null) return;

        log.info("данные: {}", message.getFrom());
        log.info("текст: {}", message.getText());

        Long userId = message.getFrom().getId();
        String city = message.getText().trim();

        if ("/start set_city".equalsIgnoreCase(city)) {
            userStateMap.put(userId, true);
            sendMessage(bot, userId, "Введите ваш город:");
        } else if (Boolean.TRUE.equals(userStateMap.getOrDefault(userId, false))) {
            userStateMap.remove(userId);

            try {
                commitService.sendCity(new CityRequest(userId, city));
                sendMessage(bot, userId, "Теперь можешь пользоваться по упоминанию из любого места: " + city);
            } catch (Exception e) {
                sendMessage(bot, userId, "Ошибка: " + e.getMessage());
                log.error("Ошибка при сохранении города", e);
            }
        }
    }

    private void sendMessage(AbsSender bot, Long chatId, String city) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(city);
        try {
            bot.execute(message);
        } catch (Exception e) {
            log.error("Ошибка при отправке сообщения: {}", e.getMessage());
        }
    }
}
