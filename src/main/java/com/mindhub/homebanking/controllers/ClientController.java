package com.mindhub.homebanking.controllers;



import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.Map;

import java.util.List;

@RestController // Indicamos que la clase actua como un controlador REST, es decir que va a manejar solicitudes HTTP siguiendo el protocolo REST
@RequestMapping("/api/clients") // Defino la ruta base de acceso a la que este controlador va a escuchar
                                // Le pongo "api" para seguir la conveción
public class ClientController {

    @Autowired // Indico que esta clase se va a conectar/cablear con "ClientRepository" para inyectar los metodos de "JpaRepository"
    private ClientRepository clientRepository;


    //-----------------------------SERVLET-------------------------------------------------------
    // Los servlets son los componentes del controlador que reciben una solicitud específica(la cual va a contener el tipo de metodo y la ruta)
    // y lo procesan para generar una respuesta
    //Este servlet en particular va a responder a la solicitud de tipo "get" con la ruta "/"


    @GetMapping("/") // Indico que este servlet va a recibir una petición con el método "get" asociado a la ruta "/"
    public List<Client> getAllClients(){
        return clientRepository.findAll();
    }
    //------------------------------------------------------------------------------------


    //------------------------------SERVLET------------------------------------------------------
    @GetMapping("/hello") // La ruta completa para invocar este método seria "http://localhost:8080/api/clients/hello"
    public String greet(){
        return "Hello Clients!!!!";
    }
    //------------------------------------------------------------------------------------

    //------------------------------SERVLET------------------------------------------------------
    @GetMapping("/{id}")
    public Client getClientById(@PathVariable Long id){ // Parametro de ruta que se pasa por la URL http para obtener un valor de la variable
        return clientRepository.findById(id).orElse(null);
    }
    //------------------------------------------------------------------------------------


    //------------------------------SERVLET------------------------------------------------------
    @PostMapping("/create") // Indico que este servlet va a resibir una petición de tipo "post" asociado a la ruta "/create"
    public Client createClient(@RequestParam String firstName, @RequestParam String lastName, @RequestParam String email){
        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        return clientRepository.save(client);
    }
    //------------------------------------------------------------------------------------


    //------------------------------SERVLET------------------------------------------------------
    @DeleteMapping("/{id}") // La ruta completa para invocar este método sería "http://localhost:8080/api/clients/{id}"
    public String deleteClientById(@PathVariable Long id) {
        // Verifico si el cliente con el ID dado existe
        if (clientRepository.existsById(id)) {
            // Si existe, elimino el cliente
            clientRepository.deleteById(id);
            return "Client with ID " + id + " was deleted.";
        } else {
            // Si no existe, devuelvo un mensaje de error
            return "Client with ID " + id + " not found.";
        }
    }
    //------------------------------------------------------------------------------------





    //------------------------------SERVLET------------------------------------------------------
    //crud para actualizar un cliente
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateClient(@PathVariable Long id, @RequestParam String firstName, @RequestParam String lastName, @RequestParam String email) {
        Client client = clientRepository.findById(id).orElse(null);

        if (client == null) {
            return new ResponseEntity<>("Client not found with id " + id, HttpStatus.NOT_FOUND);
        }
      client.setFirstName(firstName);
      client.setLastName(lastName);
      client.setEmail(email);

        Client updatedClient = clientRepository.save(client);
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------




    //------------------------------SERVLET------------------------------------------------------
    //ResponseEntity<Client> me devuelve el objeto y respuesta http con un codigo de estado
    @PatchMapping("/update/{id}")
    public ResponseEntity<?> partialUpdateClient(
            @PathVariable Long id,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email) {

        Client client = clientRepository.findById(id).orElse(null);

        if (client == null) {
            return new ResponseEntity<>("Client not found with id " + id, HttpStatus.NOT_FOUND);
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
        return new ResponseEntity<>(updatedClient, HttpStatus.OK);
    }
    //------------------------------------------------------------------------------------


}



