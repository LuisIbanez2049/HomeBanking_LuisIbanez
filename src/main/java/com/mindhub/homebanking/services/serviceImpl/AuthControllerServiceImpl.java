package com.mindhub.homebanking.services.serviceImpl;

import com.mindhub.homebanking.dtos.RegisterDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.AuthControllerService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthControllerServiceImpl implements AuthControllerService {

    @Autowired
    PasswordEncoder passwordEncoder; // Codificador de contraseñas para encriptar y verificar contraseñas.
    @Autowired
    AccountService accountService;
    @Autowired
    private ClientService clientService;


    @Override
    public boolean existEmail(String email) {
        return clientService.getClientByEmail(email) != null;
    }

    @Override
    public boolean parameterIsBlank(String parameter) {
        return parameter.isBlank();
    }

    @Override
    public boolean passWordIsShort(String password) {
        return password.length() < 8;
    }

    @Override
    public ResponseEntity<?> registerNewClient(RegisterDTO registerDTO) {
        if (existEmail(registerDTO.email())) { return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST); }
        if (parameterIsBlank(registerDTO.firstName())) { return new ResponseEntity<>("First name cannot be empty", HttpStatus.BAD_REQUEST); }
        if (parameterIsBlank(registerDTO.lastName())) { return new ResponseEntity<>("Last name cannot be empty", HttpStatus.BAD_REQUEST);}
        if (parameterIsBlank(registerDTO.email())) { return new ResponseEntity<>("Email cannot be empty", HttpStatus.BAD_REQUEST); }
        if (parameterIsBlank(registerDTO.password())) { return new ResponseEntity<>("Password cannot be empty", HttpStatus.BAD_REQUEST); }
        if (passWordIsShort(registerDTO.password())) { return new ResponseEntity<>("Password must be at least 8 characters long", HttpStatus.BAD_REQUEST);}
        String encodedPassword = passwordEncoder.encode(registerDTO.password());
        Client newClient = new Client(registerDTO.firstName(), registerDTO.lastName(), registerDTO.email(), encodedPassword);
        clientService.saveClient(newClient);
        accountService.asociateNewAccountToClient(newClient);
        return new ResponseEntity<>("Client registered successfully", HttpStatus.CREATED);
    }
}
