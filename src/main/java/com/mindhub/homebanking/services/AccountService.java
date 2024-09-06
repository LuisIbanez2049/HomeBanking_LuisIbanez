package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.models.Account;

import java.util.List;

public interface AccountService {
    List<Account> getAllAccounts();
    List<AccountDTO> getAllAccountDTO();
    Account getAccountById(Long id);
    Account getAccountByNumber(String number);
    AccountDTO getAccountDTO(Account account);
    void saveAccount(Account account);
}
