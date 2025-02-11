package com.cor.managementservice.messaging;

import com.cor.managementservice.dto.ResponseFromDataBaseDto;

public interface ProducerService {

    void send(ResponseFromDataBaseDto response);
}
