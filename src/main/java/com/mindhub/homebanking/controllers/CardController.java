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
    private CardService cardService;

    @GetMapping("/current/cards")
    public ResponseEntity<?> getCurrentCards(Authentication authentication) {
        try {
            return cardService.getAuthenticatedClientCards(authentication);
        } catch (Exception e) { return new ResponseEntity<>("Error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }

    @PostMapping("/current/cards")
    public ResponseEntity<?> createCardForCurrentClient (Authentication authentication,@RequestBody NewCardDTO newCardDTO){
        try {
            return cardService.createNewCardForCurrentClient(authentication, newCardDTO);
        } catch (Exception e){ return new ResponseEntity<>("Error creating card: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR); }
    }
}
