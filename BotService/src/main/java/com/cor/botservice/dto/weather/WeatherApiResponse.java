package com.cor.botservice.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WeatherApiResponse {

    private String name;
    private Main main;
    private List<Weather> weather;
    private Wind wind;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Main {
        private double temp;
        private double feelsLike;
        private int humidity;
        private int pressure;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Weather {
        private String description;

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @ToString
    public static class Wind {
        private double speed;
        private int deg;

    }
}

