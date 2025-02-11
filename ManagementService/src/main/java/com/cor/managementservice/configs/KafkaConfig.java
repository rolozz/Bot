package com.cor.managementservice.configs;

import com.cor.managementservice.dto.ResponseFromDataBaseDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, ResponseFromDataBaseDto> kafkaTemplate(
            ProducerFactory<String, ResponseFromDataBaseDto> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}
