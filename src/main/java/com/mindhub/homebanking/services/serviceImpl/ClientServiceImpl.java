package com.mindhub.homebanking.services.serviceImpl;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClienLoanRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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
        return getActiveClients().stream().map(activeClient -> new ClientDTO(activeClient)).collect(Collectors.toList());
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
}
