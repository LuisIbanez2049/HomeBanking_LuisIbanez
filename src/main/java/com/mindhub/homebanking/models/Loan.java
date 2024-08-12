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

    //-----------------------------------------------------------------------------
    // Con @ElementCollection Spring automaticamente me hace la relacion de uno a muchos
    // Uso @ElementCollection cuendo quiero relacionar una clase con una coleccion de elementos simples que van a estar en una tabla secundaria en la base de datos
    @ElementCollection // Indico que la coleccion "payments" es una lista de valores simples que se almacenara en una tabla secundaria
    @Column(name = "payments") // Indico que los valores de "payments" se almacenaran en un columna llamada "payments" en la tabla secundaria
    private List<Integer> payments = new ArrayList<>(); // Declaro e inicializo la propiedad "payments" que va a ser una colleccion de tipo List<Integer>
    //-----------------------------------------------------------------------------

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

    public List<ClientLoan> getClientLoans() {
        return clientLoans;
    }
    //------------------------------------------------------------------------------------

    public void addClientLoan(ClientLoan clientLoan){
        this.clientLoans.add(clientLoan);
        clientLoan.setLoan(this);
    }

}
