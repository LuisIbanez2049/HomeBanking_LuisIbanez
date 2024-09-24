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
        if (existEmail(registerDTO.email())) { return new ResponseEntity<>("Email not available. Already in use.", HttpStatus.BAD_REQUEST); }
        if (parameterIsBlank(registerDTO.firstName())) { return new ResponseEntity<>("First name can not be empty.", HttpStatus.BAD_REQUEST); }
        if (registerDTO.firstName().length() < 2) { return new ResponseEntity<>("Invalid first name. Please provide at least 2 characters.", HttpStatus.BAD_REQUEST);}
        if (parameterIsBlank(registerDTO.lastName())) { return new ResponseEntity<>("Last name can not be empty", HttpStatus.BAD_REQUEST);}
        if (registerDTO.lastName().length() < 2) { return new ResponseEntity<>("Invalid last name. Please provide at least 2 characters.", HttpStatus.BAD_REQUEST);}
        if (parameterIsBlank(registerDTO.email())) { return new ResponseEntity<>("Email can not be empty", HttpStatus.BAD_REQUEST); }
        if (!registerDTO.email().contains("@")) { return new ResponseEntity<>("Invalid email. It must contain an '@' character.", HttpStatus.BAD_REQUEST); }
        if (!registerDTO.email().contains(".com") && !registerDTO.email().contains(".net") && !registerDTO.email().contains(".org") &&
                !registerDTO.email().contains(".co") && !registerDTO.email().contains(".info")) {
            return new ResponseEntity<>("Invalid email. Please enter a valid domain extension since '.com', '.net', '.org', '.co' or '.info'.", HttpStatus.BAD_REQUEST); }
        if (registerDTO.email().contains("@.")) { return new ResponseEntity<>("Invalid email. Please provide a valid domain since 'gmail', 'yahoo', etc., " +
                "between the characters '@' and the character '.'", HttpStatus.BAD_REQUEST); }
        if (parameterIsBlank(registerDTO.password())) { return new ResponseEntity<>("Password can not be empty.", HttpStatus.BAD_REQUEST); }
        if (passWordIsShort(registerDTO.password())) { return new ResponseEntity<>("Password must be at least 8 characters long.", HttpStatus.BAD_REQUEST);}
        String encodedPassword = passwordEncoder.encode(registerDTO.password());
        Client newClient = new Client(registerDTO.firstName(), registerDTO.lastName(), registerDTO.email(), encodedPassword);
        clientService.saveClient(newClient);
        accountService.asociateNewAccountToClient(newClient);
        return new ResponseEntity<>("Client registered successfully", HttpStatus.CREATED);
    }
}
