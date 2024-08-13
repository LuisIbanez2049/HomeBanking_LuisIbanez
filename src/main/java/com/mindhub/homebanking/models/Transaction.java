package com.mindhub.homebanking.models;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double amount;
    private String description;
    private LocalDateTime dateTime = LocalDateTime.now();

    // Con "@Enumerated" Indico que la propiedad "type" va a ser guardada como un campo numerico en la base de datos
    // y con "STRING" digo que en vez de almacenar el numero de la posicion del enum, me almacene el texto que hay en esa posicion en la base de datos
    // para que sea mas entendible
    @Enumerated(EnumType.STRING)
    private TransactionType type;

    //---------------------------------Relacion entre "Transaction" and "Account"-------------------
    // Con "@ManyToOne" indico que Transaction va a tener una relacion de muchos a uno con Account (Muchas transacciones van a pertenecer a una cuenta)
    //Con "@JoinColumn" indico que a la tabla, que contiene las transacciones en la base de datos, le voy a agregar una columna con el nombre "account_id"
    // la cual va a contener las las id de las cuentas a las que les pertenece esa transaccion
    // Si no pongo la anotacion "@JoinColumn" la columna va a tener el nombre que le asigna la base de datos por defecto
    @ManyToOne(fetch = FetchType.EAGER) // Con ".EAGER" indico que cuando solicite una transaccion, esta va a venir junto con la cuenta asociada automáticamente
    @JoinColumn(name = "account_id")
    private Account account;
    //---------------------------------------------------------------------------------------



    //---------------------Constructor-----------------------------------------------
    public Transaction() {}

    public Transaction(TransactionType type, double amount, String description, LocalDateTime dateTime) {
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.dateTime = dateTime;
    }
    //---------------------------------------------------------------------------------------


    //--------------Getter and Setter---------------------------------------------------------------
    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public Long getId() {
        return id;
    }
    //---------------------------------------------------------------------------------------


    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", type=" + type +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", dateTime=" + dateTime +
                '}';
    }
}
