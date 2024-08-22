package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@CrossOrigin(origins = "http://localhost:5173") // Reemplaza con la URL de tu aplicación React ===>===>===>===>===>===>===>===>===>===>===>===>===>===>
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @GetMapping("/")
    // Maneja las solicitudes GET a la ruta base "/" para obtener todos los clientes.
    public List<AccountDTO> getAllClients() {
        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toList());

    }

    @GetMapping("/{id}")
    // Maneja las solicitudes GET para obtener un cliente por ID.
    public ResponseEntity<AccountDTO> getById(@PathVariable Long id) {
        return accountRepository.findById(id).map(account -> new AccountDTO(account)) // Convertir Client a ClientDTO
                .map(ResponseEntity::ok) // Si está presente, devolver 200 OK con el ClientDTO
                .orElse(ResponseEntity.notFound().build());
    }
}
