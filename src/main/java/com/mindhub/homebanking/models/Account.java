package com.mindhub.homebanking.models;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String number;
    private LocalDateTime creationDate;
    private double balance;

    //---------------------------------Relacion entre "Account" and "Client"-----------------------------------------------------------
    // Indico que esta clase va a tener una relacion de muchos a uno con "client", osea muchas cuentas van a pertenecer a un cliente
    // Con "FetchType.EAGER" indico que cuando solicite una cuenta, este va a venir automaticamente junto con el cliente que tiene asocidado
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id") // Indio que va a crear una columna en la tabla con el nombre "owner_id"
    private Client client;

    //----------------------------Relacion entre "Account" and "transaction"-----------------------------------------
    // Con "FetchType.EAGER" indico que cuando solicite una cuenta, este va a venir automaticamente junto con el set de transacciones que tiene asociado
    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
    Set<Transaction> transactions = new HashSet<>();
    //-----------------------------------------------------------------------------------------------------------------------------------



    //----------------------------------------Metodos Constructores--------------------------
    public Account(){}

    public Account(String number, LocalDateTime creationDate, double balance) {
        this.number = number;
        this.creationDate = creationDate;
        this.balance = balance;
    }
    //---------------------------------------------------------------------------------------


    //---------------------------------Metodos Getter y Setter------------------------------------------
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }
    //------------------------------------------------------------------------------------



    //------------------------------------------------------------------------------------
    public void addTransaction(Transaction transaction){
        transaction.setAccount(this);
        this.transactions.add(transaction);
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", number='" + number + '\'' +
                ", creationDate=" + creationDate +
                ", balance=" + balance +
                ", transactions=" + transactions +
                '}';
    }
}
