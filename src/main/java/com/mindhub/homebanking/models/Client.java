package com.mindhub.homebanking.models;


import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity   // Le indicamos a Spring que genere una tabla en la base de datos, que almacenara nuestros objetos
public class Client {

    @Id // Con esta anotación especifico cuál sera la clave primaria de nuestra clase
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Esto hará que el valor de la "id" sea generado automaticamente
                                                       // por la base de datos en una secuencia de 1 en 1
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private boolean active = true;

    //--------------------------------------------------------------------------------------
    // Trayendo a la persona JPA automaticamente deberia traerme la o las cuentas asociadas
    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    //@JsonManagedReference
    Set<Account> accounts = new HashSet<>();

    @OneToMany(mappedBy = "client", fetch = FetchType.EAGER)
    private List<ClientLoan> clientLoans = new ArrayList<>();
    //--------------------------------------------------------------------------------------



    //----------------------------Métodos Constructor------------------------------------
    // Es necesario el constructor vacio para que "Hibernate" reserve un espacio en memoria
    public Client() { }

    // Constructor parametrizado
    public Client(String first, String last, String emailClient) {
        firstName = first;
        lastName = last;
        email = emailClient;
    }
    //--------------------------------------------------------------------------------------



    //----------------------------Métodos GETTER Y SETTER------------------------------------
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public List<ClientLoan> getClientLoans() {
        return clientLoans;
    }

    public void setClientLoans(List<ClientLoan> clientLoans) {
        this.clientLoans = clientLoans;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
    //--------------------------------------------------------------------------------------


    //--------------------------------------------------------------------------------------
    // Nos permite conectar al Client con Account
    public void addAccount(Account account){
        account.setClient(this);
        accounts.add(account);
    }

    public void addClientLoan(ClientLoan clientLoan){
        this.clientLoans.add(clientLoan);
        clientLoan.setClient(this);
    }
    //--------------------------------------------------------------------------------------


    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
