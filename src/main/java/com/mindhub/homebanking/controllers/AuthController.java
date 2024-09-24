package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.LoginDTO;
import com.mindhub.homebanking.dtos.RegisterDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.utils.GenerateAccountNumber;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.AuthControllerService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.servicesSecurity.JwtUtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder; // Codificador de contraseñas para encriptar y verificar contraseñas.

    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private AuthControllerService authControllerService;

    @Autowired
    private AuthenticationManager authenticationManager; // Administrador de autenticación para manejar el proceso de autenticación.

    @Autowired
    private UserDetailsService userDetailsService; // Servicio para cargar los detalles del usuario.

    @Autowired
    private JwtUtilService jwtUtilService; // Servicio para manejar la generación y validación de JWT (JSON Web Tokens).

    // Endpoint para iniciar sesión y generar un token JWT.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            System.out.println("Login attempt for: " + loginDTO.email()); // Registra el intento de inicio de sesión.
            if (loginDTO.email().isBlank()) {
                return new ResponseEntity<>("Please provide an email.", HttpStatus.BAD_REQUEST);
            }
            if (loginDTO.password().isBlank()) {
                return new ResponseEntity<>("Please enter the password.", HttpStatus.BAD_REQUEST);
            }
            // Autentica al usuario usando el email y la contraseña proporcionados.
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()));

            // Carga los detalles del usuario después de la autenticación.
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.email());

            // Genera un token JWT para el usuario autenticado.
            final String jwt = jwtUtilService.generateToken(userDetails);
            System.out.println("JWT generated: " + jwt); // Registra el token generado.
            String fullName = clientService.getClientByEmail(userDetails.getUsername()).getFirstName() + " " + clientService.getClientByEmail(userDetails.getUsername()).getLastName();
            String[] data = {jwt, fullName};
            // Retorna el token JWT en la respuesta.
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            e.printStackTrace(); // Muestra cualquier excepción que ocurra.
            // Retorna un error si la autenticación falla.
            return new ResponseEntity<>("Email or password invalid", HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para registrar un nuevo cliente.
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        try {
            return authControllerService.registerNewClient(registerDTO);
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    // Endpoint para obtener los detalles del cliente autenticado.
    @GetMapping("/current")
    public ResponseEntity<?> getClient(Authentication authentication) {
        try {
            Client client = clientService.getClientByEmail(authentication.getName());
            return ResponseEntity.ok(clientService.getClientDTO(client));
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }

    }

}
