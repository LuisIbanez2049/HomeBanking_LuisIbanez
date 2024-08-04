package com.mindhub.homebanking.models;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity   // Le indicamos a Spring que genere una tabla en la base de datos, que almacenara nuestros objetos
public class Client {

    @Id // Con esta anotación especifico cuál sera la clave principal de nuestra clase
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Esto hará que el valor de la "id" sea generado
                                                       // por la base de datos automáticamente
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    //----------------------------Métodos Constructor------------------------------------
    public Client() { }

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
    //--------------------------------------------------------------------------------------


    @Override
    public String toString() {
        return "Client{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
