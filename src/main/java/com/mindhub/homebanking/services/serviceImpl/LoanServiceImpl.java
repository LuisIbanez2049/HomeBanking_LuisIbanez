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

import java.text.NumberFormat;
import java.util.Locale;

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
        if (loanApplicationDTO.destinyAccount().isBlank()) {
            return new ResponseEntity<>("Destiny account must be specified",HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.installment() == null || loanApplicationDTO.installment() == 0) {
            return new ResponseEntity<>("Installments must be specified",HttpStatus.BAD_REQUEST);
        }
        if (loanApplicationDTO.installment() < 0) {
            return new ResponseEntity<>("Installments can not be negative",HttpStatus.BAD_REQUEST);
        }
        if (loan.getPayments().stream().noneMatch(payment -> payment.equals(loanApplicationDTO.installment()))) {
            return new ResponseEntity<>("Installment ["+loanApplicationDTO.installment()+"] is not available", HttpStatus.BAD_REQUEST);
        }
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
    public String customAnswer(int interestRateAccordingCantOfInstallments) {
        if (interestRateAccordingCantOfInstallments < 12) {
            return "due to you selected less than 12 installments";
        } else if (interestRateAccordingCantOfInstallments == 12) {
            return "due to you selected 12 installments";
        }
        return "due to you selected more than 12 installments";
    }

    @Override
    public double applicatedInterest(LoanApplicationDTO loanApplicationDTO) {
        return (loanApplicationDTO.amount() * interestRateAccordingCantOfInstallments(loanApplicationDTO.installment())) / 100;
    }

    @Override
    public void updateAuthenticatedClientAccount(LoanApplicationDTO loanApplicationDTO, Account destinyAccount, Loan loan) {
        double interest = (loanApplicationDTO.amount() * interestRateAccordingCantOfInstallments(loanApplicationDTO.installment())) / 100;
        double upDateBalanceDestinyAccount = destinyAccount.getBalance() + loanApplicationDTO.amount();
        Transaction transaction = new Transaction(TransactionType.CREDIT, loanApplicationDTO.amount(), "Credited for '"+loan.getName()+"' loan", LocalDateTime.now());
        destinyAccount.addTransaction(transaction);
        transactionService.saveTransaction(transaction);
        destinyAccount.setBalance(upDateBalanceDestinyAccount);
    }

    @Override
    public List<LoanDTO> availableCurrentClientLoans(ClientDTO clientDTO) {
        List<LoanDTO> allLoans = getAllLoansDTO();
        return allLoans.stream().filter(loan -> clientDTO.getLoans().stream().noneMatch(loanClient -> loanClient.getLoanId().equals(loan.getId()))).collect(Collectors.toList());
    }

    @Override
    public ResponseEntity<?> getAvailableCurrentClientLoans(Authentication authentication) {
        List<LoanDTO> availableLoans = availableCurrentClientLoans(clientService.getClientDTO(clientService.getClientByEmail(authentication.getName())));
        if (availableLoans.isEmpty()) {
            return new ResponseEntity<>("You have already applied for all the loans available on the platform! Currently, there are no other options for you at this time", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(availableLoans,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> giveLoanToClient(Authentication authentication, LoanApplicationDTO loanApplicationDTO) {
        Client client = clientService.getClientByEmail(authentication.getName());
        Loan loan = getLoanById(loanApplicationDTO.id());
        ClientDTO clientDTO = clientService.getClientDTO(client);
        Account destinyAccount = accountService.getAccountByNumber(loanApplicationDTO.destinyAccount());
        // Call makeValidations to check if there are any errors
        ResponseEntity<?> validationResult = makeValidations(clientDTO, client, loanApplicationDTO, loan, destinyAccount);
        // If makeValidations returns an error, return it
        if (validationResult != null) {
            return validationResult;
        }
        ClientLoan newClientLoan = new ClientLoan(applicatedInterest(loanApplicationDTO) + loanApplicationDTO.amount(), loanApplicationDTO.installment());
        associateNewClientLoan(newClientLoan, client, loan);
        updateAuthenticatedClientAccount(loanApplicationDTO, destinyAccount, loan);
        double number = loanApplicationDTO.amount()+applicatedInterest(loanApplicationDTO);
        double numberInterest = applicatedInterest(loanApplicationDTO);
        double numberRequiredAmount = loanApplicationDTO.amount();
        // Obtener una instancia de NumberFormat para formatear con separadores de miles
        NumberFormat formato = NumberFormat.getNumberInstance(Locale.US);

        // Imprimir el número con separadores de miles
        String numeroFormateado = formato.format(number);
        String interest = formato.format(numberInterest);
        String requiredAmount = formato.format((numberRequiredAmount));
        String description ="Required Amount: $"+requiredAmount+" \nYou must pay an interest rate of "+
                interestRateAccordingCantOfInstallments(loanApplicationDTO.installment())+"% "+customAnswer(loanApplicationDTO.installment()) +
                "\nInterest equivalent to: $"+interest+"\nTotal to pay: $"+ numeroFormateado;

        newClientLoan.setDescription(description);
        return new ResponseEntity<>(loan.getName().toUpperCase()+" LOAN APPROVED", HttpStatus.CREATED);
//        return new ResponseEntity<>(loan.getName()+" loan approved"+"\nRequired Amount: $"+requiredAmount+" \nYou must pay an interest rate of "+
//                interestRateAccordingCantOfInstallments(loanApplicationDTO.installment())+"% "+customAnswer(loanApplicationDTO.installment()) +
//                "\nInterest equivalent to: $"+interest+"\nTotal to pay: $"+ numeroFormateado, HttpStatus.CREATED);
//        return new ResponseEntity<>(loan.getName()+" loan approved"+"\nRequired Amount: $"+loanApplicationDTO.amount()+" \nYou must pay an interest rate of "+
//                interestRateAccordingCantOfInstallments(loanApplicationDTO.installment())+"% "+customAnswer(loanApplicationDTO.installment()) +
//              //  "\nInterest equivalent to: $"+applicatedInterest(loanApplicationDTO)+"\nTotal to pay: $"+ (loanApplicationDTO.amount()+applicatedInterest(loanApplicationDTO)), HttpStatus.CREATED);
//                "\nInterest equivalent to: $"+applicatedInterest(loanApplicationDTO)+"\nTotal to pay: $"+ numeroFormateado, HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<?> getCurrentClientLoansFunction(Authentication authentication) {
        Client client = clientService.getClientByEmail(authentication.getName());
        ClientDTO clientDTO = clientService.getClientDTO(client);
        if (clientDTO.getLoans().isEmpty()) {
            return new ResponseEntity<>("YOU DON'T HAVE APPLIED LOANS", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(clientDTO.getLoans(), HttpStatus.OK);
    }
}
