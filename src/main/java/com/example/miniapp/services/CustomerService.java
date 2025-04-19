package com.example.miniapp.services;

import com.example.miniapp.models.Customer;
import com.example.miniapp.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer createCustomer(Customer customer) {
        // Add validation or business logic if needed
        return customerRepository.save(customer);
    }

    public Optional<Customer> updateCustomer(Long id, Customer customerDetails) {
        return customerRepository.findById(id)
            .map(existingCustomer -> {
                existingCustomer.setName(customerDetails.getName());
                existingCustomer.setEmail(customerDetails.getEmail());
                existingCustomer.setPhoneNumber(customerDetails.getPhoneNumber());
                // Note: Handling the 'trips' list update might require more specific logic
                return customerRepository.save(existingCustomer);
            });
    }

    public boolean deleteCustomer(Long id) {
        return customerRepository.findById(id)
            .map(customer -> {
                customerRepository.delete(customer);
                return true;
            }).orElse(false);
    }

    // Example using custom repository method
    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }
}
