package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;

public class ClientLoanDTO {

    private Long id;
    private Long loanId;
    private int payments;
    private String name;
    private double amount;


    public ClientLoanDTO(ClientLoan clientLoan) {
        this.id = clientLoan.getId();
        this.loanId = clientLoan.getLoan().getId();
        this.payments = clientLoan.getPayments();
        this.name = clientLoan.getLoan().getName();
        this.amount = clientLoan.getAmount();
    }

    public Long getId() {
        return id;
    }

    public Long getLoanId() {
        return loanId;
    }

    public int getPayments() {
        return payments;
    }

    public String getName() {
        return name;
    }

    public double getAmount() {
        return amount;
    }
}
