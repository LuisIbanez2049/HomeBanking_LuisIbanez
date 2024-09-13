package com.mindhub.homebanking;

import com.mindhub.homebanking.dtos.NewTransactionDTO;
import com.mindhub.homebanking.services.TransactionService;
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

@SpringBootTest
public class TransactionServiceTests {
    @Autowired
    private TransactionService transactionService;

    @Test
    void testAuthenticatedClientMakeTransaction(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        Double amount = 666D;
        NewTransactionDTO newTransactionDTO = new NewTransactionDTO("VIN001", "VIN002", "CHUCHES", amount);
        ResponseEntity<?> response = transactionService.authenticatedClientMakeTransaction(authentication, newTransactionDTO);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }
}
