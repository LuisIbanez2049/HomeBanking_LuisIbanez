package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.models.Loan;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface LoanService {
    List<Loan> getAllLoans();
    List<LoanDTO> getAllLoansDTO();
    Loan getLoanById(Long id);
    LoanDTO getLoanDTO(Loan loan);
    void saveLoan(Loan loan);
    boolean authenticatedClientHasAvailableLoans(ClientDTO clientDTO);
    ResponseEntity<?> makeValidations(ClientDTO clientDTO, Client client, LoanApplicationDTO loanApplicationDTO, Loan loan, Account account);
    void associateNewClientLoan(ClientLoan newClientLoan, Client client, Loan loan);
    int interestRateAccordingCantOfInstallments(Integer installments);
    ResponseEntity<?> giveLoanToClient(Authentication authentication, LoanApplicationDTO loanApplicationDTO);
    void updateAuthenticatedClientAccount(LoanApplicationDTO loanApplicationDTO, Account destinyAccount, Loan loan);
    List<LoanDTO> availableCurrentClientLoans(ClientDTO clientDTO);
    ResponseEntity<?> getAvailableCurrentClientLoans(Authentication authentication);
    double applicatedInterest(LoanApplicationDTO loanApplicationDTO);
    String customAnswer(int interestRateAccordingCantOfInstallments);
    ResponseEntity<?> getCurrentClientLoansFunction(Authentication authentication);

}
