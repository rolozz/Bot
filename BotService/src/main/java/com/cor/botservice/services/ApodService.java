package com.cor.botservice.services;

import com.cor.botservice.dto.ApodResponse;
import reactor.core.publisher.Mono;

public interface ApodService {

    Mono<ApodResponse> getTodayPhoto();

    Mono<ApodResponse> getRandomPhoto();

}
