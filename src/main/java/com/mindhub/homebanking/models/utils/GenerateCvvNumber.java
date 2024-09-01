package com.mindhub.homebanking.models.utils;

import java.util.Random;

public class GenerateCvvNumber {
    public static int generateNumer(){
        Random random = new Random();
         int cvv = random.nextInt((999 - 100) + 1) + 100;

        return cvv;
    }
}
