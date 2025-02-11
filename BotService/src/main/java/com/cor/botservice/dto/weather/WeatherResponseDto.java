package com.cor.botservice.dto.weather;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class WeatherResponseDto {

    String city;
    double temperature;
    double feelsLike;
    double windSpeed;
    int windDirection;
    int humidity;
    int pressure;
    String description;

    public String formatWeather() {
        return String.format(
                "🌍 Город: %s%n" +
                        "🌡 Температура: %.1f°C%n" +
                        "❄ Ощущается как: %.1f°C%n" +
                        "💨 Ветер: %.1f м/с, направление %d°%n" +
                        "🌫 Влажность: %d%%%n" +
                        "📊 Давление: %d мм рт. ст.%n" +
                        "☁ Погода: %s",
                city, temperature, feelsLike, windSpeed, windDirection, humidity, pressure, description
        );
    }
}
