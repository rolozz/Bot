package com.cor.managementservice.controllers;

import com.cor.managementservice.dto.JokeDto;
import com.cor.managementservice.services.JokerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JokeController {

    private final JokerService jokerService;

    @PostMapping("/save")
    public ResponseEntity<String> saveJokes(@RequestBody List<JokeDto> jokeDtoList){
        return ResponseEntity.ok(jokerService.saveJokes(jokeDtoList));
    }
}
