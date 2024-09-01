package com.mindhub.homebanking.models.utils;

import java.util.Random;

public class GenerateAccountNumber {
    private static final int MAX_DIGITS = 8;

    public static String generateSerialNumber() {
        Random random = new Random();
        // Genera un número aleatorio entre 0 y el máximo valor para 8 dígitos octales (7,777,777)
        int randomNumber = random.nextInt((int) Math.pow(8, MAX_DIGITS));

        // Convierte el número a octal y añade ceros al inicio para tener siempre hasta 8 dígitos
        String octalNumber = String.format("%08o", randomNumber);

        return "VIN-" + octalNumber;
    }
}
