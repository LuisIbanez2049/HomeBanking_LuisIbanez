package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface AccountService {
    List<Account> getAllAccounts();
    List<AccountDTO> getAllAccountDTO();
    List<AccountDTO> getAllAccountDTOfromAuthenticationClient(Authentication authentication);
    Account getAccountById(Long id);
    Account getAccountByNumber(String number);
    AccountDTO getAccountDTO(Account account);
    void saveAccount(Account account);
    boolean existAccountById(Long id);
    ResponseEntity<?> obtainAccountById(Long id);
    String randomAccountNumber();
    boolean authenticatedClientHasLessThan3Accounts(Client authenticatedClient);
    void asociateNewAccountToClient(Client authenticatedClient);
    ResponseEntity<?> createAccountForAuthenticatedClient(Authentication authentication);
}
