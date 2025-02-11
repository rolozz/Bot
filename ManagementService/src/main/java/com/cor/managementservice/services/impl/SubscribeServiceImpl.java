package com.cor.managementservice.services.impl;

import com.cor.managementservice.dto.RequestToDataBase;
import com.cor.managementservice.dto.ResponseFromDataBaseDto;
import com.cor.managementservice.entities.Subscribe;
import com.cor.managementservice.mappers.SubscribeMapper;
import com.cor.managementservice.repositories.SubscribeRepository;
import com.cor.managementservice.services.JokerService;
import com.cor.managementservice.services.SubscribeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SubscribeServiceImpl implements SubscribeService {

    final SubscribeRepository subscribeRepository;
    final SubscribeMapper subscribeMapper;
    final JokerService jokerService;

    @Override
    @Transactional
    public ResponseFromDataBaseDto save(RequestToDataBase request) {
        log.info("Начало прцесса по ID: {}", request.getId());
        ResponseFromDataBaseDto responseFromDataBaseDto = new ResponseFromDataBaseDto();
        if (Boolean.TRUE.equals(subscribeRepository.existsById(request.getId()))) {
            log.info("Подписчик с ID: {} найден.", request.getId());

            final var subscribe = subscribeRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Subscribe not found"));
            responseFromDataBaseDto.setCity(subscribe.getCity());
            responseFromDataBaseDto.setId(subscribe.getId());
            responseFromDataBaseDto.setPrediction(jokerService.getRandomJoke());
            log.debug("Актуальный счетчик для подписчика с ID {}: {}", request.getId(), subscribe.getCount());

            subscribe.setCount(subscribe.getCount() + 1);
            subscribeRepository.save(subscribe);
            log.info("Подписчик с ID: {} обнавлен, новое значение: {}", request.getId(), subscribe.getCount());
        } else {
            log.info("Не найден подписчик с ID: {}, создается новыая запись.", request.getId());

            subscribeRepository.save(subscribeMapper.toEntity(request));
            responseFromDataBaseDto.setCity(null);
            responseFromDataBaseDto.setId(request.getId());
            responseFromDataBaseDto.setPrediction(jokerService.getRandomJoke());
            log.info("Новый подписчик с ID: {} создан.", request.getId());
        }
        return responseFromDataBaseDto;
    }

    @Override
    @Transactional
    public void cityCommit(Long id, String city) {
        log.info("Ищем подписку с id: {}", id);
        Subscribe commit = subscribeRepository.findSubscribeById(id);
        if (commit == null) {
            throw new EntityNotFoundException("Подписка с id " + id + " не найдена.");
        }
        commit.setCity(city);
    }
}
