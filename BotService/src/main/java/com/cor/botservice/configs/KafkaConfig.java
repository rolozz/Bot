package com.cor.botservice.configs;

import com.cor.botservice.dto.RequestToDataBase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Bean
    public KafkaTemplate<String, RequestToDataBase> kafkaTemplate(
            ProducerFactory<String, RequestToDataBase> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }
}
