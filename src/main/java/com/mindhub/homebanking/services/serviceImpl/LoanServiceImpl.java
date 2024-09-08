package com.mindhub.homebanking.services.serviceImpl;

import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.LoanApplicationDTO;
import com.mindhub.homebanking.dtos.LoanDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.LoanRepository;
import com.mindhub.homebanking.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LoanServiceImpl implements LoanService {

    @Autowired
    private LoanRepository loanRepository;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ClientLoanService clientLoanService;
    @Autowired
    private ClientService clientService;
    @Autowired
    private TransactionService transactionService;

    @Override
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Override
    public List<LoanDTO> getAllLoansDTO() {
        return getAllLoans().stream().map(loan -> new LoanDTO(loan)).collect(Collectors.toList());
    }

    @Override
    public Loan getLoanById(Long id) {
        return loanRepository.findById(id).orElse(null);
    }

    @Override
    public LoanDTO getLoanDTO(Loan loan) {
        return new LoanDTO(loan);
    }

    @Override
    public void saveLoan(Loan loan) {
        loanRepository.save(loan);
    }

    @Override
    public boolean authenticatedClientHasAvailableLoans(ClientDTO clientDTO) {
        List<LoanDTO> allLoans = getAllLoansDTO();
        List<LoanDTO> availableLoans = allLoans.stream().filter(eachLoan -> clientDTO.getLoans().stream().noneMatch(loanClient -> loanClient.getLoanId().equals(eachLoan.getId()))).collect(Collectors.toList());
        return !availableLoans.isEmpty();
    }

    @Override
    public ResponseEntity<?> makeValidations(ClientDTO clientDTO, Client client, LoanApplicationDTO loanApplicationDTO, Loan loan, Account destinyAccount) {
        if (!authenticatedClientHasAvailableLoans(clientDTO)) {
            return new ResponseEntity<>("You have already applied for all the loans available on the platform!", HttpStatus.NOT_FOUND);
        }
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
        if (loan.getPayments().stream().noneMatch(payment -> payment.equals(loanApplicationDTO.installment()))) {
            return new ResponseEntity<>("Installment ["+loanApplicationDTO.installment()+"] is not a available", HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.destinyAccount().isBlank()) {
            return new ResponseEntity<>("Destiny account must be specified",HttpStatus.BAD_REQUEST);
        }
        //Account destinyAccount = accountService.getAccountByNumber(loanApplicationDTO.destinyAccount());
        if (destinyAccount == null) {
            return new ResponseEntity<>("Destiny account with number: "+loanApplicationDTO.destinyAccount()+ " does not exist or you typed an space character which is forbidden", HttpStatus.FORBIDDEN);
        }
        if (client.getAccounts().stream().noneMatch(account -> account.getNumber().equals(loanApplicationDTO.destinyAccount()))) {
            return new ResponseEntity<>("You do not have an account with number: "+loanApplicationDTO.destinyAccount(), HttpStatus.FORBIDDEN);
        }
        return null;
    }

    @Override
    public void associateNewClientLoan(ClientLoan newClientLoan, Client client, Loan loan) {
        //ClientLoan newClientLoan = new ClientLoan(loanApplicationDTO.amount(), loanApplicationDTO.installment());
        client.addClientLoan(newClientLoan);
        loan.addClientLoan(newClientLoan);
        clientLoanService.saveClientLoan(newClientLoan);
    }

    @Override
    public int interestRateAccordingCantOfInstallments(Integer installments) {
        int interestRate;
        if (installments < 12) {
            return interestRate = 15;
        } else if (installments == 12) {
            return interestRate = 20;
        } else return interestRate = 25;
    }

    @Override
    public ResponseEntity<?> giveLoanToClient(Authentication authentication, LoanApplicationDTO loanApplicationDTO) {
        Client client = clientService.getClientByEmail(authentication.getName());
        Loan loan = getLoanById(loanApplicationDTO.id());
        ClientDTO clientDTO = clientService.getClientDTO(client);
        List<LoanDTO> allLoans = getAllLoansDTO();
        Account destinyAccount = accountService.getAccountByNumber(loanApplicationDTO.destinyAccount());
        // Call makeValidations to check if there are any errors
        ResponseEntity<?> validationResult = makeValidations(clientDTO, client, loanApplicationDTO, loan, destinyAccount);
        // If makeValidations returns an error, return it
        if (validationResult != null) {
            return validationResult;
        }
        //return makeValidations(clientDTO, client, loanApplicationDTO, loan, destinyAccount);
        ClientLoan newClientLoan = new ClientLoan(loanApplicationDTO.amount(), loanApplicationDTO.installment());
        associateNewClientLoan(newClientLoan, client, loan);
        double interest = (loanApplicationDTO.amount() * interestRateAccordingCantOfInstallments(loanApplicationDTO.installment())) / 100;
        double upDateBalanceDestinyAccount = destinyAccount.getBalance() + loanApplicationDTO.amount() + interest;
        Transaction transaction = new Transaction(TransactionType.CREDIT, upDateBalanceDestinyAccount, "Account credited for: ["+loan.getName()+"] loan || Amount: "+loanApplicationDTO.amount()+" || Interest rate "+interestRateAccordingCantOfInstallments(loanApplicationDTO.installment())+"%: "+interest, LocalDateTime.now());
        destinyAccount.addTransaction(transaction);
        transactionService.saveTransaction(transaction);
        destinyAccount.setBalance(upDateBalanceDestinyAccount);
        return new ResponseEntity<>(loan.getName()+" loan approved", HttpStatus.CREATED);
    }
}
