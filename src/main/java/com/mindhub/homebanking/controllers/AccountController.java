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
    public ResponseEntity<List<AccountDTO>> getAllAccounts() {
        return new ResponseEntity<>(accountService.getAllAccountDTO(), HttpStatus.OK);
    }

    @GetMapping("/accounts/{id}")
    // Maneja las solicitudes GET para obtener un cliente por ID.
    public ResponseEntity<?> getById(@PathVariable Long id) {
        if (accountService.getAccountById(id) == null) {
            return new ResponseEntity<>("Account not found with id " + id, HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(accountService.getAccountDTO(accountService.getAccountById(id)), HttpStatus.OK);
    }


    @GetMapping("/current/accounts")
    public List<AccountDTO> getClientAccounts(Authentication authentication) {
        // Obtiene el cliente basado en el nombre de usuario autenticado.
        Client client = clientService.getClientByEmail(authentication.getName());
        // Retorna los detalles del cliente en la respuesta.
        return client.getAccounts().stream().map(account -> accountService.getAccountDTO(account)).collect(toList());
    }


    @PostMapping("/current/accounts")
    public ResponseEntity <?> createClientAccounts(Authentication authentication) {
        // Obtiene el cliente basado en el nombre de usuario autenticado.
        Client client = clientService.getClientByEmail(authentication.getName());

        String accountNumber;
        boolean isUnique = false;

        do {
            accountNumber = GenerateAccountNumber.generateSerialNumber();
            Account account = accountService.getAccountByNumber(accountNumber);
            // Si la cuenta no existe en la base de datos, es única
            if (account == null) {
                isUnique = true;
            }

        } while (!isUnique);

        if (client.getAccounts().size() < 3) {
            Account newAccount = new Account(accountNumber, LocalDateTime.now(), 0 );
            newAccount.setClient(client);
            client.addAccount(newAccount);
            accountService.saveAccount(newAccount);
            // Retorna los detalles del cliente en la respuesta.
            return new ResponseEntity<>("Account created successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("You can't have more than 3 accounts", HttpStatus.FORBIDDEN);
    }

}
