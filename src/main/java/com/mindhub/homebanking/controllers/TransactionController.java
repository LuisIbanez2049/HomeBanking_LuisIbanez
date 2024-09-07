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
        Client client = clientService.getClientByEmail(authentication.getName());
        LocalDateTime dateNow = LocalDateTime.now();
        //Account sourceAccount = accountRepository.findByNumber(newTransactionDTO.sourceAccount());
        Account sourceAccount = accountService.getAccountByNumber(newTransactionDTO.sourceAccount());
        if (sourceAccount == null) {
            return new ResponseEntity<>("Source account does not exist", HttpStatus.FORBIDDEN);
        }
        Account destinyAccount = accountService.getAccountByNumber(newTransactionDTO.destinyAccount());
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

    }
}
