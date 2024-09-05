package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private ClienLoanRepository clienLoanRepository;
    @Autowired
    TransactionRepository transactionRepository;

    @GetMapping("/")
    public List<LoanDTO> getAllLoans(){
        return loanRepository.findAll().stream().map(loan -> new LoanDTO(loan)).collect(toList());
    }

    @Transactional
    @PostMapping("/")
    public ResponseEntity<?> getALoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplicationDTO){
        // Obtiene el cliente basado en el nombre de usuario autenticado.
        Client client = clientRepository.findByEmail(authentication.getName());
        Loan loan = loanRepository.findById(loanApplicationDTO.id()).orElse(null);
        ClientDTO clientDTO = new ClientDTO(client);

        if (loan == null) {
            return new ResponseEntity<>("Loan not found with id " + loanApplicationDTO.id(), HttpStatus.NOT_FOUND);
        }
        if (clientDTO.getLoans().stream().anyMatch(eachLoan -> eachLoan.getLoanId().equals(loanApplicationDTO.id()))) {
            return new ResponseEntity<>("You already have a loan with the name ["+loan.getName()+"] and the id ["+loanApplicationDTO.id()+"]",HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.amount() == 0) {
            return new ResponseEntity<>("Amount must be specified",HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.amount() < 0) {
            return new ResponseEntity<>("Amount can not be negative",HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.amount() > loan.getMaxAmount()) {
            return new ResponseEntity<>("You can not apply for a loan greater than: "+loan.getMaxAmount(),HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.installment() == null || loanApplicationDTO.installment() == 0) {
            return new ResponseEntity<>("Installments must be specified",HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.installment() < 0) {
            return new ResponseEntity<>("Installments can not be negative",HttpStatus.BAD_REQUEST);
        }
        // noneMatch me dice que si no hay esa coincidencia me devuelve un "tru" y entra al condiciconal
        if (loan.getPayments().stream().noneMatch(payment -> payment.equals(loanApplicationDTO.installment()))) {
            return new ResponseEntity<>("Installment ["+loanApplicationDTO.installment()+"] is not a available", HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.destinyAccount().isBlank()) {
            return new ResponseEntity<>("Destiny account must be specified",HttpStatus.BAD_REQUEST);
        }
        Account destinyAccount = accountRepository.findByNumber(loanApplicationDTO.destinyAccount());
        if (destinyAccount == null) {
            return new ResponseEntity<>("Destiny account with number: "+loanApplicationDTO.destinyAccount()+ "does not exist or you typed an space character which is forbidden", HttpStatus.FORBIDDEN);
        }
        if (client.getAccounts().stream().noneMatch(account -> account.getNumber().equals(loanApplicationDTO.destinyAccount()))) {
            return new ResponseEntity<>("You do not have an account with number: "+loanApplicationDTO.destinyAccount(), HttpStatus.FORBIDDEN);
        }
        ClientLoan newClientLoan = new ClientLoan(loanApplicationDTO.amount(), loanApplicationDTO.installment());
        client.addClientLoan(newClientLoan);
        loan.addClientLoan(newClientLoan);
        clienLoanRepository.save(newClientLoan);
        int interestRate;
        if (loanApplicationDTO.installment() < 12) {
            interestRate = 15;
        } else if (loanApplicationDTO.installment() == 12) {
            interestRate = 20;
        } else interestRate = 25;
        LocalDateTime dateNow = LocalDateTime.now();
        double interest = (loanApplicationDTO.amount() * interestRate) / 100;
        double upDateBalanceDestinyAccount = destinyAccount.getBalance() + loanApplicationDTO.amount() + interest;
        Transaction transaction = new Transaction(TransactionType.CREDIT, upDateBalanceDestinyAccount, "Account credited for: ["+loan.getName()+"] loan || Amount: "+loanApplicationDTO.amount()+" || Interest rate "+interestRate+"%: "+interest, dateNow);
        destinyAccount.addTransaction(transaction);
        transactionRepository.save(transaction);
        destinyAccount.setBalance(upDateBalanceDestinyAccount);


        return new ResponseEntity<>(loan.getName()+" loan approved", HttpStatus.CREATED);
    }
}
