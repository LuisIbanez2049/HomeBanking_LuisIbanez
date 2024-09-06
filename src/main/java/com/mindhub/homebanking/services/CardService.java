package com.mindhub.homebanking.services;

import com.mindhub.homebanking.dtos.CardDTO;
import com.mindhub.homebanking.models.Card;

public interface CardService {
    Card getCardByNumber(String number);
    CardDTO getCardDTO(Card card);
    void saveCard(Card card);
}
