package com.mindhub.homebanking.services.serviceImpl;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.NewCardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.utils.GenerateCvvNumber;
import com.mindhub.homebanking.models.utils.GenerateRandomNumber;
import com.mindhub.homebanking.repositories.CardRepository;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    @Autowired
    private CardRepository cardRepository;
    @Autowired
    private ClientService clientService;

    @Override
    public Client getAuthenticatedClient(Authentication authentication) {
        return clientService.getClientByEmail(authentication.getName());
    }
    @Override
    public Card getCardByNumber(String number) {
        return cardRepository.findByNumber(number);
    }

    @Override
    public CardDTO getCardDTO(Card card) {
        return new CardDTO(card);
    }

    @Override
    public void saveCard(Card card) {
        cardRepository.save(card);
    }

    @Override
    public List<CardDTO> getCurrentClientCards(Client authenticateClient) {
        return authenticateClient.getCards().stream().map(card -> new CardDTO(card)).toList();
    }

    @Override
    public boolean parameterIsBlank(String parameter) {
        return parameter.isBlank();
    }

    @Override
    public ResponseEntity<String> responseParameterIsBlank(String parameter) {
            return new ResponseEntity<>("Card "+parameter.toUpperCase()+" must be specified", HttpStatus.BAD_REQUEST);
    }

    @Override
    public boolean hasMoreThan3TypeCard(CardType cardType, Client authenticateClient) {
        return getCurrentClientCards(authenticateClient).stream().filter(card -> card.getType().equals(cardType)).count() == 3;
    }

    @Override
    public boolean authenticatedClientHasCardRequested(CardType cardType, CardColor cardColor, Client authenticatedClient) {
        return authenticatedClient.getCards().stream().anyMatch(card -> card.getType().equals(cardType) && card.getColor().equals(cardColor));
    }

    @Override
    public String randomUniqueCardNumber() {
        String cardNumber;
        do{
            cardNumber = GenerateRandomNumber.generateNumberCard();
        }while (getCardByNumber(cardNumber) != null);
        return cardNumber;
    }

    @Override
    public void saveNewCardCreated(CardType cardType, CardColor cardColor, Client authenticatedClient) {
        Card newCard = new Card(cardType, cardColor, randomUniqueCardNumber(), GenerateCvvNumber.generateNumer(), LocalDate.now(), LocalDate.now().plusYears(5));
        authenticatedClient.addCard(newCard);
        newCard.setClient(authenticatedClient);
        saveCard(newCard);
    }

    @Override
    public ResponseEntity<?> createNewCardForCurrentClient(Authentication authentication, NewCardDTO newCardDTO) {
        Client authenticatedClient = getAuthenticatedClient(authentication);
        if (parameterIsBlank(newCardDTO.type())) { return responseParameterIsBlank("type");}
        if (parameterIsBlank(newCardDTO.color())) { return responseParameterIsBlank("color");}
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
        if (hasMoreThan3TypeCard(cardType, authenticatedClient)) {
            return new ResponseEntity<>("You cannot have more than 3 ["+newCardDTO.type().toUpperCase()+"] CARDS", HttpStatus.FORBIDDEN);
        }
        if (authenticatedClientHasCardRequested(cardType, cardColor, authenticatedClient)) {
            return new ResponseEntity<>("You already have an "+newCardDTO.color().toUpperCase()+ " "+ newCardDTO.type().toUpperCase()+" card", HttpStatus.BAD_REQUEST);
        }
        saveNewCardCreated(cardType, cardColor,authenticatedClient);
        return new ResponseEntity<>("Created card", HttpStatus.CREATED);
    }

    @Override
    public boolean authenticatedClientHaveCards(Client authenticatedClient) {
        return authenticatedClient.getCards().isEmpty();
    }

    @Override
    public ResponseEntity<?> getAuthenticatedClientCards(Authentication authentication) {
        if (authenticatedClientHaveCards(getAuthenticatedClient(authentication))) {
            return new ResponseEntity<>("You do not have cards", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(getCurrentClientCards(getAuthenticatedClient(authentication)), HttpStatus.OK);
    }
}
