package com.cor.botservice.client.utility.impl;

import com.cor.botservice.client.utility.CommitService;
import com.cor.botservice.dto.CityRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Slf4j
@Service
public class CommitServiceImpl implements CommitService {

    private final WebClient webClient;


    @Autowired
    public CommitServiceImpl(@Qualifier("commitClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    @CircuitBreaker(name = "sendCity", fallbackMethod = "sendCityFallback")
    public void sendCity(CityRequest request) {
        log.info("OUTPUT: {}", request);
        try {
            webClient.post()
                    .uri("/commit")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            log.info("Город успешно отправлен для пользователя {}: {}", request.getId(), request.getCity());
        } catch (Exception e) {
            log.error("Ошибка при отправке города", e);
            throw new RuntimeException("Ошибка при отправке города", e);
        }
    }


    @SuppressWarnings("unused")
    private void sendCityFallback(Throwable throwable) {
        log.error("Ошибка в Circuit Breaker для getRandomPhoto: {}", throwable.getMessage());
        System.out.println("Сервер не доступен");
    }
}
