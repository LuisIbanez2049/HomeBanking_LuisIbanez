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

}
