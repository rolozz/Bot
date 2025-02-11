package com.cor.managementservice.services;

import com.cor.managementservice.dto.RequestToDataBase;
import com.cor.managementservice.dto.ResponseFromDataBaseDto;

public interface SubscribeService {

    ResponseFromDataBaseDto save(RequestToDataBase request);

}
