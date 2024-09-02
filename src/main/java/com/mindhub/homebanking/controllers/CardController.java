package com.mindhub.homebanking.controllers;

import com.mindhub.homebanking.dtos.AccountDTO;
import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.ClientDTO;
import com.mindhub.homebanking.dtos.NewCardDTO;
import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.models.utils.GenerateAccountNumber;
import com.mindhub.homebanking.models.utils.GenerateCvvNumber;
import com.mindhub.homebanking.models.utils.GenerateRandomNumber;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cards")
public class CardController {

    @Autowired
    private ClientRepository clientRepository; // Repositorio para manejar operaciones CRUD de clientes.

    @Autowired
    private CardRepository cardRepository;

    @GetMapping("/clients/current/cards")
    public List<CardDTO> getClient(Authentication authentication) {
        // Obtiene el cliente basado en el nombre de usuario autenticado.
        Client client = clientRepository.findByEmail(authentication.getName());

        // Retorna los detalles del cliente en la respuesta.
        return client.getCards().stream().map(card -> new CardDTO(card)).collect(Collectors.toList());
    }

    @PostMapping("/clients/current/cards")
    public ResponseEntity<?> createCardForCurrentClient (Authentication authentication,@RequestBody NewCardDTO newCardDTO){
        Client client = clientRepository.findByEmail(authentication.getName());
        LocalDate date = LocalDate.now();

        // Verifica si el nombre y apellido no están vacíos.
        if (newCardDTO.type().isBlank()) {
            return new ResponseEntity<>("Card type must be specified", HttpStatus.BAD_REQUEST);
        }
        // Verifica si el nombre y apellido no están vacíos.
        if (newCardDTO.color().isBlank()) {
            return new ResponseEntity<>("Card color must be specified", HttpStatus.BAD_REQUEST);
        }
        //-------------------------------------------------------------------------------------
        if (newCardDTO.type().toLowerCase().equals("debit")) {
            if (client.getCards().stream().filter(card -> card.getType().equals(CardType.DEBIT)).count() == 3) {
                return new ResponseEntity<>("You cannot have more than 3 DEBIT CARDS", HttpStatus.FORBIDDEN);
            }

        }
        if (newCardDTO.type().toLowerCase().equals("credit")) {
            if (client.getCards().stream().filter(card -> card.getType().equals(CardType.CREDIT)).count() == 3) {
                return new ResponseEntity<>("You cannot have more than 3 CREDIT CARDS", HttpStatus.FORBIDDEN);
            }

        }

        if (newCardDTO.type().toLowerCase().equals("debit") && newCardDTO.color().equalsIgnoreCase("gold")) {
            if (client.getCards().stream().anyMatch(card -> card.getType().equals(CardType.DEBIT) &&
                    card.getColor().equals(CardColor.GOLD) )) {
                return new ResponseEntity<>("You already have an "+newCardDTO.color().toUpperCase()+ " "+ newCardDTO.type().toUpperCase()+" card", HttpStatus.BAD_REQUEST);
            }
        } else if (newCardDTO.type().toLowerCase().equals("debit") && newCardDTO.color().equalsIgnoreCase("silver")) {
            if (client.getCards().stream().anyMatch(card -> card.getType().equals(CardType.DEBIT) &&
                    card.getColor().equals(CardColor.SILVER) )) {
                return new ResponseEntity<>("You already have an "+newCardDTO.color().toUpperCase()+ " "+ newCardDTO.type().toUpperCase()+" card", HttpStatus.BAD_REQUEST);
            }
        }else if (newCardDTO.type().toLowerCase().equals("debit") && newCardDTO.color().equalsIgnoreCase("titanium")) {
            if (client.getCards().stream().anyMatch(card -> card.getType().equals(CardType.DEBIT) &&
                    card.getColor().equals(CardColor.TITANIUM) )) {
                return new ResponseEntity<>("You already have an "+newCardDTO.color().toUpperCase()+ " "+ newCardDTO.type().toUpperCase()+" card", HttpStatus.BAD_REQUEST);
            }
        }

        if (newCardDTO.type().toLowerCase().equals("credit") && newCardDTO.color().equalsIgnoreCase("gold")) {
            if (client.getCards().stream().anyMatch(card -> card.getType().equals(CardType.CREDIT) &&
                    card.getColor().equals(CardColor.GOLD) )) {
                return new ResponseEntity<>("You already have an "+newCardDTO.color().toUpperCase()+ " "+ newCardDTO.type().toUpperCase()+" card", HttpStatus.BAD_REQUEST);
            }
        } else if (newCardDTO.type().toLowerCase().equals("credit") && newCardDTO.color().equalsIgnoreCase("silver")) {
            if (client.getCards().stream().anyMatch(card -> card.getType().equals(CardType.CREDIT) &&
                    card.getColor().equals(CardColor.SILVER) )) {
                return new ResponseEntity<>("You already have an "+newCardDTO.color().toUpperCase()+ " "+ newCardDTO.type().toUpperCase()+" card", HttpStatus.BAD_REQUEST);
            }
        }else if (newCardDTO.type().toLowerCase().equals("credit") && newCardDTO.color().equalsIgnoreCase("titanium")) {
            if (client.getCards().stream().anyMatch(card -> card.getType().equals(CardType.CREDIT) &&
                    card.getColor().equals(CardColor.TITANIUM) )) {
                return new ResponseEntity<>("You already have an "+newCardDTO.color().toUpperCase()+ " "+ newCardDTO.type().toUpperCase()+" card", HttpStatus.BAD_REQUEST);
            }
        }

        String cardNumber;
        boolean isUnique = false;

        do {
            cardNumber = GenerateRandomNumber.generateNumberCard();
            Card card = cardRepository.findByNumber(cardNumber);

            // Si la cuenta no existe en la base de datos, es única
            if (card == null) {
                isUnique = true;
            }

        } while (!isUnique);

        if (newCardDTO.type().toLowerCase().equals("credit")) {
            if (client.getCards().stream().filter(card -> card.getType().equals(CardType.CREDIT)).toArray().length < 3) {
                if (newCardDTO.color().toLowerCase().equals("gold")) {
                    Card newCard = new Card(CardType.CREDIT, CardColor.GOLD, cardNumber, GenerateCvvNumber.generateNumer(),date,date.plusYears(5) );
                    client.addCard(newCard);
                    newCard.setClient(client);
                    cardRepository.save(newCard);
                    return new ResponseEntity<>("Created card", HttpStatus.CREATED);
                } else if (newCardDTO.color().toLowerCase().equals("silver")) {
                    Card newCard = new Card(CardType.CREDIT, CardColor.SILVER, cardNumber, GenerateCvvNumber.generateNumer(), date,date.plusYears(5) );
                    client.addCard(newCard);
                    newCard.setClient(client);
                    cardRepository.save(newCard);
                    return new ResponseEntity<>("Created card", HttpStatus.CREATED);
                }
                else if (newCardDTO.color().toLowerCase().equals("titanium")){
                    Card newCard = new Card(CardType.CREDIT, CardColor.TITANIUM, cardNumber, GenerateCvvNumber.generateNumer(), date,date.plusYears(5) );
                    client.addCard(newCard);
                    newCard.setClient(client);
                    cardRepository.save(newCard);
                    return new ResponseEntity<>("Created card", HttpStatus.CREATED);
                }
            }else { return new ResponseEntity<>("You cannot have more than 3 CREDIT CARDS", HttpStatus.FORBIDDEN); }
        }

        if (newCardDTO.type().toLowerCase().equals("debit")) {

            if (client.getCards().stream().filter(card -> card.getType().equals(CardType.DEBIT)).count() < 3) {
                if (newCardDTO.color().toLowerCase().equals("gold")) {
                    Card newCard = new Card(CardType.DEBIT, CardColor.GOLD, cardNumber, GenerateCvvNumber.generateNumer(), date,date.plusYears(5) );
                    client.addCard(newCard);
                    newCard.setClient(client);
                    cardRepository.save(newCard);
                    return new ResponseEntity<>("Created card", HttpStatus.CREATED);
                } else if (newCardDTO.color().toLowerCase().equals("silver")) {
                    Card newCard = new Card(CardType.DEBIT, CardColor.SILVER, cardNumber, GenerateCvvNumber.generateNumer(), date,date.plusYears(5) );
                    client.addCard(newCard);
                    newCard.setClient(client);
                    cardRepository.save(newCard);
                    return new ResponseEntity<>("Created card", HttpStatus.CREATED);
                }
                else if (newCardDTO.color().toLowerCase().equals("titanium")){
                    Card newCard = new Card(CardType.DEBIT, CardColor.TITANIUM, cardNumber, GenerateCvvNumber.generateNumer(), date,date.plusYears(5) );
                    client.addCard(newCard);
                    newCard.setClient(client);
                    cardRepository.save(newCard);
                    return new ResponseEntity<>("Created card", HttpStatus.CREATED);
                }

            }else { return new ResponseEntity<>("You cannot have more than 3 DEBIT CARDS", HttpStatus.FORBIDDEN); }
        }
        return new ResponseEntity<>("ALGO SALIO MAL", HttpStatus.FORBIDDEN);
    }
}
