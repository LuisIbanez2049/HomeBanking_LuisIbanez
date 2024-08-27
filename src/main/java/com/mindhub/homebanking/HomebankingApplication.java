package com.mindhub.homebanking;

import com.mindhub.homebanking.models.*;
import com.mindhub.homebanking.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@SpringBootApplication
public class HomebankingApplication {

	public static void main(String[] args) {
		SpringApplication.run(HomebankingApplication.class, args);
	}

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Bean //Indicamos a spring que lo tiene que tener en cuanta a la hora de arrancar la aplicaion
	// esto se va aejecutar primero cuando corra la aplicación
	public CommandLineRunner initData(ClientRepository clientRepository,
									  AccountRepository accountRepository,
									  TransactionRepository transactionRepository,
									  LoanRepository loanRepository,
									  ClienLoanRepository clienLoanRepository,
									  CardRepository cardRepository){
		return (args) -> {

			LocalDateTime dateNow = LocalDateTime.now();
			LocalDate date = LocalDate.now();


			Client melba = new Client("Melba", "Morel", "melba@mindhub.com", passwordEncoder.encode("123"));
			Client luis = new Client("Luis", "Ibanez", "luis@gmail.com", passwordEncoder.encode("1234"));

			clientRepository.save(melba);
			clientRepository.save(luis);

			//----------------------------------------------------------------------------------------
			Account account1Melba = new Account("VIN001", dateNow, 5000);
			Account account2Melba = new Account("VIN002", dateNow.plusDays(1), 7500);

			account1Melba.setClient(melba);
			account2Melba.setClient(melba);

			melba.addAccount(account1Melba);
			melba.addAccount(account2Melba);

			accountRepository.save(account1Melba);
			accountRepository.save(account2Melba);
			//----------------------------------------------------------------------------------------

			//----------------------------------------------------------------------------------------
			Account account1Luis = new Account("VIN003", dateNow, 2000);
			Account account2Luis = new Account("VIN004", dateNow.minusMonths(1), 12000);

			account1Luis.setClient(luis);
			account2Luis.setClient(luis);

			luis.addAccount(account1Luis);
			luis.addAccount(account2Luis);

			accountRepository.save(account1Luis);
			accountRepository.save(account2Luis);


			//-----------Agregar transacciones a las cuentas de Melva --------------------------------------------------
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


			//--------------------------------Crear Prestamos
			Loan mortage = new Loan("Mortgage", 500000, Arrays.asList(12,24,36,48,60));
			Loan personal = new Loan("Personal", 100000, Arrays.asList(6,12,24));
			Loan automotive = new Loan("Automotive", 300000, Arrays.asList(6,12,24,36));

			loanRepository.save(mortage);
			loanRepository.save(personal);
			loanRepository.save(automotive);

			//-----------Loan de Melva----------------------------------------------------
			ClientLoan clientLoan1 = new ClientLoan(400000,60);
			melba.addClientLoan(clientLoan1);
			mortage.addClientLoan(clientLoan1);
			clienLoanRepository.save(clientLoan1);

			ClientLoan clientLoan2 = new ClientLoan(50000,12);
			melba.addClientLoan(clientLoan2);
			personal.addClientLoan(clientLoan2);
			clienLoanRepository.save(clientLoan2);


			//-----------Loan de Luis----------------------------------------------------
			ClientLoan clientLoan3 = new ClientLoan(100000,24);
			luis.addClientLoan(clientLoan3);
			personal.addClientLoan(clientLoan3);
			clienLoanRepository.save(clientLoan3);

			ClientLoan clientLoan4 = new ClientLoan(200000,36);
			luis.addClientLoan(clientLoan4);
			automotive.addClientLoan(clientLoan4);
			clienLoanRepository.save(clientLoan4);

			//-------------------------------------------Crear Cards de Melba----------------------------------------------
			Card card1Melva = new Card(CardType.DEBIT, CardColor.GOLD, date,date.plusYears(5));
			Card card2Melva = new Card(CardType.CREDIT, CardColor.TITANIUM, date, date.plusYears(5));

			melba.addCard(card1Melva);
			melba.addCard(card2Melva);

			card1Melva.setClient(melba);
			card2Melva.setClient(melba);

			cardRepository.save(card1Melva);
			cardRepository.save(card2Melva);

			//-------------------------------------------Crear Cards de Melba----------------------------------------------
			Card card1Luis = new Card(CardType.CREDIT, CardColor.SILVER, date, date.plusYears(5));

			luis.addCard(card1Luis);

			card1Luis.setClient(luis);

			cardRepository.save(card1Luis);

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
