package com.cor.botservice.messaging;

import com.cor.botservice.dto.RequestToDataBase;

public interface KafkaProducerService {

    void send(RequestToDataBase request);

}
