package com.cor.botservice.client.nasa.impl;

import com.cor.botservice.dto.ApodResponse;
import com.cor.botservice.client.nasa.ApodService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class ApodServiceImpl implements ApodService {

    private static final ApodResponse circuitResponse = new ApodResponse("Хьюстон, у нас проблемы",
            "Сервис не доступен");

    private final WebClient webClient;
    @Value("${telegram.bot.nasa_key}")
    private String nasaKey;

    @Autowired
    public ApodServiceImpl(@Qualifier("nasaWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    @CircuitBreaker(name = "getRandomPhoto", fallbackMethod = "getRandomPhotoFallback")
    public Mono<ApodResponse> getRandomPhoto() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/planetary/apod")
                        .queryParam("api_key", nasaKey)
                        .queryParam("count", 1)
                        .build())
                .retrieve()
                .bodyToMono(ApodResponse[].class) // Получаем массив
                .map(apodResponses -> apodResponses.length > 0 ? apodResponses[0] : null)
                .doOnNext(response -> log.info("Ответ от NASA random: {}", response))
                .doOnError(error -> log.error("Ошибка при запросе случайного фото: {}", error.getMessage()));
    }

    @SuppressWarnings("unused")
    private Mono<ApodResponse> getRandomPhotoFallback(Throwable throwable) {
        log.error("Ошибка в Circuit Breaker для getRandomPhoto: {}", throwable.getMessage());
        return Mono.just(circuitResponse);
    }

}
