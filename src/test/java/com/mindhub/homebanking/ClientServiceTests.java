//package com.mindhub.homebanking;
//
//import com.mindhub.homebanking.dtos.ClientDTO;
//import com.mindhub.homebanking.models.Client;
//import com.mindhub.homebanking.repositories.ClientRepository;
//import com.mindhub.homebanking.services.serviceImpl.ClientServiceImpl;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.core.Authentication;
//import org.springframework.test.util.ReflectionTestUtils;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.Matchers.*;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@SpringBootTest
//public class ClientServiceTests {
//    @Mock
//    private ClientRepository clientRepository;
//
//    @InjectMocks
//    private ClientServiceImpl clientService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this); // Inicializa los mocks
//    }
//
//    @Test
//    void testGetAuthenticatedClientByEmail() {
//        Authentication auth = mock(Authentication.class);
//        when(auth.getName()).thenReturn("test@example.com");
//        Client client = new Client();
//        client.setEmail("test@example.com");
//        when(clientRepository.findByEmail("test@example.com")).thenReturn(client);
//
//        Client result = clientService.getAuthenticatedClientByEmail(auth);
//        assertEquals("test@example.com", result.getEmail());
//    }
//
//    @Test
//    void testGetAllClients() {
//        List<Client> clients = new ArrayList<>();
//        clients.add(new Client());
//        when(clientRepository.findAll()).thenReturn(clients);
//
//        List<Client> result = clientService.getAllClients();
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    void testGetActiveClients() {
//        Client client1 = new Client();
//        client1.setActive(true);
//        Client client2 = new Client();
//        client2.setActive(false);
//        List<Client> clients = List.of(client1, client2);
//        when(clientRepository.findAll()).thenReturn(clients);
//
//        List<Client> result = clientService.getActiveClients();
//        assertEquals(1, result.size());
//        assertTrue(result.get(0).isActive());
//    }
//
//    @Test
//    public void testGetClientById() {
//        Client client = new Client();
//        client.setFirstName("John");
//        // Establecer el id usando ReflectionTestUtils
//        ReflectionTestUtils.setField(client, "id", 1L);
//
//        // Simular la respuesta del repositorio
//        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
//
//        // Llamar al método del servicio y verificar el resultado
//        Client retrievedClient = clientService.getClientById(1L);
//        assertEquals("John", retrievedClient.getFirstName());
//        assertEquals(1L, retrievedClient.getId());  // Verifica que el id se estableció correctamente
//    }
//
//    @Test
//    void testGetClientByEmail() {
//        Client client = new Client();
//        client.setEmail("test@example.com");
//        when(clientRepository.findByEmail("test@example.com")).thenReturn(client);
//
//        Client result = clientService.getClientByEmail("test@example.com");
//        assertEquals("test@example.com", result.getEmail());
//    }
//
//    @Test
//    void testGetClientDTO() {
//        Client client = new Client();
//        client.setFirstName("John");
//        ClientDTO clientDTO = clientService.getClientDTO(client);
//        assertEquals("John", clientDTO.getFirstName());
//    }
//
//    @Test
//    void testSaveClient() {
//        Client client = new Client();
//        clientService.saveClient(client);
//        verify(clientRepository, times(1)).save(client);
//    }
//
//    @Test
//    void testDeleteClientByIdFunction() {
//        Client client = new Client();
//        ReflectionTestUtils.setField(client, "id", 1L);
//        //client.setId(1L);
//        client.setActive(true);
//        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
//
//        ResponseEntity<?> response = clientService.deleteClientByIdFunction(1L);
//        assertEquals("Client with ID 1 was deleted.", response.getBody());
//        assertFalse(client.isActive());
//    }
//
//@Test
//void testUpDateClientFunction() {
//    Client client = new Client();
//    ReflectionTestUtils.setField(client, "id", 1L);
//
//    when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
//
//    // Ejecutar el método que se está probando
//    ResponseEntity<?> response = clientService.upDateClientFunction(1L, "New Name", "Last Name", "email@example.com");
//
//    // Verificar que el cuerpo de la respuesta es un ClientDTO
//    ClientDTO updatedClient = (ClientDTO) response.getBody();
//
//    // Validar los cambios
//    assertEquals("New Name", updatedClient.getFirstName());
//    assertEquals("Last Name", updatedClient.getLastName());
//    assertEquals("email@example.com", updatedClient.getEmail());
//}
//
//    @Test
//    void testCreateClientFunction() {
//        ResponseEntity<?> response = clientService.createClientFunction("First", "Last", "email@example.com");
//        ClientDTO clientDTO = (ClientDTO) response.getBody();
//        assertEquals("First", clientDTO.getFirstName());
//        assertEquals(HttpStatus.CREATED, response.getStatusCode());
//    }
//
//    @Test
//    void testPartialUpdateClientFunction() {
//        Client client = new Client();
//        ReflectionTestUtils.setField(client, "id", 1L);
//        //client.setId(1L);
//        when(clientRepository.findById(1L)).thenReturn(Optional.of(client));
//
//        ResponseEntity<?> response = clientService.partialUpdateClientFunction(1L, "UpdatedFirstName", null, null);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        ClientDTO updatedClient = (ClientDTO) response.getBody();
//        assertEquals("UpdatedFirstName", updatedClient.getFirstName());
//    }
//
//}
