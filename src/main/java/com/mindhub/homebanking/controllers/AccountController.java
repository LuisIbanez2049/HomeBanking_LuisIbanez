package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.utils.GenerateAccountNumber;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
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

// Como el servidor de mi api y el servidor del front-end estan en diferentes origenes (Front: http://localhost:5173, Back: http://localhost:8080)
// El navegador web por defecto bloqueará las solicitudes del front al back debido a las politicas de CORS
@CrossOrigin(origins = "http://localhost:5173") // Reemplaza con la URL de tu aplicación React ===>===>===>===>===>===>===>===>===>===>===>===>===>===>
@RequestMapping("/api/accounts")
public class AccountController {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository; // Repositorio para manejar operaciones CRUD de clientes.

    @GetMapping("/")
    // Maneja las solicitudes GET a la ruta base "/" para obtener todos los clientes.
    public List<AccountDTO> getAllAccounts() {
        return accountRepository.findAll().stream().map(account -> new AccountDTO(account)).collect(toList());

    }

    @GetMapping("/{id}")
    // Maneja las solicitudes GET para obtener un cliente por ID.
    public ResponseEntity<AccountDTO> getById(@PathVariable Long id) {
        return accountRepository.findById(id).map(account -> new AccountDTO(account)) // Convertir Client a ClientDTO
                .map(ResponseEntity::ok) // Si está presente, devolver 200 OK con el ClientDTO
                .orElse(ResponseEntity.notFound().build());
    }


    @GetMapping("/clients/current/accounts")
    public List<AccountDTO> getClientAccounts(Authentication authentication) {
        // Obtiene el cliente basado en el nombre de usuario autenticado.
        Client client = clientRepository.findByEmail(authentication.getName());

        // Retorna los detalles del cliente en la respuesta.
        return client.getAccounts().stream().map(account -> new AccountDTO(account)).collect(toList());
    }


    @PostMapping("/clients/current/accounts")
    public ResponseEntity <?> createClientAccounts(Authentication authentication) {
        // Obtiene el cliente basado en el nombre de usuario autenticado.
        Client client = clientRepository.findByEmail(authentication.getName());

        String accountNumber;
        boolean isUnique = false;

        do {
            accountNumber = GenerateAccountNumber.generateSerialNumber();
            Account account = accountRepository.findByNumber(accountNumber);

            // Si la cuenta no existe en la base de datos, es única
            if (account == null) {
                isUnique = true;
            }

        } while (!isUnique);

        if (client.getAccounts().toArray().length < 3) {
            LocalDateTime date = LocalDateTime.now();
            Account newAccount = new Account(accountNumber, date, 0 );
            newAccount.setClient(client);
            client.addAccount(newAccount);
            accountRepository.save(newAccount);
            // Retorna los detalles del cliente en la respuesta.
            return new ResponseEntity<>("Account created successfully", HttpStatus.CREATED);
        }

        return new ResponseEntity<>("You can't have more than 3 accounts", HttpStatus.FORBIDDEN);
    }

}
