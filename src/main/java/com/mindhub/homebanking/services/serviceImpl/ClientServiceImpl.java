package com.mindhub.homebanking.services.serviceImpl;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.RegisterDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.utils.GenerateAccountNumber;
import com.mindhub.homebanking.repositories.ClienLoanRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    ClientRepository clientRepository;
    @Override
    public Client getAuthenticatedClientByEmail(Authentication authentication) {
        return getClientByEmail(authentication.getName());
    }

    @Override
    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    @Override
    public List<Client> getActiveClients() {
        return getAllClients().stream().filter(client -> client.isActive()).collect(Collectors.toList());
    }


    @Override
    public List<ClientDTO> getAllActiveClientDTO() {
        return getActiveClients().stream().map(activeClient -> getClientDTO(activeClient)).collect(Collectors.toList());
    }

    @Override
    public Client getClientById(Long id) {
        return clientRepository.findById(id).orElse(null);
    }

    @Override
    public Client getClientByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    @Override
    public ClientDTO getClientDTO(Client client) {
        return new ClientDTO(client);
    }

    @Override
    public void saveClient(Client client) {
        clientRepository.save(client);
    }

    @Override
    public ResponseEntity<?> getAllClientsDTO() {
        return new ResponseEntity<>(getAllActiveClientDTO(), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> makeValidationsClientByID(Long id) {
        if (getClientById(id) == null) {
            return new ResponseEntity<>("Client not found with id " + id, HttpStatus.NOT_FOUND);
        }
        if (!getClientById(id).isActive()) {
            return new ResponseEntity<>("The client with id " + id + " is no longer a client", HttpStatus.NOT_FOUND);
        }
        return null;
    }

    @Override
    public ResponseEntity<?> getClientBYIDFunction(Long id) {
        ResponseEntity<?> validationResult = makeValidationsClientByID(id);
        if (validationResult != null) {
            return validationResult;
        }
        return new ResponseEntity<>(getClientDTO(getClientById(id)), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> makeValidationsCreateClient(String firstName, String lastName, String email) {
        if (firstName.isBlank()) {
            return new ResponseEntity<>("First name must be specified", HttpStatus.BAD_REQUEST);
        }
        if (lastName.isBlank()) {
            return new ResponseEntity<>("Last name must be specified", HttpStatus.BAD_REQUEST);
        }
        if (email.isBlank()) {
            return new ResponseEntity<>("Email must be specified", HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @Override
    public Client createAnewClient(String firstName, String lastName, String email) {
        Client client = new Client();
        client.setFirstName(firstName);
        client.setLastName(lastName);
        client.setEmail(email);
        saveClient(client);
        return client;
    }

    @Override
    public ResponseEntity<?> createClientFunction(String firstName, String lastName, String email) {
        ResponseEntity<?> validationResult = makeValidationsCreateClient(firstName, lastName, email);
        if (validationResult != null) {
            return validationResult;
        }
        Client client = createAnewClient(firstName, lastName, email);
        return new ResponseEntity<>(getClientDTO(client), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> deleteClientByIdFunction(Long id) {
        Client client = getClientById(id);
        if (client == null) {
            return new ResponseEntity<>("Client with ID " + id + " not found.", HttpStatus.NOT_FOUND);
        }
        client.setActive(false);
        saveClient(client);
        return new ResponseEntity<>("Client with ID " + id + " was deleted.", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> upDateClientFunction(Long id, String firstName, String lastName, String email) {
        ResponseEntity<?> validationResult = makeValidationsCreateClient(firstName, lastName, email);
        if (validationResult != null) {
            return validationResult;
        }
        ResponseEntity<?> validationResult2 = makeValidationsClientByID(id);
        if (validationResult2 != null) {
            return validationResult2;
        }
        Client client = getClientById(id);
        updatePartialAttributes(client, firstName, lastName, email);
        saveClient(client);
        return new ResponseEntity<>(getClientDTO(client), HttpStatus.OK);
    }

    @Override
    public void updatePartialAttributes(Client client, String firstName, String lastName, String email) {
        if (firstName != null) {
            client.setFirstName(firstName);
        }
        if (lastName != null) {
            client.setLastName(lastName);
        }
        if (email != null) {
            client.setEmail(email);
        }
    }

    @Override
    public ResponseEntity<?> partialUpdateClientFunction(Long id, String firstName, String lastName, String email) {
        Client client = getClientById(id);
        ResponseEntity<?> validationResult = makeValidationsClientByID(id);
        if (validationResult != null) {
            return validationResult;
        }
        updatePartialAttributes(client, firstName, lastName, email);
        saveClient(client);
        return new ResponseEntity<>(getClientDTO(client), HttpStatus.OK);
    }
}
