package com.cor.managementservice.services;

import com.cor.managementservice.dto.JokeDto;
import com.cor.managementservice.dto.ResponseFromDataBaseDto;

import java.util.List;

public interface JokerService {

    String saveJokes(List<JokeDto> jokeDtoList);

    String getRandomJoke();

}
