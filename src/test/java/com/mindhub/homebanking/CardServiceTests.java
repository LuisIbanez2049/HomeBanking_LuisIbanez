package com.mindhub.homebanking;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.dtos.NewCardDTO;
import com.mindhub.homebanking.models.Card;
import com.mindhub.homebanking.models.CardColor;
import com.mindhub.homebanking.models.CardType;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.services.CardService;
import com.mindhub.homebanking.services.ClientService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;


import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CardServiceTests {
    @Autowired
    private CardService cardService;
    @Autowired
    private ClientService clientService;
    @Test
    void testGetCardByNumber(){
        Card card = cardService.getCardByNumber("3435-6736-2470-2857");
        assertThat(card, is(notNullValue()));
    }
    @Test
    void testGetCardDTO(){
        Card card = cardService.getCardByNumber("3435-6736-2470-2857");
        CardDTO cardDTO = cardService.getCardDTO(card);
        assertThat(cardDTO, is(notNullValue()));
    }
    @Test
    void testSaveCard(){
        Card card = new Card(CardType.CREDIT, CardColor.GOLD, "1234", 123, LocalDate.now(), LocalDate.now().plusYears(5));
        cardService.saveCard(card);
        cardService.getCardByNumber("1234");
        assertThat(cardService.getCardByNumber("1234").getNumber(), is("1234"));
    }
    @Test
    void testGetCurrentClientCards(){
        Long id = 1L;
        List<CardDTO> cardDTOS = cardService.getCurrentClientCards(clientService.getClientById(id));
        assertThat(cardDTOS, is(notNullValue()));
    }
    @Test
    void testParameterIsBlank(){
        String parameter = "";
        assertThat(cardService.parameterIsBlank(parameter), is(true));
    }
    @Test
    void testParameterIsNotBlank(){
        String parameter = "notBlank";
        assertThat(cardService.parameterIsBlank(parameter), is(false));
    }
    @Test
    void testResponseParameterIsBlank(){
        String parameter = "type";
        ResponseEntity<String> response = cardService.responseParameterIsBlank(parameter);
        String expectedMessage = "Card "+parameter.toUpperCase()+" must be specified";
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testHasMoreThan3TypeCard(){
        Long id = 1L;
        Client client = clientService.getClientById(id);
        Card newCard1 = new Card(CardType.DEBIT, CardColor.SILVER, "3435-6736-2470-2899", 456, LocalDate.now(),LocalDate.now().plusYears(5));
        Card newCard2 = new Card(CardType.DEBIT, CardColor.TITANIUM,"3435-6736-6827-2299", 896, LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(newCard1);
        client.addCard(newCard2);
        newCard1.setClient(client);
        newCard2.setClient(client);
        cardService.saveCard(newCard1);
        cardService.saveCard(newCard2);
        assertThat(cardService.hasMoreThan3TypeCard(CardType.DEBIT, client), is(true));
    }
    @Test
    void testAuthenticatedClientHasCardRequested(){
        Long id = 1L;
        Client client = clientService.getClientById(id);
        assertThat(cardService.authenticatedClientHasCardRequested(CardType.DEBIT, CardColor.GOLD, client), is(true));
    }
    @Test
    void testRandomUniqueCardNumber(){
        Card card = cardService.getCardByNumber(cardService.randomUniqueCardNumber());
        assertThat(card, is(nullValue()));
    }
    @Test
    void testSaveNewCardCreated(){
        Long id = 1L;
        Client client = clientService.getClientById(id);
        cardService.saveNewCardCreated(CardType.DEBIT, CardColor.SILVER, client);
        assertThat(cardService.authenticatedClientHasCardRequested(CardType.DEBIT, CardColor.SILVER, client), is(true));
    }
    @Test
    void testCreateNewCardForCurrentClient(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        NewCardDTO newCardDTO = new NewCardDTO("debit", "silver");
        String expectedMessage = "Created card";
        ResponseEntity<?> response = cardService.createNewCardForCurrentClient(authentication, newCardDTO);
        assertThat(response.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testCreateNewCardForCurrentClient_SpaceCharacterCardType(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        NewCardDTO newCardDTO = new NewCardDTO("debit ", "silver");
        String expectedMessage = "Not exist the type of card: ["+newCardDTO.type()+"] or you typed an space character which is forbidden";
        ResponseEntity<?> response = cardService.createNewCardForCurrentClient(authentication, newCardDTO);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testCreateNewCardForCurrentClient_CardTypeNotExist(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        NewCardDTO newCardDTO = new NewCardDTO("arbolito", "silver");
        String expectedMessage = "Not exist the type of card: ["+newCardDTO.type()+"] or you typed an space character which is forbidden";
        ResponseEntity<?> response = cardService.createNewCardForCurrentClient(authentication, newCardDTO);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testCreateNewCardForCurrentClient_SpaceCharacterCardColor(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        NewCardDTO newCardDTO = new NewCardDTO("debit", "silver ");
        String expectedMessage = "Not exist the color card: ["+newCardDTO.color()+"] or you typed an space character which is forbidden";
        ResponseEntity<?> response = cardService.createNewCardForCurrentClient(authentication, newCardDTO);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testCreateNewCardForCurrentClient_CardColorNotExist(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        NewCardDTO newCardDTO = new NewCardDTO("debit", "arbolito");
        String expectedMessage = "Not exist the color card: ["+newCardDTO.color()+"] or you typed an space character which is forbidden";
        ResponseEntity<?> response = cardService.createNewCardForCurrentClient(authentication, newCardDTO);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testCreateNewCardForCurrentClient_CanNotCreateOtherDebitCard(){
        Long id = 1L;
        Client client = clientService.getClientById(id);
        Card newCard1 = new Card(CardType.DEBIT, CardColor.SILVER, "3435-6736-2470-2899", 456, LocalDate.now(),LocalDate.now().plusYears(5));
        Card newCard2 = new Card(CardType.DEBIT, CardColor.TITANIUM,"3435-6736-6827-2299", 896, LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(newCard1);
        client.addCard(newCard2);
        newCard1.setClient(client);
        newCard2.setClient(client);
        cardService.saveCard(newCard1);
        cardService.saveCard(newCard2);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        NewCardDTO newCardDTO = new NewCardDTO("debit", "silver");
        String expectedMessage = "You cannot have more than 3 ["+newCardDTO.type().toUpperCase()+"] CARDS";
        ResponseEntity<?> response = cardService.createNewCardForCurrentClient(authentication, newCardDTO);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testCreateNewCardForCurrentClient_CanNotCreateOtherCreditCard(){
        Long id = 1L;
        Client client = clientService.getClientById(id);
        Card newCard1 = new Card(CardType.CREDIT, CardColor.SILVER, "3435-6736-2470-2899", 456, LocalDate.now(),LocalDate.now().plusYears(5));
        Card newCard2 = new Card(CardType.CREDIT, CardColor.GOLD,"3435-6736-6827-2299", 896, LocalDate.now(), LocalDate.now().plusYears(5));
        client.addCard(newCard1);
        client.addCard(newCard2);
        newCard1.setClient(client);
        newCard2.setClient(client);
        cardService.saveCard(newCard1);
        cardService.saveCard(newCard2);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        NewCardDTO newCardDTO = new NewCardDTO("credit", "silver");
        String expectedMessage = "You cannot have more than 3 ["+newCardDTO.type().toUpperCase()+"] CARDS";
        ResponseEntity<?> response = cardService.createNewCardForCurrentClient(authentication, newCardDTO);
        assertThat(response.getStatusCode(), is(HttpStatus.FORBIDDEN));
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testCreateNewCardForCurrentClient_AlreadyHasTheCard(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        NewCardDTO newCardDTO = new NewCardDTO("credit", "titanium");
        String expectedMessage = "You already have an "+newCardDTO.color().toUpperCase()+ " "+ newCardDTO.type().toUpperCase()+" card";
        ResponseEntity<?> response = cardService.createNewCardForCurrentClient(authentication, newCardDTO);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testAuthenticatedClientHaveCards(){
        Client client = new Client();
        assertThat(cardService.authenticatedClientHaveCards(client), is(true));
    }
    @Test
    void testGetAuthenticatedClientCards_HasNotCards(){
        Client client = new Client("pepe", "pepo", "pepo@hmail.com", "123");
        clientService.saveClient(client);
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("pepo@hmail.com");
        ResponseEntity<?> response = cardService.getAuthenticatedClientCards(authentication);
        String expectedMessage = "You do not have cards";
        assertThat(response.getStatusCode(), is(HttpStatus.NOT_FOUND));
        assertThat(response.getBody(), is(expectedMessage));
    }
    @Test
    void testGetAuthenticatedClientCards(){
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("melba@mindhub.com");
        ResponseEntity<?> response = cardService.getAuthenticatedClientCards(authentication);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));
        assertThat(response.getBody(), is(notNullValue()));
    }

}
