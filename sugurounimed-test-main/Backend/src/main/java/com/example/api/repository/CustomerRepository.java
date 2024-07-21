package com.example.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.api.domain.Customer;
import org.springframework.data.repository.PagingAndSortingRepository;
import java.util.Optional;

public interface CustomerRepository extends JpaRepository<Customer, Long>, PagingAndSortingRepository<Customer, Long> {

	Optional <Customer> findByName(String name);
	Optional <Customer> findByEmail(String email);
	Optional <Customer> findByGender(String gender);
}
