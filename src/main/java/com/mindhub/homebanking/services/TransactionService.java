package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.NewTransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import org.springframework.http.ResponseEntity;

public interface TransactionService {
    void saveTransaction(Transaction transaction);
    ResponseEntity<?> makeValidations(Client client, Account sourceAccount, NewTransactionDTO newTransactionDTO);
}
