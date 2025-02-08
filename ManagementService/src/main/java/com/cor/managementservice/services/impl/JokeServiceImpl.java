package com.cor.managementservice.services.impl;

import com.cor.managementservice.dto.JokeDto;
import com.cor.managementservice.mappers.JokeMapper;
import com.cor.managementservice.repositories.JokeRepository;
import com.cor.managementservice.services.JokerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class JokeServiceImpl implements JokerService {

    private final JokeMapper jokeMapper;
    private final JokeRepository jokeRepository;
    @Value("${joke.backup-file}")
    private String backupFilePath;

    @Override
    @Transactional
    public String saveJokes(List<JokeDto> jokeDtoList) {
        try {
            jokeRepository.saveAll(jokeMapper.toEntityList(jokeDtoList));
            saveJokesToFile(jokeDtoList);
            log.info("Сохранено: {}", jokeDtoList);
            return "Все гуд";
        } catch (Exception e) {
            log.warn("Ошибка при сохранении списка: {}", e.getMessage(), e);
            return "Все плохо";
        }

    }

    private void saveJokesToFile(List<JokeDto> jokeDtoList) {
        final var objectMapper = new ObjectMapper();
        final var file = new File(backupFilePath);
        try {
            if (file.exists()) {
                List<JokeDto> existingJokes = readJokesFromFile(file, objectMapper);
                jokeDtoList.addAll(existingJokes);
            }
            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(objectMapper.writeValueAsString(jokeDtoList));
                log.info("Список шуток сохранен в файл: {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Ошибка при сохранении шуток в файл: {}", e.getMessage(), e);
        }
    }

    private List<JokeDto> readJokesFromFile(File file, ObjectMapper objectMapper) {
        try {
            return List.of(objectMapper.readValue(file, JokeDto[].class));
        } catch (IOException e) {
            log.warn("Ошибка при чтении файла, создаем новый", e);
            return List.of();
        }
    }
}
