package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.RegisterDTO;
import org.springframework.http.ResponseEntity;

public interface AuthControllerService {
    boolean existEmail(String email);
    boolean parameterIsBlank(String parameter);
    boolean passWordIsShort(String password);
    ResponseEntity<?> registerNewClient(RegisterDTO registerDTO);
}
