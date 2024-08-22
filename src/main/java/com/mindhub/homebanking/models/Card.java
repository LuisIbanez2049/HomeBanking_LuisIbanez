package com.mindhub.homebanking.models;

import com.mindhub.homebanking.models.utils.GenerateRandomNumber;
import jakarta.persistence.*;
import org.hibernate.grammars.hql.HqlParser;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Random;

@Entity
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String cardHolader;

    @Enumerated(EnumType.STRING)
    private CardType type;

    @Enumerated(EnumType.STRING)
    private CardColor color;

    private String number;
    private int cvv = new Random().nextInt((999 - 100) + 1) + 100;
    private LocalDate fromDate;
    private LocalDate thruDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id")
    private Client client;


    public Card(){ }

    public Card(CardType type, CardColor color, LocalDate fromDate, LocalDate thruDate) {
        this.type = type;
        this.color = color;
        this.fromDate = fromDate;
        this.thruDate = thruDate;
    }

    //--------------------------------------------------Getters and Setters--------------------------------------------------
    public String getCardHolader() {
        return cardHolader;
    }

    public void setCardHolader(String cardHolader) {
        this.cardHolader = cardHolader;
    }

    public CardType getType() {
        return type;
    }

    public void setType(CardType type) {
        this.type = type;
    }

    public CardColor getColor() {
        return color;
    }

    public void setColor(CardColor color) {
        this.color = color;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getCvv() {
        return cvv;
    }

    public void setCvv(int cvv) {
        this.cvv = cvv;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getThruDate() {
        return thruDate;
    }

    public void setThruDate(LocalDate thruDate) {
        this.thruDate = thruDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
        this.cardHolader = client.getFirstName() + " " + client.getLastName();
        this.number = GenerateRandomNumber.generateNumberCard();
    }

    public Long getId() {
        return id;
    }
    //-------------------------------------------------------------------------------------------------
}
