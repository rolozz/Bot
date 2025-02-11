package com.cor.managementservice.controllers;

import com.cor.managementservice.dto.CityRequest;
import com.cor.managementservice.services.SubscribeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class SubscribeController {

    SubscribeService subscribeService;

    @PostMapping("/commit")
    public void setCommit(@RequestBody CityRequest request){
        log.info("input: {}", request);
        subscribeService.cityCommit(request.getId(), request.getCity());
    }
}
