package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.NewTransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface TransactionService {
    void saveTransaction(Transaction transaction);
    ResponseEntity<?> makeValidations(Client client, Account sourceAccount, Account destinyAccount, NewTransactionDTO newTransactionDTO);
    void associateNewDebitTransaction(NewTransactionDTO newTransactionDTO, Account sourceAccount);
    void associateNewCreditTransaction(NewTransactionDTO newTransactionDTO, Account destinyAccount);
    ResponseEntity<?> authenticatedClientMakeTransaction(Authentication authentication, NewTransactionDTO newTransactionDTO);
}
