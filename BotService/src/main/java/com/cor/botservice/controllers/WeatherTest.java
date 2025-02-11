package com.cor.botservice.controllers;

import com.cor.botservice.client.weather.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WeatherTest {

    private  final WeatherService weatherService;

    @Autowired
    public WeatherTest(WeatherService weatherService) {
        this.weatherService = weatherService;
    }


    @GetMapping("/test")
    public String test(){
        return weatherService.getWeather("yaroslavl").block();
    }
}
