package com.mindhub.homebanking;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest
public class AccountServiceTests {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @Test
    void testGetAllAccounts() {
        List<Account> result = accountRepository.findAll();
        assertThat(result, hasSize(4));  // Uso del matcher `hasSize` para verificar el tamaño de la lista
    }
    @Test
    void testGetAccountById() {
        Long id = 1L;
        Account account = accountService.getAccountById(id);

        // Verifica que la cuenta no sea nula antes de realizar la comparación
        assertThat(account, is(notNullValue()));

        // Verifica que el ID de la cuenta sea el esperado
        assertThat(account.getId(), is(id));
    }
    @Test
    void testExistAccountById(){
        Long id = 1L;
        boolean exist = false;
        Account account = accountService.getAccountById(id);
        if (account != null) {
            exist = true;
        }
        assertThat(exist,is(true) );
    }
    @Test
    void testNotExistAccountById(){
        Long id = 5L;
        boolean exist = false;
        Account account = accountService.getAccountById(id);
        if (account != null) {
            exist = true;
        }
        assertThat(exist,is(false) );
    }
    @Test
    void testGetAllAccountDTO(){
        List<AccountDTO> listOfAccountDTO = accountService.getAllAccountDTO();
        assertThat(listOfAccountDTO, is(notNullValue()));
    }
    @Test
    void testGetAccountByNumber(){
        assertThat("VIN001", is(accountService.getAccountByNumber("VIN001").getNumber()));
    }
    @Test
    void testGetAccountDTO(){
        Long id = 1L;
        AccountDTO accountDTO = accountService.getAccountDTO(accountService.getAccountById(id));
        assertThat(accountDTO, is(notNullValue()));
    }
    @Test
    void testSaveAccount(){
        Account account = new Account("VIN005", LocalDateTime.now(), 0);
        accountService.saveAccount(account);
        assertThat(accountService.getAccountByNumber("VIN005"), is(notNullValue()));
    }
    @Test
    void testObteinAccountByIdFunction(){
        Long id = 1L;
        ResponseEntity<?> response = accountService.obtainAccountById(id);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
    }
    @Test
    void testObteinAccountByIdFunction_AccountDoesNotExist(){
        Long nonExistentId = 5L;
        ResponseEntity<?> response = accountService.obtainAccountById(nonExistentId);
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        String expectedMessage = "Account not found with id " + nonExistentId;
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testUniqueRandomAccountNumber(){
        Account account = accountService.getAccountByNumber(accountService.randomAccountNumber());
        assertThat(account, is(nullValue()));
    }
    @Test
    void testAuthenticatedClientHasLessThan3Accounts(){
        Long id = 1L;
        boolean result = accountService.authenticatedClientHasLessThan3Accounts(clientService.getClientById(id));
        assertThat(result, is(true));
    }
    @Test
    void testAuthenticatedClientHasLessThan3Accounts_HasMoreThan3Accounts(){
        Long id = 1L;
        Account account = new Account();
        account.setClient(clientService.getClientById(id));
        clientService.getClientById(id).addAccount(account);
        accountService.saveAccount(account);

        boolean result = accountService.authenticatedClientHasLessThan3Accounts(clientService.getClientById(id));
        assertThat(result, is(false));
    }
    @Test
    void testAssociateNewAccountToClient(){
        Long id = 1L;
        Client client = clientService.getClientById(id);
        accountService.asociateNewAccountToClient(client);
        assertThat(client.getAccounts(), hasSize(3));
    }
    @Test
    void testCreateAccountForAuthenticatedClient(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        ResponseEntity<?> response = accountService.createAccountForAuthenticatedClient(authentication);
        String expectedMessage = "Account created successfully";
        assertThat(response.getBody(), is(expectedMessage));
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }
    @Test
    void testCreateAccountForAuthenticatedClient_ClientHasNotLessThan3accounts(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        Long id = 1L;
        Client client = clientService.getClientById(id);
        accountService.asociateNewAccountToClient(client);
        ResponseEntity<?> response = accountService.createAccountForAuthenticatedClient(authentication);
        String expectedMessage = "You can't have more than 3 accounts";
        assertThat(response.getBody(), is(expectedMessage));
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
    }

}
