package com.mindhub.homebanking.services.serviceImpl;

import com.mindhub.homebanking.dtos.NewTransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.ClientService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.text.NumberFormat;
import java.util.Locale;

import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientService clientService;
    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public ResponseEntity<?> makeValidations(Client client, Account sourceAccount, Account destinyAccount, NewTransactionDTO newTransactionDTO) {
        if (newTransactionDTO.destinyAccount().isBlank()) {
            return new ResponseEntity<>("Destiny account must be specified.", HttpStatus.BAD_REQUEST);
        }
        if (destinyAccount == null) {
            return new ResponseEntity<>("Destiny account: "+newTransactionDTO.destinyAccount()+ " does not exist or you typed an space character which is forbidden.", HttpStatus.FORBIDDEN);
        }
        if (newTransactionDTO.sourceAccount().isBlank()) {
            return new ResponseEntity<>("Source account must be specified.", HttpStatus.BAD_REQUEST);
        }
        if (sourceAccount == null) {
            return new ResponseEntity<>("Source account: "+newTransactionDTO.sourceAccount()+ " does not exist or you typed an space character which is forbidden.", HttpStatus.FORBIDDEN);
        }
        if (client.getAccounts().stream().noneMatch(account -> account.getNumber().equals(newTransactionDTO.sourceAccount()))) {
            return new ResponseEntity<>("You do not have an account with number: "+newTransactionDTO.sourceAccount(), HttpStatus.FORBIDDEN);
        }
        if (newTransactionDTO.sourceAccount().equals(newTransactionDTO.destinyAccount())) {
            return new ResponseEntity<>("You can not make a transaction from an account to the same account.", HttpStatus.BAD_REQUEST);
        }
        if (newTransactionDTO.amount().isNaN()|| newTransactionDTO.amount() == 0) {
            return new ResponseEntity<>("Amount must be specified.", HttpStatus.BAD_REQUEST);
        }
        if (newTransactionDTO.amount() < 0) {
            return new ResponseEntity<>("Amount can not be negative.", HttpStatus.BAD_REQUEST);
        }
        if (newTransactionDTO.description().isBlank()) {
            return new ResponseEntity<>("Description must be specified.", HttpStatus.BAD_REQUEST);
        }

        if (sourceAccount.getBalance() < newTransactionDTO.amount()) {
            // Obtener una instancia de NumberFormat para formatear con separadores de miles
            NumberFormat formato = NumberFormat.getNumberInstance(Locale.US);

            // Imprimir el número con separadores de miles
            String numeroFormateado = formato.format(sourceAccount.getBalance());
            return new ResponseEntity<>("You don't have enough funds to carry out this transaction. Your balance is: $"+numeroFormateado, HttpStatus.BAD_REQUEST);
        }
        return null;
    }

    @Override
    public void associateNewDebitTransaction(NewTransactionDTO newTransactionDTO, Account sourceAccount) {
        Transaction newTransaction = new Transaction(TransactionType.DEBIT, newTransactionDTO.amount(),"Debited for '"+newTransactionDTO.description()+"'.\n Funds were transferred to the account '"+newTransactionDTO.destinyAccount()+"'", LocalDateTime.now());
        sourceAccount.addTransaction(newTransaction);
        saveTransaction(newTransaction);
        double upDateBalanceSourceAccount = sourceAccount.getBalance() - newTransactionDTO.amount();
        sourceAccount.setBalance(upDateBalanceSourceAccount);
    }

    @Override
    public void associateNewCreditTransaction(NewTransactionDTO newTransactionDTO, Account destinyAccount) {
        Transaction destinyTransaction = new Transaction(TransactionType.CREDIT, newTransactionDTO.amount(), "Credited for '"+newTransactionDTO.description()+"'.\n Funds were transferred from the account '"+newTransactionDTO.sourceAccount()+"'", LocalDateTime.now());
        destinyAccount.addTransaction(destinyTransaction);
        saveTransaction(destinyTransaction);
        double upDateBalanceDestinyAccount = destinyAccount.getBalance() + newTransactionDTO.amount();
        destinyAccount.setBalance(upDateBalanceDestinyAccount);
    }

    @Override
    public ResponseEntity<?> authenticatedClientMakeTransaction(Authentication authentication, NewTransactionDTO newTransactionDTO) {
        Client client = clientService.getClientByEmail(authentication.getName());
        Account sourceAccount = accountService.getAccountByNumber(newTransactionDTO.sourceAccount());
        Account destinyAccount = accountService.getAccountByNumber(newTransactionDTO.destinyAccount());
        // Call makeValidations to check if there are any errors
        ResponseEntity<?> validationResult = makeValidations(client, sourceAccount,destinyAccount, newTransactionDTO);
        // If makeValidations returns an error, return it
        if (validationResult != null) {
            return validationResult;
        }
        associateNewDebitTransaction(newTransactionDTO, sourceAccount);
        associateNewCreditTransaction(newTransactionDTO, destinyAccount);
        return new ResponseEntity<>("Successful transaction",HttpStatus.CREATED);
    }
}
