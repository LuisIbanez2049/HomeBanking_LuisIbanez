package com.mindhub.homebanking.services.serviceImpl;

import com.mindhub.homebanking.models.ClientLoan;
import com.mindhub.homebanking.repositories.ClienLoanRepository;
import com.mindhub.homebanking.services.ClientLoanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientLoanServiceImpl implements ClientLoanService {

    @Autowired
    private ClienLoanRepository clienLoanRepository;
    @Override
    public void saveClientLoan(ClientLoan clientLoan) {
        clienLoanRepository.save(clientLoan);
    }
}
