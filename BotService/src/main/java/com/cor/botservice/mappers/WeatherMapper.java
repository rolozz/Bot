package com.cor.botservice.mappers;

import com.cor.botservice.dto.weather.WeatherApiResponse;
import com.cor.botservice.dto.weather.WeatherResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "Spring")
public interface WeatherMapper {

    @Mapping(target = "city", source = "name")
    @Mapping(target = "temperature", source = "main.temp")
    @Mapping(target = "feelsLike", source = "main.feelsLike")
    @Mapping(target = "humidity", source = "main.humidity")
    @Mapping(target = "pressure", source = "main.pressure")
    @Mapping(target = "windSpeed", source = "wind.speed")
    @Mapping(target = "windDirection", source = "wind.deg")
    @Mapping(target = "description", expression = "java(getWeatherDescription(weatherApiResponse))")
    WeatherResponseDto toWeatherResponseDto(WeatherApiResponse weatherApiResponse);

    @SuppressWarnings("unused")
    default String getWeatherDescription(WeatherApiResponse weatherApiResponse) {
        return weatherApiResponse.getWeather() != null && !weatherApiResponse.getWeather().isEmpty()
                ? weatherApiResponse.getWeather().getFirst().getDescription()
                : "Нет данных";
    }
}
