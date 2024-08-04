package com.mindhub.homebanking.repositories;


import com.mindhub.homebanking.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que este componente actuara como gestor de acceso a la capa de persistencia de datos
public interface ClientRepository extends JpaRepository<Client, Long> {
}
