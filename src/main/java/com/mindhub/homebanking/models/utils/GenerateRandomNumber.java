package com.mindhub.homebanking.models.utils;

import java.util.Random;

public class GenerateRandomNumber {

    public static String generateNumberCard() {
        Random random = new Random();
        String number = "";
        for (int i = 0; i < 4; i++) {
            number += random.nextInt((9999 - 1000) + 1) + 1000;
            if (i < 3) {
                number += "-";
            }
        }
        return number;
    }
}
