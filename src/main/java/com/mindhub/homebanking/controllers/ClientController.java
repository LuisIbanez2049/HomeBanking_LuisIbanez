package com.mindhub.homebanking.controllers;



import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Me indica que la clase actua como un controlador y que cada método devuelve un objeto
@RequestMapping("/api/clients") //Cualquier solicitud que empiece con "/api/clients" será manejada por este controlador
public class ClientController {


    @GetMapping("/hello") // Asigna la ruta "/hello" al método getClients()
                          // La ruta completa para invocar este método seria "http://localhost:8080/api/clients/hello"
    public String getClients(){
        return "Hello Clients!!!!";
    }

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/")
    public List<Client> getAllClients(){
        return clientRepository.findAll();
    }
}


