package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean //Indicamos a spring que lo tiene que tener en cuanta a la hora de arrancar la aplicaion
	// esto se va aejecutar primero cuando corra la aplicación
	public CommandLineRunner initData(ClientRepository clientRepository){
		return (args) -> {
			clientRepository.save((new Client("Melba", "Morel", "melba@mindhub.com")));
			clientRepository.save((new Client("Luis", "Ibanez", "luis@gmail.com")));
		};
	}
}

// Aqui no necesitamos configurar nada porque estamos invirtiendo el control a SpringBoot, él  se encarga de hacer las
// configuraciones

// CommandLineRunner es una interfaz de Spring Boot que bajo la anotacion @Bean se va a ejecutar cuando arranque la aplicacion
// CommandLineRunner es un singleton, patrón de diseño que se va a ejecutar solo una vez cada que se inicie la aplicación
// Recibe por parametro la instancia de "ClientRepository" para poder usar los metodos "save" de "JpaRepository"
// En este caso cada vez que arranque la aplicacion se van a crear y guardar dos objetos instanciados a partir de la clase"Client"
//en la base de datos.
