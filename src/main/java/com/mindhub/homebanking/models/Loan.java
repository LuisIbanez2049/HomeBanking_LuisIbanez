package com.mindhub.homebanking.models;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private double maxAmount;

    @ElementCollection
    @Column(name = "payments")
    private List<Integer> payments = new ArrayList<>();


    @OneToMany(fetch = FetchType.EAGER)
    private List<ClientLoan> clientLoans = new ArrayList<>();


    //-------------------------Constructores-----------------------------------------------------
    public Loan (){ }

    public Loan(String name, double maxAmount, List<Integer> payments) {
        this.name = name;
        this.maxAmount = maxAmount;
        this.payments = payments;
    }
    //------------------------------------------------------------------------------------


    //--------------------------Getter y Setter----------------------------------------------
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getMaxAmount() {
        return maxAmount;
    }

    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }

    public List<Integer> getPayments() {
        return payments;
    }

    public void setPayments(List<Integer> payments) {
        this.payments = payments;
    }

    public Long getId() {
        return id;
    }
    //------------------------------------------------------------------------------------

    public void addClientLoan(ClientLoan clientLoan){
        this.clientLoans.add(clientLoan);
        clientLoan.setLoan(this);
    }

}
