package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.NewTransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private ClientRepository clientRepository; // Repositorio para manejar operaciones CRUD de clientes.

    @Autowired
    private TransactionRepository transactionRepository;

    @Transactional
    @PostMapping("/clients/current/transaction")
    public ResponseEntity<?> makeTransaction(Authentication authentication , @RequestBody NewTransactionDTO newTransactionDTO){
        Client client = clientRepository.findByEmail(authentication.getName());
        LocalDateTime dateNow = LocalDateTime.now();

        Account sourceAccount = accountRepository.findByNumber(newTransactionDTO.sourceAccount());
        if (sourceAccount == null) {
            return new ResponseEntity<>("Source account does not exist", HttpStatus.FORBIDDEN);
        }
        Account destinyAccount = accountRepository.findByNumber(newTransactionDTO.destinyAccount());
        if (destinyAccount == null) {
            return new ResponseEntity<>("Destiny account does not exist", HttpStatus.FORBIDDEN);
        }
        if (client.getAccounts().stream().noneMatch(account -> account.getNumber().equals(newTransactionDTO.sourceAccount()))) {
            return new ResponseEntity<>("You do not have an account with number: "+newTransactionDTO.sourceAccount(), HttpStatus.FORBIDDEN);
        }
        if (newTransactionDTO.sourceAccount().equals(newTransactionDTO.destinyAccount())) {
            return new ResponseEntity<>("You can not make a transaction from an account to the same account", HttpStatus.BAD_REQUEST);
        }
        if (newTransactionDTO.sourceAccount().isBlank()) {
            return new ResponseEntity<>("Source account must be specified", HttpStatus.BAD_REQUEST);
        }
        if (newTransactionDTO.destinyAccount().isBlank()) {
            return new ResponseEntity<>("Destiny account must be specified", HttpStatus.BAD_REQUEST);
        }
        if (newTransactionDTO.description().isBlank()) {
            return new ResponseEntity<>("Description must be specified", HttpStatus.BAD_REQUEST);
        }

        if (newTransactionDTO.amount().isNaN() || newTransactionDTO.amount() == 0) {
            return new ResponseEntity<>("Amount must be specified", HttpStatus.BAD_REQUEST);
        }
        if (newTransactionDTO.amount() < 0) {
            return new ResponseEntity<>("Amount can not be negative", HttpStatus.BAD_REQUEST);
        }

        if (sourceAccount.getBalance() < newTransactionDTO.amount()) {
            return new ResponseEntity<>("You don't have enough funds to carry out this transaction. Your balance is: "+sourceAccount.getBalance(), HttpStatus.BAD_REQUEST);
        }

        if (sourceAccount != null && destinyAccount != null) {
            // Transaction transaction1MelbaAccount1 = new Transaction(TransactionType.CREDIT, 2000, "Rent", dateNow);
            Transaction newTransaction = new Transaction(TransactionType.DEBIT, newTransactionDTO.amount(), newTransactionDTO.description(), dateNow);
            sourceAccount.addTransaction(newTransaction);
            transactionRepository.save(newTransaction);
            double upDateBalanceSourceAccount = sourceAccount.getBalance() - newTransactionDTO.amount();
            sourceAccount.setBalance(upDateBalanceSourceAccount);

            Transaction destinyTransaction = new Transaction(TransactionType.CREDIT, newTransactionDTO.amount(), newTransactionDTO.description(), dateNow);
            destinyAccount.addTransaction(destinyTransaction);
            transactionRepository.save(destinyTransaction);
            double upDateBalanceDestinyAccount = destinyAccount.getBalance() + newTransactionDTO.amount();
            destinyAccount.setBalance(upDateBalanceDestinyAccount);

            return new ResponseEntity<>("Successful transaction",HttpStatus.CREATED);
        }


        return new ResponseEntity<>("INTERNAL SERVER ERROR", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
