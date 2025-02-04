package com.cor.botservice.services.impl;

import com.cor.botservice.dto.ApodResponse;
import com.cor.botservice.services.ApodService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ApodServiceImpl(WebClient nasaWebClient) {
        this.webClient = nasaWebClient;
    }

    @Override
    @CircuitBreaker(name = "getTodayPhoto", fallbackMethod = "getTodayPhotoFallback")
    public Mono<ApodResponse> getTodayPhoto() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/planetary/apod")
                        .queryParam("api_key", nasaKey)
                        .build())
                .retrieve()
                .bodyToMono(ApodResponse.class)
                .doOnNext(response -> log.info("Ответ от NASA today: {}", response));
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
                .map(apodResponses -> apodResponses.length > 0 ? apodResponses[0] : null) // Берем первый элемент
                .doOnNext(response -> log.info("Ответ от NASA random: {}", response))
                .doOnError(error -> log.error("Ошибка при запросе случайного фото: {}", error.getMessage()));
    }

    @SuppressWarnings("unused")
    private Mono<ApodResponse> getTodayPhotoFallback(Throwable throwable) {
        log.error("Ошибка в Circuit Breaker для getTodayPhoto: {}", throwable.getMessage());
        return Mono.just(circuitResponse);
    }

    @SuppressWarnings("unused")
    private Mono<ApodResponse> getRandomPhotoFallback(Throwable throwable) {
        log.error("Ошибка в Circuit Breaker для getRandomPhoto: {}", throwable.getMessage());
        return Mono.just(circuitResponse);
    }

}
