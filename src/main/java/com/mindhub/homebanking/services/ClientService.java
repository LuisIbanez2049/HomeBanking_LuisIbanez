package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.RegisterDTO;
import com.mindhub.homebanking.models.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ClientService {
    Client getAuthenticatedClientByEmail(Authentication authentication);
    List<Client> getAllClients();
    List<Client> getActiveClients();
    List<ClientDTO> getAllActiveClientDTO();
    Client getClientById(Long id);
    Client getClientByEmail(String email);
    ClientDTO getClientDTO(Client client);
    void saveClient(Client client);
    ResponseEntity<?> getAllClientsDTO();
    ResponseEntity<?> makeValidationsClientByID(Long id);
    ResponseEntity<?> getClientBYIDFunction(Long id);
    ResponseEntity<?> makeValidationsCreateClient(String firstName, String lastName, String email);
    Client createAnewClient(String firstName, String lastName, String email);
    ResponseEntity<?> createClientFunction(String firstName, String lastName, String email);
    ResponseEntity<?> deleteClientByIdFunction(Long id);
    ResponseEntity<?> upDateClientFunction(Long id, String firstName, String lastName, String email);
    ResponseEntity<?> partialUpdateClientFunction(Long id, String firstName, String lastName, String email);
    void updatePartialAttributes(Client client, String firstName, String lastName, String email);
}
