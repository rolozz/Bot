package com.cor.botservice.client.nasa;

import com.cor.botservice.dto.ApodResponse;
import reactor.core.publisher.Mono;

public interface ApodService {

    Mono<ApodResponse> getRandomPhoto();

}
