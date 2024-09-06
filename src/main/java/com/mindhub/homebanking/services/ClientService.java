package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;

import java.util.List;

public interface ClientService {
    List<Client> getAllClients();
    List<Client> getActiveClients();
    List<ClientDTO> getAllActiveClientDTO();
    Client getClientById(Long id);
    Client getClientByEmail(String email);
    ClientDTO getClientDTO(Client client);
    void saveClient(Client client);
}
