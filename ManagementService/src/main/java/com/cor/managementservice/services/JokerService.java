package com.cor.managementservice.services;

import com.cor.managementservice.dto.JokeDto;

import java.util.List;

public interface JokerService {

    String saveJokes(List<JokeDto> jokeDtoList);

}
