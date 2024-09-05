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

import javax.swing.plaf.synth.ColorType;
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
        try {

        Client client = clientRepository.findByEmail(authentication.getName());
        LocalDate date = LocalDate.now();

        if (newCardDTO.type().isBlank()) {
            return new ResponseEntity<>("Card type must be specified", HttpStatus.BAD_REQUEST);
        }
        if (newCardDTO.color().isBlank()) {
            return new ResponseEntity<>("Card color must be specified", HttpStatus.BAD_REQUEST);
        }
        // "equalsIgnoreCase" ignora si las letras estan en mayuscula o minuscula. Evito utilizar "toLowerCase()" o "toUpperCase()"
        if (!newCardDTO.type().equalsIgnoreCase("debit") && !newCardDTO.type().equalsIgnoreCase("credit")) {
            return new ResponseEntity<>("No exist the type of card: "+"["+newCardDTO.type()+"]" + " or you typed an space character which is forbidden", HttpStatus.BAD_REQUEST);
        }
        if (!newCardDTO.color().equalsIgnoreCase("gold") && !newCardDTO.color().equalsIgnoreCase("silver") && !newCardDTO.color().equalsIgnoreCase("titanium")) {
            return new ResponseEntity<>("No exist the type of color: "+"["+newCardDTO.color()+"]" + " or you typed an space character which is forbidden", HttpStatus.BAD_REQUEST);
        }
        //-------------------------------------------------------------------------------------
        if (newCardDTO.type().toLowerCase().contains("debit")) {
            if (client.getCards().stream().filter(card -> card.getType().equals(CardType.DEBIT)).count() == 3) {
                return new ResponseEntity<>("You cannot have more than 3 DEBIT CARDS", HttpStatus.FORBIDDEN);
            }

        }
        if (newCardDTO.type().toLowerCase().contains("credit")) {
            if (client.getCards().stream().filter(card -> card.getType().equals(CardType.CREDIT)).count() == 3) {
                return new ResponseEntity<>("You cannot have more than 3 CREDIT CARDS", HttpStatus.FORBIDDEN);
            }
        }

        CardType cardType;
        if (newCardDTO.type().equalsIgnoreCase("debit")) {
            cardType = CardType.DEBIT;
        } else cardType = CardType.CREDIT;

        CardColor cardColor;
        if (newCardDTO.color().equalsIgnoreCase("gold")) {
            cardColor = CardColor.GOLD;
        } else if (newCardDTO.color().equalsIgnoreCase("silver")) {
            cardColor = CardColor.SILVER;
        } else cardColor = CardColor.TITANIUM;

        if (client.getCards().stream().anyMatch(card -> card.getType().equals(cardType) && card.getColor().equals(cardColor) )) {
            return new ResponseEntity<>("You already have an "+newCardDTO.color().toUpperCase()+ " "+ newCardDTO.type().toUpperCase()+" card", HttpStatus.BAD_REQUEST);
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

            Card newCard = new Card(cardType, cardColor, cardNumber, GenerateCvvNumber.generateNumer(),date,date.plusYears(5) );
            client.addCard(newCard);
            newCard.setClient(client);
            cardRepository.save(newCard);
            return new ResponseEntity<>("Created card", HttpStatus.CREATED);
        } catch (Exception e){
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
