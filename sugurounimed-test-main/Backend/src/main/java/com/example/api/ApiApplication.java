package com.example.api;

import com.example.api.domain.Customer;
import com.example.api.model.Endereco;
import com.example.api.model.EnderecoViaCep;
import com.example.api.service.AddressService;
import com.example.api.service.CustomerService;
import com.example.api.service.EnderecoController;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@SpringBootApplication
public class ApiApplication implements CommandLineRunner {

	@Autowired
	@JsonBackReference

	private CustomerService customerService;
	@Autowired

	private EnderecoController endereco;

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}



/*------------------------------------------------- Menu -------------------------------------------------------------*/

	private void displayMenu() throws IOException, InterruptedException {
		Scanner scanner = new Scanner(System.in);
		while (true) {
			System.out.println("\nMenu:");
			System.out.println("1. Exibir todos os clientes");
			System.out.println("2. Cadastrar novo cliente");
			System.out.println("3. Editar cliente");
			System.out.println("4. Adicionar endereço");
			System.out.println("5. Excluir cliente");
			System.out.println("6. Sair");
			System.out.print("Escolha uma opção: ");
			int choice = scanner.nextInt();
			scanner.nextLine();  // Consumir a nova linha

			switch (choice) {
				case 1:
					displayAllCustomers();
					break;
				case 2:
					createCustomer(scanner);
					break;
				case 3:
					editCustomer(scanner);
					break;
				case 4:
					addAddress(scanner);
					break;
				case 5:
					deleteCustomer(scanner);
					break;
				case 6:
					System.out.println("Saindo...");
					scanner.close();
					System.exit(0);
					break;
				default:
					System.out.println("Opção inválida. Tente novamente.");
			}
		}
	}

/*----------------------------------------- Listar Todos os Clientes -----------------------------------------------------*/

	@Transactional(readOnly = true)
	private void displayAllCustomers() {
		List<Customer> customers = customerService.findAll();


		List<Endereco> enderecoList = endereco.buscarTodosEnderecos();
		for (Endereco e: enderecoList){
			System.out.println(e);
		}

		for(Customer c: customers){
			for(Endereco e: enderecoList){
				if(e.getCustomer().getId() == c.getId()){
					c.getEnderecos().add(e);
				}
			}
		}

		System.out.println("\nClientes cadastrados:");
		customers.forEach(System.out::println);
	}



/*----------------------------------------- Cadastro de Cliente -----------------------------------------------------*/

	@Transactional
	private void createCustomer(Scanner scanner) throws IOException, InterruptedException {
		System.out.print("Digite o nome do cliente: ");
		String name = scanner.nextLine();
		System.out.print("Digite o email do cliente: ");
		String email = scanner.nextLine();
		System.out.print("Digite o gênero do cliente (M/F): ");
		String gender = scanner.nextLine();
		System.out.print("Digite o CEP do cliente: ");
		String cep = scanner.nextLine();

		List<Endereco> enderecos = new ArrayList<>();

		try {
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			AddressService addressService = new AddressService(cep);
			String json = addressService.retornaJson();
			EnderecoViaCep enderecoViaCep = gson.fromJson(json, EnderecoViaCep.class);
			Endereco endereco = new Endereco(enderecoViaCep);

			enderecos.add(endereco);
		} catch (RuntimeException e) {
			System.out.println("Erro na requisição: " + e.getMessage());
		}

		// Criação do cliente com o endereço
		Customer customer = new Customer(name, email, gender, enderecos);
		enderecos.forEach(e -> e.setCustomer(customer)); // Associar cliente ao endereço
		customerService.save(customer);

		System.out.println("Cliente cadastrado com sucesso.");
		System.out.println(enderecos);
	}


/*------------------------------------------- Editar Cliente -------------------------------------------------------*/

	@Transactional
	private void editCustomer(Scanner scanner) throws IOException, InterruptedException {
		System.out.print("Digite o ID do cliente a ser editado: ");
		Long id = scanner.nextLong();
		scanner.nextLine();  // Consumir a nova linha
		Optional<Customer> optionalCustomer = customerService.findById(id);

		if (optionalCustomer.isPresent()) {
			Customer customer = optionalCustomer.get();
			System.out.print("Digite o novo nome do cliente: ");
			customer.setName(scanner.nextLine());
			System.out.print("Digite o novo email do cliente: ");
			customer.setEmail(scanner.nextLine());
			System.out.print("Digite o novo gênero do cliente (M/F): ");
			customer.setGender(scanner.nextLine());

			List<Endereco> enderecos = customer.getEnderecos();
			for (Endereco endereco : enderecos) {
				System.out.println("Editando endereço: " + endereco);
				System.out.print("Digite o novo CEP: ");
				String cep = scanner.nextLine();
				if (!cep.isEmpty()) {
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					AddressService addressService = new AddressService(cep);
					String json = addressService.retornaJson();
					EnderecoViaCep enderecoViaCep = gson.fromJson(json, EnderecoViaCep.class);
					endereco.updateFromEnderecoViaCep(enderecoViaCep); // Método para atualizar dados do Endereco com EnderecoViaCep
				}
			}

			customerService.save(customer); // Atualiza os dados do cliente e endereço no banco
			System.out.println("Cliente atualizado com sucesso.");
		} else {
			System.out.println("Cliente não encontrado.");
		}
	}


/*------------------------------------ Adicionar Endereço ao Cliente ------------------------------------------------*/


	private void addAddress(Scanner scanner) {
		System.out.print("Digite o ID do cliente para adicionar um endereço: ");
		Long id = scanner.nextLong();
		scanner.nextLine();  // Consumir a nova linha

		Optional<Customer> optionalCustomer = customerService.findById(id);
		if (optionalCustomer.isPresent()) {
			Customer customer = optionalCustomer.get();

			System.out.print("Digite o novo CEP: ");
			String cep = scanner.nextLine();

			try {
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				AddressService addressService = new AddressService(cep);
				String json = addressService.retornaJson();
				EnderecoViaCep enderecoViaCep = gson.fromJson(json, EnderecoViaCep.class);
				Endereco endereco = new Endereco(enderecoViaCep);

				endereco.setCustomer(customer); // Associa o endereço ao cliente

				customer.getEnderecos().add(endereco); // Adiciona o endereço à lista de endereços do cliente

				customerService.save(customer);

				System.out.println("Endereço adicionado ao cliente:");
				System.out.println(endereco);
			} catch (RuntimeException | IOException | InterruptedException e) {
				System.out.println("Erro na requisição: " + e.getMessage());
			}
		} else {
			System.out.println("Cliente não encontrado.");
		}
	}
	
/*---------------------------------------------- Deletar Cliente -----------------------------------------------------------*/

		@Transactional
	private void deleteCustomer(Scanner scanner) {
		System.out.print("Digite o ID do cliente a ser excluído: ");
		Long id = scanner.nextLong();
		scanner.nextLine();  // Consumir a nova linha
		customerService.deleteById(id);
		System.out.println("Cliente excluído com sucesso.");
	}

	@Override
	public void run(String... args) throws Exception {
		displayMenu();
	}
}
