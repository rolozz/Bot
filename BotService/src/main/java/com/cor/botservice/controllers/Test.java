package com.cor.botservice.controllers;

import com.cor.botservice.client.utility.CommitService;
import com.cor.botservice.dto.CityRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {

    private final CommitService commitService;

    @Autowired
    public Test(CommitService commitService) {
        this.commitService = commitService;
    }

    @GetMapping("/com")
    public void test(){
        CityRequest request = new CityRequest();
        request.setId(870363202L);
        request.setCity("Yaroslavl");
        commitService.sendCity(request);
    }
}
