package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController // Indicamos que la clase actua como un controlador REST, es decir que va a manejar solicitudes HTTP siguiendo el protocolo REST
@CrossOrigin(origins = "http://localhost:5173") // Reemplaza con la URL de tu aplicación React ===>===>===>===>===>===>===>===>===>===>===>===>===>===>
@RequestMapping("/api/clients") // Defino la ruta base de acceso a la que este controlador va a escuchar
                                // Le pongo "api" para seguir la conveción
public class ClientController {

    @Autowired // Indico que esta clase se va a conectar/cablear con "ClientRepository" para inyectar los metodos de "JpaRepository"
    private ClientRepository clientRepository;

    @Autowired
    private AccountRepository accountRepository;


    //-----------------------------SERVLET-------------------------------------------------------
    // Los servlets son los componentes del controlador que reciben una solicitud específica(la cual va a contener el tipo de metodo y la ruta)
    // y lo procesan para generar una respuesta
    //Este servlet en particular va a responder a la solicitud de tipo "get" con la ruta "/"

    @GetMapping("/")  // Indico que este servlet va a recibir una petición con el método "get" asociado a la ruta "/"
    public ResponseEntity<List<ClientDTO>> getAllActiveClients() {
        //List<Client> Me devuelve una lista de Client porque en "clientRepository" indique que trabaje con Client
        List<ClientDTO> clientDTOS = clientRepository.findAll()
                // Utilizo el método stream() para acceder a los metodos de orden superior. En este caso es "filter" y "map"
                .stream()
                // Filtro solo los clientes cuya propiedad "active" sea true  || Mediante el metodo "isActive()" accesdo al valor. Me devuleve un "Stream<Client>"
                .filter(client -> client.isActive())
                // Recorro cada cliente que este activo y por cada uno genero un ClientDTO. Me devuelve un Stream<ClientDTO>"
                .map(client -> new ClientDTO(client))
                // Agarro el Stream<Client> y lo convierto a una lista porque la variable "clientDTOS" es de tipo List
                .collect(Collectors.toList());
        return new ResponseEntity<>(clientDTOS, HttpStatus.OK);
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
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            return new ResponseEntity<>("Client not found with id " + id, HttpStatus.NOT_FOUND);
        }
        if (!client.isActive()) {
            return new ResponseEntity<>("The client with id " + id + " is no longer a client", HttpStatus.NOT_FOUND);
        }
        ClientDTO clientDTO = new ClientDTO(client);
        return new ResponseEntity<>(clientDTO, HttpStatus.OK);
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
        Client savedClient = clientRepository.save(client);
        // Con ese "client" que guarde en la base de datos que ahora tiene una id, creo un ClientDTO
        ClientDTO clientDTO = new ClientDTO(savedClient);
        return new ResponseEntity<>(clientDTO, HttpStatus.CREATED);
    }
    //------------------------------------------------------------------------------------



    //-----------------------------SERVLET-------------------------------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClientById(@PathVariable Long id) {
        Client client = clientRepository.findById(id).orElse(null);

        if (client == null) {
            return new ResponseEntity<>("Client with ID " + id + " not found.", HttpStatus.NOT_FOUND);
        }

        client.setActive(false);
        clientRepository.save(client);
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
        Client client = clientRepository.findById(id).orElse(null);
        if (client == null) {
            return new ResponseEntity<>("Client not found with id " + id, HttpStatus.NOT_FOUND);
        }
        if (!client.isActive()) {
            return new ResponseEntity<>("The client with id " + id + " is no longer a client", HttpStatus.NOT_FOUND);
        }
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        Client updatedClient = clientRepository.save(client);
        ClientDTO clientDTO = new ClientDTO(updatedClient);
        return new ResponseEntity<>(clientDTO, HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------



    //-----------------------------SERVLET-----------------------------------------------------------------------------------
    @PatchMapping("/update/{id}") // Con "PATCH" indico que voy a modificar una o algunas propiedades
    public ResponseEntity<?> partialUpdateClient(
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email) {
        Client client = clientRepository.findById(id).orElse(null);
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
        Client updatedClient = clientRepository.save(client);
        ClientDTO clientDTO = new ClientDTO(updatedClient);
        return new ResponseEntity<>(clientDTO, HttpStatus.OK);
    }
    //---------------------------------------------------------------------------------------------------------------------------

//    @GetMapping("/test")
//    public ResponseEntity<?> test (Authentication authentication){
//        String mail = authentication.getname
//    }

}




