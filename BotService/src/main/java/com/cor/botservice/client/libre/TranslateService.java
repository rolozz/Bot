package com.cor.botservice.client.libre;

import reactor.core.publisher.Mono;

public interface TranslateService {

    Mono<String> translate(String text, String sourceLang, String targetLang);

}
