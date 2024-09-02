package com.mindhub.homebanking.dtos;

public record NewTransactionDTO (String sourceAccount, String destinyAccount, String description, Double amount ) {
}
