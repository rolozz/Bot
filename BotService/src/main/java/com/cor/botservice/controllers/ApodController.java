package com.cor.botservice.controllers;

import com.cor.botservice.dto.ApodResponse;
import com.cor.botservice.services.ApodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ApodController {

    private final ApodService apodService;

    @GetMapping("/apod/photo")
    public Mono<String> getTodayPhoto() {
        return apodService.getTodayPhoto()
                .map(ApodResponse::getUrl)
                .doOnTerminate(() -> System.out.println("Запрос на фото дня завершен"))
                .onErrorResume(e -> {
                    System.err.println("Ошибка при получении фото дня: " + e.getMessage());
                    return Mono.just("Ошибка при получении фото дня");
                });
    }

}
