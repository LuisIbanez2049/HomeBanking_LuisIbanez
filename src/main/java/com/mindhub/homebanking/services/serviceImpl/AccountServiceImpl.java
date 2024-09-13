package com.mindhub.homebanking.services.serviceImpl;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.utils.GenerateAccountNumber;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ClientService clientService;

    @Override
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }
    @Override
    public Account getAccountById(Long id) {
        return accountRepository.findById(id).orElse(null);
    }

    @Override
    public boolean existAccountById(Long id) {
        return getAccountById(id) != null;
    }

    @Override
    public List<AccountDTO> getAllAccountDTO() {
        return getAllAccounts().stream().map(account -> getAccountDTO(account)).collect(Collectors.toList());
    }

    @Override
    public List<AccountDTO> getAllAccountDTOfromAuthenticationClient(Authentication authentication) {
        return clientService.getAuthenticatedClientByEmail(authentication).getAccounts().stream().map(account -> getAccountDTO(account)).toList();
    }


    @Override
    public Account getAccountByNumber(String number) {
        return accountRepository.findByNumber(number);
    }

    @Override
    public AccountDTO getAccountDTO(Account account) {
        return new AccountDTO(account);
    }

    @Override
    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    @Override
    public ResponseEntity<?> obtainAccountById(Long id) {
        if (existAccountById(id)) {
            return new ResponseEntity<>(getAccountDTO(getAccountById(id)), HttpStatus.OK);
        }
        return new ResponseEntity<>("Account not found with id " + id, HttpStatus.NOT_FOUND);
    }

    @Override
    public String randomAccountNumber() {
        String accountNumber;
        do {
            accountNumber = GenerateAccountNumber.generateSerialNumber();
        } while (getAccountByNumber(accountNumber) != null);
        return accountNumber;
    }

    @Override
    public boolean authenticatedClientHasLessThan3Accounts(Client authenticatedClient) {
        return authenticatedClient.getAccounts().size() < 3;
    }

    @Override
    public void asociateNewAccountToClient(Client authenticatedClient) {
        Account newAccount = new Account(randomAccountNumber(), LocalDateTime.now(), 0);
        newAccount.setClient(authenticatedClient);
        authenticatedClient.addAccount(newAccount);
        saveAccount(newAccount);
    }

    @Override
    public ResponseEntity<?> createAccountForAuthenticatedClient(Authentication authentication) {
        Client authenticatedClient = clientService.getAuthenticatedClientByEmail(authentication);
        if (authenticatedClientHasLessThan3Accounts(authenticatedClient)) {
            asociateNewAccountToClient(authenticatedClient);
            return new ResponseEntity<>("Account created successfully", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("You can't have more than 3 accounts", HttpStatus.FORBIDDEN);
    }
}
