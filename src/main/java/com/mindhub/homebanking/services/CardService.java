package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.NewCardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface CardService {
    Card getCardByNumber(String number);
    CardDTO getCardDTO(Card card);
    //Client getAuthenticatedClient(Authentication authentication);
    void saveCard(Card card);

    List<CardDTO> getCurrentClientCards(Client authenticateClient);
    boolean parameterIsBlank(String parameter);
    ResponseEntity<String> responseParameterIsBlank(String parameter);
    boolean hasMoreThan3TypeCard(CardType cardType, Client authenticateClient);
    boolean authenticatedClientHasCardRequested(CardType cardType, CardColor cardColor, Client authenticatedClient);
    String randomUniqueCardNumber();
    void saveNewCardCreated(CardType cardType, CardColor cardColor, Client authenticatedClient);
    ResponseEntity<?> createNewCardForCurrentClient(Authentication authentication, NewCardDTO newCardDTO);
    ResponseEntity<?> getAuthenticatedClientCards(Authentication authentication);
    boolean authenticatedClientHaveCards(Client authenticatedClient);
}
