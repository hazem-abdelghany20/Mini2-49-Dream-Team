package com.example.miniapp.repositories;

import com.example.miniapp.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    // Example custom query method: Find a customer by email
    Optional<Customer> findByEmail(String email);

    // You can add more custom query methods here as needed
    // For example, find customers by phone number
    // Optional<Customer> findByPhoneNumber(String phoneNumber);
}
