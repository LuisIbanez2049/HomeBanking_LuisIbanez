package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.NewTransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientLoanService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
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
@RequestMapping("/api")
public class TransactionController {

    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;

    @Autowired
    private TransactionService transactionService;

    @Transactional
    @PostMapping("/transactions")
    public ResponseEntity<?> makeTransaction(Authentication authentication , @RequestBody NewTransactionDTO newTransactionDTO){
      try {
          Client client = clientService.getClientByEmail(authentication.getName());
          LocalDateTime dateNow = LocalDateTime.now();
          Account sourceAccount = accountService.getAccountByNumber(newTransactionDTO.sourceAccount());
          Account destinyAccount = accountService.getAccountByNumber(newTransactionDTO.destinyAccount());

          // Call makeValidations to check if there are any errors
          ResponseEntity<?> validationResult = transactionService.makeValidations(client, sourceAccount, newTransactionDTO);
          // If makeValidations returns an error, return it
          if (validationResult != null) {
              return validationResult;
          }
          Transaction newTransaction = new Transaction(TransactionType.DEBIT, newTransactionDTO.amount(),"Account debited for: ["+newTransactionDTO.description()+"]. Funds were transferred to the account: ["+newTransactionDTO.destinyAccount()+"]", dateNow);
          sourceAccount.addTransaction(newTransaction);
          transactionService.saveTransaction(newTransaction);
          double upDateBalanceSourceAccount = sourceAccount.getBalance() - newTransactionDTO.amount();
          sourceAccount.setBalance(upDateBalanceSourceAccount);

          Transaction destinyTransaction = new Transaction(TransactionType.CREDIT, newTransactionDTO.amount(), "Account credited for: ["+newTransactionDTO.description()+"]. Funds were transferred from the account: ["+newTransactionDTO.sourceAccount()+"]", dateNow);
          destinyAccount.addTransaction(destinyTransaction);
          transactionService.saveTransaction(destinyTransaction);
          double upDateBalanceDestinyAccount = destinyAccount.getBalance() + newTransactionDTO.amount();
          destinyAccount.setBalance(upDateBalanceDestinyAccount);

          return new ResponseEntity<>("Successful transaction",HttpStatus.CREATED);
      } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }

    }
}
