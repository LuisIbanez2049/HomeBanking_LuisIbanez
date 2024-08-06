package com.mindhub.homebanking.repositories;


import com.mindhub.homebanking.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //
public interface ClientRepository extends JpaRepository<Client, Long> {
}







// "<Client, Long>" Client es tipo de dato que quiero trabajar y "Long" es el tipo de dato de la ID

// Esta interfaz extiende de la interfaz "JpaRepository" por lo que va a tener todos los metodos abstractos de "JpaRepository" para poder comunicarme
// con la base de datos, Ya sea para almacenar, obtener, modificar o eliminar objetos de la base de datos

// "JpaRepository" es una interfaz que esta dentro de "spring data" el cual usa "Hibernate" para hacer la implementación
