package com.cor.managementservice.configs;

import jakarta.persistence.OptimisticLockException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class RetryConfig {

    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(3) // 3 попытки
                .exponentialBackoff(100, 2, 2000) // 100ms -> 200ms -> 400ms (max 2s)
                .retryOn(OptimisticLockException.class)
                .retryOn(EmptyResultDataAccessException.class)
                .build();
    }

}
