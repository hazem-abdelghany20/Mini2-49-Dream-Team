package com.example.miniapp;

import com.example.miniapp.controllers.CustomerController;
import com.example.miniapp.models.Customer;
import com.example.miniapp.repositories.CustomerRepository;
import com.example.miniapp.services.CustomerService;

import org.junit.jupiter.api.*;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CustomerTests {

    // -------------------- Model --------------------
    @Test
    void testCustomerModel() {
        Customer customer = new Customer(1L, "John", "john@example.com", "0123456789");
        assertEquals("John", customer.getName());
        assertEquals("john@example.com", customer.getEmail());
    }

    @Test
    void testCustomerEquals() {
        Customer c1 = new Customer(1L, "John", "john@example.com", "0123456789");
        Customer c2 = new Customer(1L, "John", "john@example.com", "0123456789");
        assertEquals(c1, c2);
    }

    // -------------------- Service --------------------
    @Mock CustomerRepository customerRepository;

    @InjectMocks CustomerService customerService;

    @BeforeEach
    void setUpService() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddCustomerService() {
        Customer customer = new Customer(null, "Jane", "jane@gmail.com", "0112233445");
        when(customerRepository.save(customer)).thenReturn(customer);
        assertEquals(customer, customerService.addCustomer(customer));
    }

    @Test
    void testUpdateCustomerService() {
        Customer existing = new Customer(1L, "Old", "old@x.com", "0100000000");
        Customer updated = new Customer(1L, "New", "new@x.com", "0123456789");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(existing)).thenReturn(updated);

        Customer result = customerService.updateCustomer(1L, updated);
        assertEquals("New", result.getName());
    }

    // -------------------- Controller --------------------
    @Test
    void testCustomerControllerAdd() {
        CustomerService mockService = mock(CustomerService.class);
        Customer customer = new Customer(null, "Jane", "jane@gmail.com", "0112233445");
        when(mockService.addCustomer(customer)).thenReturn(customer);

        CustomerController controller = new CustomerController(mockService);
        ResponseEntity<Customer> response = controller.addCustomer(customer);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Jane", response.getBody().getName());
    }

    @Test
    void testCustomerControllerGetAll() {
        CustomerService mockService = mock(CustomerService.class);
        List<Customer> sample = List.of(new Customer(1L, "Jane", "jane@gmail.com", "0112233445"));
        when(mockService.getAllCustomers()).thenReturn(sample);

        CustomerController controller = new CustomerController(mockService);
        ResponseEntity<List<Customer>> response = controller.getAllCustomers();

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
    }
}
