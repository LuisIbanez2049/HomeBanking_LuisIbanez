package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Bean //Indicamos a spring que lo tiene que tener en cuanta a la hora de arrancar la aplicaion
	// esto se va aejecutar primero cuando corra la aplicación
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository){
		return (args) -> {

			LocalDateTime dateNow = LocalDateTime.now();


			Client melba = new Client("Melba", "Morel", "melba@mindhub.com");
			Client luis = new Client("Luis", "Ibanez", "luis@gmail.com");

			clientRepository.save(melba);
			clientRepository.save(luis);

			//----------------------------------------------------------------------------------------
			Account account1 = new Account("VIN001", dateNow, 5000);
			Account account2 = new Account("VIN002", dateNow.plusDays(1), 7500);

			account1.setOwner(melba);
			account2.setOwner(melba);

			melba.addAccount(account1);
			melba.addAccount(account2);

			accountRepository.save(account1);
			accountRepository.save(account2);
			//----------------------------------------------------------------------------------------

			//----------------------------------------------------------------------------------------
			Account account3 = new Account("VIN003", dateNow, 2000);
			Account account4 = new Account("VIN004", dateNow.minusMonths(1), 12000);

			account3.setOwner(luis);
			account4.setOwner(luis);

			luis.addAccount(account3);
			luis.addAccount(account4);

			accountRepository.save(account3);
			accountRepository.save(account4);
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
