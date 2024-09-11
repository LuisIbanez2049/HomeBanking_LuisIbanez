package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // Indicamos que la clase actua como un controlador REST, es decir que va a manejar solicitudes HTTP siguiendo el protocolo REST
@RequestMapping("/api/clients") // Defino la ruta base de acceso a la que este controlador va a escuchar
                                // Le pongo "api" para seguir la conveción
public class ClientController {

    //@Autowired // Indico que esta clase se va a conectar/cablear con "ClientRepository" para inyectar los metodos de "JpaRepository"
    //private ClientRepository clientRepository;
    @Autowired
    private ClientService clientService;

    //-----------------------------SERVLET-------------------------------------------------------
    // Los servlets son los componentes del controlador que reciben una solicitud específica(la cual va a contener el tipo de metodo y la ruta)
    // y lo procesan para generar una respuesta
    //Este servlet en particular va a responder a la solicitud de tipo "get" con la ruta "/"

    @GetMapping("/")  // Indico que este servlet va a recibir una petición con el método "get" asociado a la ruta "/"
    public ResponseEntity<?> getAllActiveClients() {
        try {
            return clientService.getAllClientsDTO();
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    //------------------------------------------------------------------------------------

    //-----------------------------SERVLET-------------------------------------------------------
    @GetMapping("/hello")  // La ruta completa para invocar este método seria "http://localhost:8080/api/clients/hello"
    public ResponseEntity<String> greet() {
        return new ResponseEntity<>("Hello Clients!!!!", HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------

    //-----------------------------SERVLET-------------------------------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> getClientById(@PathVariable Long id) { // con "@PathVariable" indico que va a recibir por paramtro a traves de la ruta
                                                                    // un valor que va a ser variable
        try {
            return clientService.getClientBYIDFunction(id);
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }

    }
    //------------------------------------------------------------------------------------

    //-----------------------------SERVLET-------------------------------------------------------
    @PostMapping("/create")  // Indico que este servlet va a resibir una petición de tipo "post" asociado a la ruta "/create"
    public ResponseEntity<?> createClient(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {
        try {
            return clientService.createClientFunction(firstName, lastName, email);
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }

    }
    //------------------------------------------------------------------------------------

    //-----------------------------SERVLET-------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteClientById(@PathVariable Long id) {
        try {
            return clientService.deleteClientByIdFunction(id);
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    //------------------------------------------------------------------------------------

    //-----------------------------SERVLET-------------------------------------------------------
    @PutMapping("/update/{id}") // Con "PUT" indico que voy a modificar todas las propiedades
    public ResponseEntity<?> updateClient(
            @PathVariable Long id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email) {
        try {
            return clientService.upDateClientFunction(id, firstName, lastName, email);
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }

    }
    //------------------------------------------------------------------------------------

    //-----------------------------SERVLET-----------------------------------------------------------------------------------
    @PatchMapping("/update/{id}") // Con "PATCH" indico que voy a modificar una o algunas propiedades
    public ResponseEntity<?> partialUpdateClient(
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email) {
        try {
            return clientService.partialUpdateClientFunction(id, firstName, lastName, email);
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }

    }

}




