package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.utils.GenerateAccountNumber;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
//@RequestMapping("/api/accounts")
@RequestMapping("/api/clients")
public class AccountController {
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @GetMapping("/accounts")
    // Maneja las solicitudes GET a la ruta base "/" para obtener todos los clientes.
    public ResponseEntity<?> getAllAccounts() {
        try {
            return new ResponseEntity<>(accountService.getAllAccountDTO(), HttpStatus.OK);
        }catch (Exception e) { return new ResponseEntity<>("Error creating card: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @GetMapping("/accounts/{id}")
    // Maneja las solicitudes GET para obtener un cliente por ID.
    public ResponseEntity<?> getAccountById(@PathVariable Long id) {
        try {
            return accountService.obtainAccountById(id);
        } catch (Exception e) { return new ResponseEntity<>("Error creating card: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }


    @GetMapping("/current/accounts")
    public ResponseEntity<?> getClientAccounts(Authentication authentication) {
        try {
            return new ResponseEntity<>(accountService.getAllAccountDTOfromAuthenticationClient(authentication), HttpStatus.OK);
        } catch (Exception e) { return new ResponseEntity<>("Error creating card: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }


    @PostMapping("/current/accounts")
    public ResponseEntity <?> createClientAccounts(Authentication authentication) {
        try {
            return accountService.createAccountForAuthenticatedClient(authentication);
        } catch (Exception e) { return new ResponseEntity<>("Error creating card: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }

}
