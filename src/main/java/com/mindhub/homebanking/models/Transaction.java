package com.mindhub.homebanking.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private double amount;
    private String description;
    private LocalDateTime dateTime = LocalDateTime.now();
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    //---------------------------------Relacion entre "Transaction" and "Account"-------------------
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;
    //---------------------------------------------------------------------------------------



    //---------------------Constructor-----------------------------------------------
    public Transaction() {}


    public Transaction(TransactionType type, double amount, String description, LocalDateTime dateTime) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.dateTime = dateTime;
    }
    //---------------------------------------------------------------------------------------


    //--------------Getter and Setter---------------------------------------------------------------
    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }
    //---------------------------------------------------------------------------------------


    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
