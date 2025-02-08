package com.cor.botservice.messaging.settings;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopics {

    @Bean
    public NewTopic toDataBase(){
        return new NewTopic("to-database-event",1,(short) 1);
    }

    @Bean
    public NewTopic fromDataBase(){
        return new NewTopic("from-database-event",1,(short) 1);
    }
}
