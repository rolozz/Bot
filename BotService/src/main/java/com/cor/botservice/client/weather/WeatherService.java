package com.cor.botservice.client.weather;

import reactor.core.publisher.Mono;

public interface WeatherService {

    Mono<String> getWeather(String city);

}
