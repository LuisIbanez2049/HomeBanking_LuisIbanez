package com.mindhub.homebanking;

import com.mindhub.homebanking.models.Account;
import com.mindhub.homebanking.models.Client;
import com.mindhub.homebanking.models.Transaction;
import com.mindhub.homebanking.models.TransactionType;
import com.mindhub.homebanking.repositories.AccountRepository;
import com.mindhub.homebanking.repositories.ClientRepository;
import com.mindhub.homebanking.repositories.TransactionRepository;
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
	public CommandLineRunner initData(ClientRepository clientRepository, AccountRepository accountRepository, TransactionRepository transactionRepository){
		return (args) -> {

			LocalDateTime dateNow = LocalDateTime.now();


			Client melba = new Client("Melba", "Morel", "melba@mindhub.com");
			Client luis = new Client("Luis", "Ibanez", "luis@gmail.com");

			clientRepository.save(melba);
			clientRepository.save(luis);

			//----------------------------------------------------------------------------------------
			Account account1Melba = new Account("VIN001", dateNow, 5000);
			Account account2Melba = new Account("VIN002", dateNow.plusDays(1), 7500);

			account1Melba.setOwner(melba);
			account2Melba.setOwner(melba);

			melba.addAccount(account1Melba);
			melba.addAccount(account2Melba);

			accountRepository.save(account1Melba);
			accountRepository.save(account2Melba);
			//----------------------------------------------------------------------------------------

			//----------------------------------------------------------------------------------------
			Account account1Luis = new Account("VIN003", dateNow, 2000);
			Account account2Luis = new Account("VIN004", dateNow.minusMonths(1), 12000);

			account1Luis.setOwner(luis);
			account2Luis.setOwner(luis);

			luis.addAccount(account1Luis);
			luis.addAccount(account2Luis);

			accountRepository.save(account1Luis);
			accountRepository.save(account2Luis);


			//-----------Agregar transacciones a las cuentas de Melva --------------------------------------------------
//			Transaction transaction1MelvaAccount1 = new Transaction(TransactionType.CREDIT, 2000, "Rent", dateNow);
//			account1Melba.addTransaction(transaction1MelvaAccount1);
//			transactionRepository.save(transaction1MelvaAccount1);
			Transaction transaction1MelbaAccount1 = new Transaction(TransactionType.CREDIT, 2000, "Rent", dateNow);
			Transaction transaction2MelbaAccount1 = new Transaction(TransactionType.DEBIT, 500, "Groceries", dateNow.minusDays(1));
			Transaction transaction3MelbaAccount1 = new Transaction(TransactionType.CREDIT, 1500, "Salary", dateNow.minusDays(2));

			account1Melba.addTransaction(transaction1MelbaAccount1);
			account1Melba.addTransaction(transaction2MelbaAccount1);
			account1Melba.addTransaction(transaction3MelbaAccount1);

			transactionRepository.save(transaction1MelbaAccount1);
			transactionRepository.save(transaction2MelbaAccount1);
			transactionRepository.save(transaction3MelbaAccount1);


			Transaction transaction1MelbaAccount2 = new Transaction(TransactionType.DEBIT, 800, "Electricity Bill", dateNow);
			Transaction transaction2MelbaAccount2 = new Transaction(TransactionType.CREDIT, 2500, "Freelance Work", dateNow.minusDays(3));
			Transaction transaction3MelvaAccount2 = new Transaction(TransactionType.DEBIT, 700, "Internet Bill", dateNow.minusDays(4));

			account2Melba.addTransaction(transaction1MelbaAccount2);
			account2Melba.addTransaction(transaction2MelbaAccount2);
			account2Melba.addTransaction(transaction3MelvaAccount2);

			transactionRepository.save(transaction1MelbaAccount2);
			transactionRepository.save(transaction2MelbaAccount2);
			transactionRepository.save(transaction3MelvaAccount2);

			// -----------Agregar transacciones a las cuentas de Luis --------------------------------------------------
			Transaction transaction1LuisAccount1 = new Transaction(TransactionType.CREDIT, 1000, "Gift", dateNow);
			Transaction transaction2LuisAccount1 = new Transaction(TransactionType.DEBIT, 1200, "Car Maintenance", dateNow.minusDays(1));
			Transaction transaction3LuisAccount1 = new Transaction(TransactionType.CREDIT, 4000, "Bonus", dateNow.minusDays(2));

			account1Luis.addTransaction(transaction1LuisAccount1);
			account1Luis.addTransaction(transaction2LuisAccount1);
			account1Luis.addTransaction(transaction3LuisAccount1);

			transactionRepository.save(transaction1LuisAccount1);
			transactionRepository.save(transaction2LuisAccount1);
			transactionRepository.save(transaction3LuisAccount1);

			Transaction transaction1LuisAccount2 = new Transaction(TransactionType.DEBIT, 800, "Restaurant", dateNow);
			Transaction transaction2LuisAccount2 = new Transaction(TransactionType.CREDIT, 5000, "Investment Return", dateNow.minusDays(3));
			Transaction transaction3LuisAccount2 = new Transaction(TransactionType.DEBIT, 600, "Subscription", dateNow.minusDays(4));

			account2Luis.addTransaction(transaction1LuisAccount2);
			account2Luis.addTransaction(transaction2LuisAccount2);
			account2Luis.addTransaction(transaction3LuisAccount2);

			transactionRepository.save(transaction1LuisAccount2);
			transactionRepository.save(transaction2LuisAccount2);
			transactionRepository.save(transaction3LuisAccount2);
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
