package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.ClientService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<ClientDTO>> getAllActiveClients() {
        return new ResponseEntity<>(clientService.getAllActiveClientDTO(), HttpStatus.OK);
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
        if (clientService.getClientById(id) == null) {
            return new ResponseEntity<>("Client not found with id " + id, HttpStatus.NOT_FOUND);
        }
        if (!clientService.getClientById(id).isActive()) {
            return new ResponseEntity<>("The client with id " + id + " is no longer a client", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(clientService.getClientDTO(clientService.getClientById(id)), HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------



    //-----------------------------SERVLET-------------------------------------------------------
    //ResponseEntity<ClientDTO> me devuelve el objeto y respuesta http con un codigo de estado

    @PostMapping("/create")  // Indico que este servlet va a resibir una petición de tipo "post" asociado a la ruta "/create"
    public ResponseEntity<ClientDTO> createClient(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {
        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        // Guardo al cliente en la base de datos y me devuelve la entidad guardada con informacion adicional generada por la base de datos como la "id"
        //Client savedClient = clientRepository.save(client);
        clientService.saveClient(client);
        // Con ese "client" que guarde en la base de datos que ahora tiene una id, creo un ClientDTO
        //ClientDTO clientDTO = new ClientDTO(savedClient);
        return new ResponseEntity<>(clientService.getClientDTO(client), HttpStatus.CREATED);
    }
    //------------------------------------------------------------------------------------



    //-----------------------------SERVLET-------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClientById(@PathVariable Long id) {
        Client client = clientService.getClientById(id);

        if (client == null) {
            return new ResponseEntity<>("Client with ID " + id + " not found.", HttpStatus.NOT_FOUND);
        }

        client.setActive(false);
        clientService.saveClient(client);
        // Eliminar el cliente
        //clientRepository.delete(client);
        return new ResponseEntity<>("Client with ID " + id + " was deleted.", HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------



    //-----------------------------SERVLET-------------------------------------------------------
    @PutMapping("/update/{id}") // Con "PUT" indico que voy a modificar todas las propiedades
    public ResponseEntity<?> updateClient(
            @PathVariable Long id,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return new ResponseEntity<>("Client not found with id " + id, HttpStatus.NOT_FOUND);
        }
        if (!client.isActive()) {
            return new ResponseEntity<>("The client with id " + id + " is no longer a client", HttpStatus.NOT_FOUND);
        }
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        clientService.saveClient(client);
        //Client updatedClient = clientRepository.save(client);
        //ClientDTO clientDTO = new ClientDTO(updatedClient);
        return new ResponseEntity<>(clientService.getClientDTO(client), HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------



    //-----------------------------SERVLET-----------------------------------------------------------------------------------
    @PatchMapping("/update/{id}") // Con "PATCH" indico que voy a modificar una o algunas propiedades
    public ResponseEntity<?> partialUpdateClient(
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email) {
        Client client = clientService.getClientById(id);
        if (client == null) {
            return new ResponseEntity<>("Client not found with id " + id, HttpStatus.NOT_FOUND);
        }
        if (!client.isActive()) {
            return new ResponseEntity<>("The client with id " + id + " is no longer a client", HttpStatus.NOT_FOUND);
        }
        if (firstName != null) {
            client.setFirstName(firstName);
        }
        if (lastName != null) {
            client.setLastName(lastName);
        }
        if (email != null) {
            client.setEmail(email);
        }
        //Client updatedClient = clientRepository.save(client);
        //ClientDTO clientDTO = new ClientDTO(updatedClient);
        clientService.saveClient(client);
        return new ResponseEntity<>(clientService.getClientDTO(client), HttpStatus.OK);
    }

}




