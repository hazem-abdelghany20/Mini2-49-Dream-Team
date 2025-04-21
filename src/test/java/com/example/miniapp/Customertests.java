package com.example.miniapp;

import com.example.miniapp.controllers.CustomerController;  // :contentReference[oaicite:0]{index=0}&#8203;:contentReference[oaicite:1]{index=1}
import com.example.miniapp.models.Customer;                // :contentReference[oaicite:2]{index=2}&#8203;:contentReference[oaicite:3]{index=3}
import com.example.miniapp.repositories.CustomerRepository;// :contentReference[oaicite:4]{index=4}&#8203;:contentReference[oaicite:5]{index=5}
import com.example.miniapp.services.CustomerService;        // :contentReference[oaicite:6]{index=6}&#8203;:contentReference[oaicite:7]{index=7}

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class Customertests {

    // ---------- Service Layer Tests ----------
    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void testGetAllCustomers() {
        List<Customer> stubList = Arrays.asList(
                new Customer(1L, "Alice", "alice@example.com", "0123456789"),
                new Customer(2L, "Bob",   "bob@example.com",   "0987654321")
        );
        when(customerRepository.findAll()).thenReturn(stubList);

        List<Customer> result = customerService.getAllCustomers();
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void testGetCustomerById_WhenExists() {
        Customer c = new Customer(1L, "Alice", "alice@example.com", "0123456789");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(c));

        Customer result = customerService.getCustomerById(1L);
        assertEquals("Alice", result.getName());
    }

    @Test
    void testGetCustomerById_WhenNotFound() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> customerService.getCustomerById(999L)
        );
        assertTrue(ex.getMessage().contains("Customer not found with id 999"));
    }

    @Test
    void testAddCustomer() {
        Customer toAdd = new Customer(null, "Carol", "carol@example.com", "0112233445");
        Customer saved = new Customer(3L, "Carol", "carol@example.com", "0112233445");
        when(customerRepository.save(toAdd)).thenReturn(saved);

        Customer result = customerService.addCustomer(toAdd);
        assertEquals(3L, result.getId());
        assertEquals("Carol", result.getName());
    }

    @Test
    void testUpdateCustomer_WhenExists() {
        Customer existing = new Customer(1L, "Alice", "alice@old.com", "0000000000");
        Customer updates  = new Customer(null, "Alice A.", "alice@new.com", "0111111111");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.save(any(Customer.class))).thenAnswer(i -> i.getArgument(0));

        Customer result = customerService.updateCustomer(1L, updates);
        assertEquals("Alice A.", result.getName());
        assertEquals("alice@new.com", result.getEmail());
    }

    @Test
    void testDeleteCustomer_WhenExists() {
        Customer existing = new Customer(1L, "Bob", "bob@example.com", "0987654321");
        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));

        String msg = customerService.deleteCustomer(1L);
        assertEquals("Customer deleted successfully", msg);
        verify(customerRepository, times(1)).delete(existing);
    }

    @Test
    void testFindCustomersByEmailDomain() {
        List<Customer> stub = List.of(new Customer(1L, "Foo", "foo@bar.com", "01"));
        when(customerRepository.findByEmail("bar.com")).thenReturn(stub);
        List<Customer> result = customerService.findCustomersByEmailDomain("bar.com");
        assertEquals(1, result.size());
        verify(customerRepository).findByEmail("bar.com");
    }

    @Test
    void testFindCustomersByPhonePrefix() {
        List<Customer> stub = List.of(new Customer(2L, "Bar", "bar@baz.com", "091234"));
        when(customerRepository.findByPhoneNumber("091")).thenReturn(stub);
        List<Customer> result = customerService.findCustomersByPhonePrefix("091");
        assertEquals("091234", result.get(0).getPhoneNumber());
        verify(customerRepository).findByPhoneNumber("091");
    }


    // ---------- Controller Layer Tests ----------
    private MockMvc mockMvc;

    @Mock
    private CustomerService mockService;

    @BeforeEach
    void setupController() {
        CustomerController controller = new CustomerController(mockService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void controller_getAllCustomers() throws Exception {
        List<Customer> stubList = Arrays.asList(
                new Customer(1L, "Alice", "alice@example.com", "0123456789"),
                new Customer(2L, "Bob",   "bob@example.com",   "0987654321")
        );
        when(mockService.getAllCustomers()).thenReturn(stubList);

        mockMvc.perform(get("/customer/allCustomers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Alice"));
    }

    @Test
    void controller_addCustomer() throws Exception {
        Customer input = new Customer(null, "Carol", "carol@example.com", "0112233445");
        Customer returned = new Customer(3L, "Carol", "carol@example.com", "0112233445");
        when(mockService.addCustomer(any(Customer.class))).thenReturn(returned);

        mockMvc.perform(post("/customer/addCustomer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(input))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.email").value("carol@example.com"));
    }

    @Test
    void controller_getCustomerById() throws Exception {
        Customer c = new Customer(1L, "Alice", "alice@example.com", "0123456789");
        when(mockService.getCustomerById(1L)).thenReturn(c);

        mockMvc.perform(get("/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Alice"));
    }

    @Test
    void controller_updateCustomer() throws Exception {
        Customer updates = new Customer(null, "Alice A.", "alice@new.com", "0111111111");
        Customer updated = new Customer(1L, "Alice A.", "alice@new.com", "0111111111");
        when(mockService.updateCustomer(eq(1L), any(Customer.class))).thenReturn(updated);

        mockMvc.perform(put("/customer/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updates))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("alice@new.com"));
    }

    @Test
    void controller_deleteCustomer() throws Exception {
        when(mockService.deleteCustomer(1L)).thenReturn("Customer deleted successfully");

        mockMvc.perform(delete("/customer/delete/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Customer deleted successfully"));
    }

    @Test
    void controller_findByEmailDomain() throws Exception {
        List<Customer> stub = Arrays.asList(
                new Customer(1L, "Alice", "alice@domain.com", "0123456789")
        );
        when(mockService.findCustomersByEmailDomain("domain.com")).thenReturn(stub);

        mockMvc.perform(get("/customer/findByEmailDomain")
                        .param("domain", "domain.com")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("alice@domain.com"));
    }

    @Test
    void controller_findByPhonePrefix() throws Exception {
        List<Customer> stub = Arrays.asList(
                new Customer(2L, "Bob", "bob@example.com", "0933333333")
        );
        when(mockService.findCustomersByPhonePrefix("093")).thenReturn(stub);

        mockMvc.perform(get("/customer/findByPhonePrefix")
                        .param("prefix", "093")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].phoneNumber").value("0933333333"));
    }
}

