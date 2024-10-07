package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.NewTransactionDTO;
import com.mindhub.homebanking.dtos.PaymentRecordDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/external")
@CrossOrigin("*")
public class ExternalPayment {
    @Autowired
    private ClientService clientService;
    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private TransactionService transactionService;

    @Transactional
    @PostMapping("/payment")
    public ResponseEntity<?> makePayment(@RequestBody PaymentRecordDTO paymentRecordDTO){
        Card cardClient = cardRepository.findByNumber(paymentRecordDTO.cardNumberClient());
        if (cardClient == null) {
            return new ResponseEntity<>("Client card not found", HttpStatus.BAD_REQUEST);
        }
        Client client = cardClient.getClient();
        if (client == null) {
            return new ResponseEntity<>("Client not found", HttpStatus.NOT_FOUND);
        }


        Account restaurantAccount = accountService.getAccountByNumber(paymentRecordDTO.accountNumberRestaurant());
        if (restaurantAccount == null) {
            return new ResponseEntity<>("Restaurant account not found", HttpStatus.BAD_REQUEST);
        }

        Optional<Account> clientAccountOpt = client.getAccounts()
                .stream()
                .filter(account -> account.getBalance() > paymentRecordDTO.totalAmount())
                .findFirst();

        if (!clientAccountOpt.isPresent()) {
            return new ResponseEntity<>("Client account not found or insufficient balance", HttpStatus.BAD_REQUEST);
        }

        Account clientAccount = clientAccountOpt.get();


            Transaction newTransaction = new Transaction(TransactionType.DEBIT, paymentRecordDTO.totalAmount(),"Debited for 'VOYAGGER RESTAURANT'", LocalDateTime.now());
            clientAccount.addTransaction(newTransaction);
            transactionService.saveTransaction(newTransaction);
            double upDateBalanceClientAccount = clientAccount.getBalance() - paymentRecordDTO.totalAmount();
            clientAccount.setBalance(upDateBalanceClientAccount);



            Transaction destinyTransaction = new Transaction(TransactionType.CREDIT, paymentRecordDTO.totalAmount(), "Credited for 'CLIENT MAKE AN ORDER'", LocalDateTime.now());
            restaurantAccount.addTransaction(destinyTransaction);
            transactionService.saveTransaction(destinyTransaction);
            double upDateBalanceDestinyAccount = restaurantAccount.getBalance() + paymentRecordDTO.totalAmount();
            restaurantAccount.setBalance(upDateBalanceDestinyAccount);


        return new ResponseEntity<>("PAYMENT SUCCESSFUL!", HttpStatus.OK);
    }
}
