package com.cor.botservice.client.libre.impl;

import com.cor.botservice.client.libre.TranslateService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class TranslateServiceImpl implements TranslateService {

    private final WebClient webClient;

    @Autowired
    public TranslateServiceImpl(@Qualifier("translateWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public Mono<String> translate(String text, String sourceLang, String targetLang) {
        return webClient.post()
                .uri("/translate")
                .bodyValue(Map.of(
                        "q", text,
                        "source", sourceLang,
                        "target", targetLang,
                        "format", "text"
                ))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> json.get("translatedText").asText());
    }
}
