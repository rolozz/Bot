package com.cor.botservice.messaging.impl;

import com.cor.botservice.dto.RequestToDataBase;
import com.cor.botservice.messaging.KafkaProducerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class KafkaProducerServiceImpl implements KafkaProducerService {

    final KafkaTemplate<String, RequestToDataBase> kafkaTemplate;
    @Value("${kafka.topic.request}")
    private String topic;
    @Override
    public void send(RequestToDataBase request) {
        kafkaTemplate.send(topic, request);
        log.info("Отправка сообщения :{}", request);
    }
}
