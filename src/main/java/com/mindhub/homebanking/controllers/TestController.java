package com.mindhub.homebanking.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    private String messageController() {
        return "Respuesta de test controller";
    }
}
