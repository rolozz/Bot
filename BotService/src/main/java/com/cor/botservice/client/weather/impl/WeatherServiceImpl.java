package com.cor.botservice.client.weather.impl;

import com.cor.botservice.client.weather.WeatherService;
import com.cor.botservice.dto.ApodResponse;
import com.cor.botservice.dto.weather.WeatherApiResponse;
import com.cor.botservice.dto.weather.WeatherResponseDto;
import com.cor.botservice.mappers.WeatherMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class WeatherServiceImpl implements WeatherService {

    private final WebClient webClient;
    private final WeatherMapper weatherMapper;

    @Autowired
    public WeatherServiceImpl(@Qualifier("openWeatherWebClient") WebClient webClient, WeatherMapper weatherMapper) {
        this.webClient = webClient;
        this.weatherMapper = weatherMapper;
    }

    @CircuitBreaker(name = "getWeather", fallbackMethod = "getWeatherFallback")
    @Override
    public Mono<String> getWeather(String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/weather")
                        .queryParam("q", city)
                        .queryParam("units", "metric")
                        .queryParam("lang", "ru")
                        .build())
                .retrieve()
                .bodyToMono(WeatherApiResponse.class)
                .map(weatherMapper::toWeatherResponseDto)
                .map(WeatherResponseDto::formatWeather)
                .doOnSuccess(response -> log.info("Weather data retrieved for city: {}", city))
                .doOnError(error -> log.error("Failed to retrieve weather data for city: {}", city, error));
    }

    @SuppressWarnings("unused")
    private Mono<String> getWeatherFallback(Throwable throwable) {
        log.error("Ошибка в Circuit Breaker для getWeather: {}", throwable.getMessage());
        return Mono.just("Сервис не доступен");
    }

}
