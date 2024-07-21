package com.example.api.web.rest;

import com.example.api.domain.Customer;
import com.example.api.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = "http://127.0.0.1:5500/index.html")
public class CustomerController {

	private CustomerService service;

	@Autowired
	public CustomerController(CustomerService service) {
		this.service = service;
	}


	@GetMapping
	public List<Customer> findAll() {
		return service.findAll();
	}


	@GetMapping("/paged")
	public Page<Customer> findAllPaged(Pageable pageable) {
		return service.findAll(pageable);
	}


	@GetMapping("/{id}")
	public Customer findById(@PathVariable Long id) {
		return service.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
	}

	@GetMapping("/email/{email}")
	public Customer findByEmail(@PathVariable String email) {
		return service.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
	}

	@GetMapping("/name/{name}")
	public Customer findByName(@PathVariable String name) {
		return service.findByName(name)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
	}

	@GetMapping("/gender/{gender}")
	public Customer findByGender(@PathVariable String gender) {
		return service.findByGender(gender)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente não encontrado"));
	}
	@PostMapping
	public Customer createCustomer(@RequestBody Customer customer) {
		return service.save(customer);
	}

	@DeleteMapping("/{id}")
	public void deleteCustomer(@PathVariable Long id) {
		service.deleteById(id);
	}
}
