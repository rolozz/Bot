package com.cor.botservice.messaging.impl;

import com.cor.botservice.dto.ResponseFromDataBaseDto;
import com.cor.botservice.messaging.KafkaConsumerService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableKafka
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    RedisTemplate<Long, ResponseFromDataBaseDto> redisTemplate;

    @Override
    @KafkaListener(topics = "${kafka.topic.response}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(ResponseFromDataBaseDto response) {
        log.info("Получено сообщение: {}", response);
        redisTemplate.opsForValue().set(response.getId(), response);
    }


}
