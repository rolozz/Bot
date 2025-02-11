package com.cor.managementservice.messaging.impl;

import com.cor.managementservice.dto.ResponseFromDataBaseDto;
import com.cor.managementservice.messaging.ProducerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProducerServiceImpl implements ProducerService {

    final KafkaTemplate<String, ResponseFromDataBaseDto> kafkaTemplate;
    @Value("${kafka.topic.response}")
    private String topic;

    @Override
    public void send(ResponseFromDataBaseDto response) {
        kafkaTemplate.send(topic, response);
    }
}
