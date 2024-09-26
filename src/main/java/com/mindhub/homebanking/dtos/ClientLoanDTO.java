package com.mindhub.homebanking.dtos;

import com.mindhub.homebanking.models.ClientLoan;
import java.text.NumberFormat;
import java.util.Locale;

public class ClientLoanDTO {

    private Long id;
    private Long loanId;
    private int payments;
    private String name;
    private String amount;
    private String description;


    public ClientLoanDTO(ClientLoan clientLoan) {
        // Obtener una instancia de NumberFormat para formatear con separadores de miles
        NumberFormat formato = NumberFormat.getNumberInstance(Locale.US);

        // Imprimir el número con separadores de miles
        String numeroFormateado = formato.format(clientLoan.getAmount());
        this.id = clientLoan.getId();
        this.loanId = clientLoan.getLoan().getId();
        this.payments = clientLoan.getPayments();
        this.name = clientLoan.getLoan().getName();
        this.amount = numeroFormateado;
        this.description = clientLoan.getDescription();
    }

    public Long getId() {
        return id;
    }

    public Long getLoanId() {
        return loanId;
    }

    public int getPayments() {
        return payments;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
