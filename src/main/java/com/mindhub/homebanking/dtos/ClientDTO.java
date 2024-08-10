package com.mindhub.homebanking.dtos;
import com.mindhub.homebanking.models.Client;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

// Armo un DTO para transferis datos, en este caso todos los datos del cliente que reciba por parametro el constructor de este DTO
// Este cliente ya va a estar previamente creado, ya existe
public class ClientDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Set<AccountDTO> accounts;

    private List<ClientLoanDTO> loans = new ArrayList<>();

    // El constructor de ClientDTO recibe por parametro un objeto ya creado de tipo Client para poder acceder a los valores de las propiedades de client
    // a traves de sus metodos accesores getters
    public ClientDTO(Client client) {
    // mediante el objeto client a traves de su metodo accesor "getId()" accedo al valor de su propiedad id y ese valor le asigno a la propiedad id de esta clase
        this.id = client.getId();
        this.firstName = client.getFirstName();
        this.lastName = client.getLastName();
        this.email = client.getEmail();
        this.accounts = client.getAccounts().stream()
                .map(AccountDTO::new)
                .collect(Collectors.toSet());

        this.loans = client.getClientLoans().stream().map(ClientLoanDTO::new).collect(Collectors.toList());
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public Set<AccountDTO> getAccounts() {
        return accounts;
    }

    public List<ClientLoanDTO> getLoans() {
        return loans;
    }

    public void setLoans(List<ClientLoanDTO> loans) {
        this.loans = loans;
    }

    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + accounts +
                ", clientLoans=" + loans +
                '}';
    }
}
