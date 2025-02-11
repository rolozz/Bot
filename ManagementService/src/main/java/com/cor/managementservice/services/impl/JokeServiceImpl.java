package com.cor.managementservice.services.impl;

import com.cor.managementservice.dto.JokeDto;
import com.cor.managementservice.dto.ResponseFromDataBaseDto;
import com.cor.managementservice.entities.Joke;
import com.cor.managementservice.mappers.JokeMapper;
import com.cor.managementservice.repositories.JokeRepository;
import com.cor.managementservice.services.JokerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.OptimisticLockException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class JokeServiceImpl implements JokerService {

    final JokeMapper jokeMapper;
    final JokeRepository jokeRepository;
    final RetryTemplate retryTemplate;

    @Value("${joke.backup-file}")
    String backupFilePath;

    @Override
    @Transactional
    public String saveJokes(List<JokeDto> jokeDtoList) {
        log.info("Начинаем сохранение списка шуток: {}", jokeDtoList);

        try {
            jokeRepository.saveAll(jokeMapper.toEntityList(jokeDtoList));
            saveJokesToFile(jokeDtoList);
            log.info("Шутки успешно сохранены в базе данных и файле.");
            return "Все гуд";
        } catch (Exception e) {
            log.error("Ошибка при сохранении списка шуток: {}", e.getMessage(), e);
            return "Все плохо";
        }
    }

    @Override
    @Transactional
    public String getRandomJoke() {
        log.info("Запрос на получение случайной активной шутки.");

        return retryTemplate.execute(context -> {
            Optional<Joke> jokeOptional = jokeRepository.findRandomActiveJoke();

            if (jokeOptional.isPresent()) {
                Joke joke = jokeOptional.get();
                log.info("Найдена активная шутка: {}", joke);

                int updatedRows = jokeRepository.deactivateJoke(joke.getUuid());
                if (updatedRows == 0) {
                    log.warn("Не удалось обновить флаг активности шутки (возможно, конкурентное обновление), повторяем попытку...");
                    throw new OptimisticLockException("Не удалось обновить шутку, повторяем попытку...");
                }

                log.info("Шутка успешно получена и деактивирована: {}", joke);
                return joke.getName();
            } else {
                log.warn("Все шутки уже использованы, активируем их заново.");
                jokeRepository.activateAllJokes();
                throw new EmptyResultDataAccessException("Все шутки были использованы, активируем заново", 1);
            }
        });
    }

    private void saveJokesToFile(List<JokeDto> jokeDtoList) {
        log.info("Сохранение шуток в файл: {}", backupFilePath);
        final var objectMapper = new ObjectMapper();
        final var file = new File(backupFilePath);

        try {
            if (file.exists()) {
                List<JokeDto> existingJokes = readJokesFromFile(file, objectMapper);
                jokeDtoList.addAll(existingJokes);
                log.info("Добавляем существующие шутки в файл.");
            }

            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(objectMapper.writeValueAsString(jokeDtoList));
                log.info("Шутки успешно сохранены в файл: {}", file.getAbsolutePath());
            }
        } catch (IOException e) {
            log.error("Ошибка при сохранении шуток в файл: {}", e.getMessage(), e);
        }
    }

    private List<JokeDto> readJokesFromFile(File file, ObjectMapper objectMapper) {
        log.info("Чтение шуток из файла: {}", file.getAbsolutePath());

        try {
            List<JokeDto> jokes = List.of(objectMapper.readValue(file, JokeDto[].class));
            log.info("Успешно загружено {} шуток из файла.", jokes.size());
            return jokes;
        } catch (IOException e) {
            log.warn("Ошибка при чтении файла, создаем новый файл.", e);
            return List.of();
        }
    }
}
