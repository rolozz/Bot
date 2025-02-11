package com.cor.managementservice.messaging.impl;

import com.cor.managementservice.dto.RequestToDataBase;
import com.cor.managementservice.messaging.ProducerService;
import com.cor.managementservice.services.SubscribeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@EnableKafka
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConsumerServiceImpl {

    SubscribeService subscribeService;

    ProducerService producerService;

    @KafkaListener(topics = "${kafka.topic.request}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(RequestToDataBase request) {
        producerService.send(subscribeService.save(request));
    }

}
