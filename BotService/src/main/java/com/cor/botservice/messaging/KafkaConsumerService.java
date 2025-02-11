package com.cor.botservice.messaging;

import com.cor.botservice.dto.ResponseFromDataBaseDto;

import java.util.concurrent.CompletableFuture;

public interface KafkaConsumerService {

    void consume(ResponseFromDataBaseDto response);
}
