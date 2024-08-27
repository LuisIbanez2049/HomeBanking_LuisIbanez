package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.LoginDTO;
import com.mindhub.homebanking.dtos.RegisterDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
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

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private PasswordEncoder passwordEncoder; // Codificador de contraseñas para encriptar y verificar contraseñas.

    @Autowired
    private ClientRepository clientRepository; // Repositorio para manejar operaciones CRUD de clientes.

    @Autowired
    private AccountRepository accountRepository; // Repositorio para manejar operaciones CRUD de cuentas.

    @Autowired
    private AuthenticationManager authenticationManager; // Administrador de autenticación para manejar el proceso de autenticación.

    @Autowired
    private UserDetailsService userDetailsService; // Servicio para cargar los detalles del usuario.

    @Autowired
    private JwtUtilService jwtUtilService; // Servicio para manejar la generación y validación de JWT (JSON Web Tokens).

    static private int num = 007; // Contador para generar identificadores únicos para las cuentas.

    // Endpoint para iniciar sesión y generar un token JWT.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO) {
        try {
            System.out.println("Login attempt for: " + loginDTO.email()); // Registra el intento de inicio de sesión.

            // Autentica al usuario usando el email y la contraseña proporcionados.
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password()));

            // Carga los detalles del usuario después de la autenticación.
            final UserDetails userDetails = userDetailsService.loadUserByUsername(loginDTO.email());

            // Genera un token JWT para el usuario autenticado.
            final String jwt = jwtUtilService.generateToken(userDetails);
            System.out.println("JWT generated: " + jwt); // Registra el token generado.

            // Retorna el token JWT en la respuesta.
            return ResponseEntity.ok(jwt);
        } catch (Exception e) {
            e.printStackTrace(); // Muestra cualquier excepción que ocurra.
            // Retorna un error si la autenticación falla.
            return new ResponseEntity<>("Email or password invalid", HttpStatus.BAD_REQUEST);
        }
    }

    // Endpoint para registrar un nuevo cliente.
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {
        // Verifica si el email ya está registrado en la base de datos.
        if (clientRepository.findByEmail(registerDTO.email()) != null) {
            return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
        }

        // Verifica si el nombre y apellido no están vacíos.
        if (registerDTO.firstName().isBlank() || registerDTO.lastName().isBlank()) {
            return new ResponseEntity<>("First name and last name cannot be empty", HttpStatus.BAD_REQUEST);
        }

        // Verifica si la contraseña cumple con el requisito mínimo de longitud.
        if (registerDTO.password().length() < 8) {
            return new ResponseEntity<>("Password must be at least 8 characters long", HttpStatus.BAD_REQUEST);
        }

        // Codifica la contraseña antes de almacenarla.
        String encodedPassword = passwordEncoder.encode(registerDTO.password());

        // Crea un nuevo cliente con la información proporcionada.
        Client newClient = new Client(registerDTO.firstName(), registerDTO.lastName(), registerDTO.email(), encodedPassword);

        // Guarda el cliente y convierte a DTO.
        ClientDTO clientDTO = new ClientDTO(clientRepository.save(newClient));

        // Crea una nueva cuenta para el cliente.
        Account account = new Account("VIN-" + String.valueOf(this.num), LocalDateTime.now(), 0);
        this.num += 1;

        // Asocia la cuenta al cliente.
        newClient.addAccount(account);
        // Guarda la cuenta y convierte a DTO.
        AccountDTO accountDTO = new AccountDTO(accountRepository.save(account));

        // Guarda el nuevo cliente en la base de datos.
        clientRepository.save(newClient);

        // Retorna una respuesta exitosa con un mensaje de confirmación.
        return new ResponseEntity<>("Client registered successfully", HttpStatus.CREATED);
    }

    // Endpoint para obtener los detalles del cliente autenticado.
    @GetMapping("/current")
    public ResponseEntity<?> getClient(Authentication authentication) {
        // Obtiene el cliente basado en el nombre de usuario autenticado.
        Client client = clientRepository.findByEmail(authentication.getName());

        // Retorna los detalles del cliente en la respuesta.
        return ResponseEntity.ok(new ClientDTO(client));
    }

}
