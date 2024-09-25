package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.ClientLoanDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import com.mindhub.homebanking.services.*;
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
    private LoanService loanService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private TransactionService transactionService;

    @GetMapping("/")
    public ResponseEntity<?> getAllAvailableLoansFromAuthenticatedClient(Authentication authentication){
        try {
            return loanService.getAvailableCurrentClientLoans(authentication);
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    @GetMapping("/currentClient")
    public  ResponseEntity<?> getCurrentClientLoans(Authentication authentication){
        try {
            return loanService.getCurrentClientLoansFunction(authentication);
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }
    @Transactional
    @PostMapping("/")
    public ResponseEntity<?> applyForALoan(Authentication authentication, @RequestBody LoanApplicationDTO loanApplicationDTO){
        try {
            return loanService.giveLoanToClient(authentication, loanApplicationDTO);
        } catch (Exception e){ return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }
}
