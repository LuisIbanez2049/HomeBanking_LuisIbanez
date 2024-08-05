package com.mindhub.homebanking.repositories;


import com.mindhub.homebanking.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que este componente actuara como gestor de acceso a la capa de persistencia de datos
public interface ClientRepository extends JpaRepository<Client, Long> {
}







// "<Client, Long>" Client es tipo de dato que quiero trabajar y "Long" es el tipo de dato de la ID

// Esta interfaz extiende de la interfaz "JpaRepository" por lo que va a tener todos los metodos de "JpaRepository" para poder comunicarme
// con la base de datos
// y graias que estoy usando la capa persistencia de "spring data" tongo toda la implementacion de esos métodos
// "JpaRepository" es una interfaz que esta dentro de "spring data" el cual usa "Hibernate" para hacer la implementación
// Ya sea para almacenar, obtener, modificar o eliminar objetos de la base de datos