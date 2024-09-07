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
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
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
@RequestMapping("/api/clients")
public class CardController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private CardService cardService;

    @GetMapping("/current/cards")
    public ResponseEntity<?> getCurrentCards(Authentication authentication) {
        // Obtiene el cliente basado en el nombre de usuario autenticado.
        Client client = clientService.getClientByEmail(authentication.getName());
        List<CardDTO> cardDTOS = client.getCards().stream().map(card -> cardService.getCardDTO(card)).toList();
        if (cardDTOS.isEmpty()) {
            return new ResponseEntity<>("You do not have cards", HttpStatus.NOT_FOUND);
        }
        // Retorna los detalles del cliente en la respuesta.
        return new ResponseEntity<>(cardDTOS, HttpStatus.OK);
    }

    @PostMapping("/current/cards")
    public ResponseEntity<?> createCardForCurrentClient (Authentication authentication,@RequestBody NewCardDTO newCardDTO){
        try {

            Client client = clientService.getClientByEmail(authentication.getName());
            LocalDate date = LocalDate.now();

            if (newCardDTO.type().isBlank()) {
              return new ResponseEntity<>("Card type must be specified", HttpStatus.BAD_REQUEST);
            }
            if (newCardDTO.color().isBlank()) {
            return new ResponseEntity<>("Card color must be specified", HttpStatus.BAD_REQUEST);
            }
            CardType cardType;
            CardColor cardColor;

            try {
                cardType = CardType.valueOf(newCardDTO.type().toUpperCase());
            } catch (IllegalArgumentException e) {
                return new ResponseEntity<>("Not exist the type of card: ["+newCardDTO.type()+"] or you typed an space character which is forbidden", HttpStatus.BAD_REQUEST);
            }

            try {
                cardColor = CardColor.valueOf(newCardDTO.color().toUpperCase());
            } catch (IllegalArgumentException e) {
                return  new ResponseEntity<>("Not exist the color card: ["+newCardDTO.color()+"] or you typed an space character which is forbidden",HttpStatus.BAD_REQUEST);
            }
            if (client.getCards().stream().filter(card -> card.getType().equals(cardType)).count() == 3) {
                return new ResponseEntity<>("You cannot have more than 3 ["+newCardDTO.type().toUpperCase()+"] CARDS", HttpStatus.FORBIDDEN);
            }

            if (client.getCards().stream().anyMatch(card -> card.getType().equals(cardType) && card.getColor().equals(cardColor) )) {
              return new ResponseEntity<>("You already have an "+newCardDTO.color().toUpperCase()+ " "+ newCardDTO.type().toUpperCase()+" card", HttpStatus.BAD_REQUEST);
            }

            String cardNumber;
            boolean isUnique = false;

            do {
              cardNumber = GenerateRandomNumber.generateNumberCard();
              Card card = cardService.getCardByNumber(cardNumber);

              // Si la cuenta no existe en la base de datos, es única
              if (card == null) {
                isUnique = true;
              }

            } while (!isUnique);

            Card newCard = new Card(cardType, cardColor, cardNumber, GenerateCvvNumber.generateNumer(),date,date.plusYears(5) );
            client.addCard(newCard);
            newCard.setClient(client);
            cardService.saveCard(newCard);
            return new ResponseEntity<>("Created card", HttpStatus.CREATED);
        }  catch (Exception e){
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
