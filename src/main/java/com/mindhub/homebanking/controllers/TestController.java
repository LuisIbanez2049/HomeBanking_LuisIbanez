package com.mindhub.homebanking.controllers;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("*")
@RequestMapping("/api")
public class TestController {


    @GetMapping("/message")
    private String messageController() {
        return "Respuesta de test controller";
    }
}
