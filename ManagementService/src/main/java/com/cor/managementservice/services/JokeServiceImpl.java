package com.cor.managementservice.services;

import com.cor.managementservice.dto.JokeDto;
import com.cor.managementservice.mappers.JokeMapper;
import com.cor.managementservice.repositories.JokeRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class JokeServiceImpl {

    JokeMapper jokeMapper;
    JokeRepository jokeRepository;

    public String saveJokes(List<JokeDto> jokeDtoList){
        try {
            jokeRepository.saveAll(jokeMapper.toEntityList(jokeDtoList));
            log.info("Сохранено: {}", jokeDtoList);
            return "Все гуд";
        }catch (Exception e){
            log.warn("Ошибка при сохранении списка шуток: {}", e.getMessage(), e);
            return "Все плохо";
        }

    }

}
