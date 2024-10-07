package com.mindhub.homebanking.dtos;

public record PaymentRecordDTO(String cardNumberClient, String accountNumberRestaurant, double totalAmount) {
}
