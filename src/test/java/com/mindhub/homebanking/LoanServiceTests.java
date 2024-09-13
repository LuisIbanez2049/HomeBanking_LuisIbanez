package com.mindhub.homebanking;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;
import com.mindhub.homebanking.services.ClientLoanService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.LoanService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class LoanServiceTests {
    @Autowired
    LoanService loanService;
    @Autowired
    ClientService clientService;
    @Autowired
    ClientLoanService clientLoanService;
    @Test
    void testGetAllLoans(){
        List<Loan> loans = loanService.getAllLoans();
        assertThat(loans, is(notNullValue()));
    }
    @Test
    void testGetAllLoansDTO(){
        List<LoanDTO> loanDTOS = loanService.getAllLoansDTO();
        assertThat(loanDTOS, is(notNullValue()));
    }
    @Test
    void testGetLoanById(){
        Long id = 3L;
        Loan loan = loanService.getLoanById(id);
        assertThat(loan, is(notNullValue()));
    }
    @Test
    void testGetLoanById_NoExistId(){
        Long id = 4L;
        Loan loan = loanService.getLoanById(id);
        assertThat(loan, is(nullValue()));
    }
    @Test
    void testGetLoanDTO(){
        Long id = 1L;
        LoanDTO loanDTO = loanService.getLoanDTO(loanService.getLoanById(id));
        assertThat(loanDTO, is(notNullValue()));
    }
    @Test
    void testSaveLoan(){
        Loan loan = new Loan();
        loanService.saveLoan(loan);
        assertThat(loanService.getAllLoans(), hasSize(4));
    }
    @Test
    void testAuthenticatedClientHasAvailableLoans(){
        Long id = 1L;
        ClientDTO clientDTO = clientService.getClientDTO(clientService.getClientById(id)) ;
        assertThat(loanService.authenticatedClientHasAvailableLoans(clientDTO), is(true));
    }
    @Test
    void testAuthenticatedClientHasNotAvailableLoans(){
        Long id = 1L;
        Long idLoan = 3L;
        Loan loan = loanService.getLoanById(idLoan);
        Client client = clientService.getClientById(id);
        ClientLoan newClientLoan = new ClientLoan(400000,60);
        client.addClientLoan(newClientLoan);
        loan.addClientLoan(newClientLoan);
        clientLoanService.saveClientLoan(newClientLoan);
        ClientDTO clientDTO = clientService.getClientDTO(client) ;
        assertThat(loanService.authenticatedClientHasAvailableLoans(clientDTO), is(false));
    }
    @Test
    void testGiveLoanToClient_6(){
//        Client client = new Client("luis", "luisS","luis@mindhub.com", "123456789" );
//        clientService.saveClient(client);
//        String destinyAccount = client.getAccounts().stream().map(account -> account.getNumber()).toString();
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        Long id = 3L;
        Double amount = 300000D;
        Integer installments = 6;
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(id, amount, installments, "VIN001");
        ResponseEntity<?> response = loanService.giveLoanToClient(authentication,loanApplicationDTO );
        System.out.println(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }
    @Test
    void testGiveLoanToClient_12(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        Long id = 3L;
        Double amount = 300000D;
        Integer installments = 12;
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(id, amount, installments, "VIN001");
        ResponseEntity<?> response = loanService.giveLoanToClient(authentication,loanApplicationDTO );
        System.out.println(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }
    @Test
    void testGiveLoanToClient_MoreThan12(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        Long id = 3L;
        Double amount = 300000D;
        Integer installments = 36;
        LoanApplicationDTO loanApplicationDTO = new LoanApplicationDTO(id, amount, installments, "VIN001");
        ResponseEntity<?> response = loanService.giveLoanToClient(authentication,loanApplicationDTO );
        System.out.println(response.getBody());
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
    }
}
