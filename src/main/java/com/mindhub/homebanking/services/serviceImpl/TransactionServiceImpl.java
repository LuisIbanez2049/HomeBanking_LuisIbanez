package com.mindhub.homebanking.services.serviceImpl;

import com.mindhub.homebanking.dtos.NewTransactionDTO;
import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.repositories.TransactionRepository;
import com.mindhub.homebanking.services.AccountService;
import com.mindhub.homebanking.services.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private AccountService accountService;
    @Override
    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    @Override
    public ResponseEntity<?> makeValidations(Client client, Account sourceAccount, NewTransactionDTO newTransactionDTO) {
        if (sourceAccount == null) {
            return new ResponseEntity<>("Source account: "+newTransactionDTO.sourceAccount()+ " does not exist or you typed an space character which is forbidden", HttpStatus.FORBIDDEN);
        }
        Account destinyAccount = accountService.getAccountByNumber(newTransactionDTO.destinyAccount());
        if (destinyAccount == null) {
            return new ResponseEntity<>("Destiny account: "+newTransactionDTO.destinyAccount()+ " does not exist or you typed an space character which is forbidden", HttpStatus.FORBIDDEN);
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
        return null;
    }

}
