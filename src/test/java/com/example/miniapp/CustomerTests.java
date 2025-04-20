package com.example.miniapp;

import com.example.miniapp.controllers.CustomerController;
import com.example.miniapp.models.Customer;
import com.example.miniapp.repositories.CustomerRepository;
import com.example.miniapp.services.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class CustomerTests {

    /**
     * Model Tests
     */
    @Nested
    @DisplayName("Customer Model Tests")
    class CustomerModelTests {

        @Test
        @DisplayName("Test Customer Model Creation")
        void testCustomerCreation() {
            // Test default constructor (from @NoArgsConstructor)
            Customer customer1 = new Customer();
            assertNotNull(customer1);

            // Test full constructor (from @AllArgsConstructor)
            Customer customer3 = new Customer(1L, "John Smith", "john@example.com", "+201234567890");
            assertNotNull(customer3);
            assertEquals(1L, customer3.getId());
            assertEquals("John Smith", customer3.getName());
            assertEquals("john@example.com", customer3.getEmail());
            assertEquals("+201234567890", customer3.getPhoneNumber());
        }

        @Test
        @DisplayName("Test Customer Getters and Setters")
        void testCustomerGettersSetters() {
            Customer customer = new Customer();

            customer.setId(1L);
            customer.setName("John Doe");
            customer.setEmail("john@example.com");
            customer.setPhoneNumber("+201234567890");

            assertEquals(1L, customer.getId());
            assertEquals("John Doe", customer.getName());
            assertEquals("john@example.com", customer.getEmail());
            assertEquals("+201234567890", customer.getPhoneNumber());
        }

        @Test
        @DisplayName("Test Customer toString Method")
        void testToString() {
            Customer customer = new Customer(1L, "John Doe", "john@example.com", "+201234567890");
            String toString = customer.toString();

            assertTrue(toString.contains("id=1"));
            assertTrue(toString.contains("name=John Doe"));
            assertTrue(toString.contains("email=john@example.com"));
            assertTrue(toString.contains("phoneNumber=+201234567890"));
        }

        @Test
        @DisplayName("Test Customer equals and hashCode Methods")
        void testEqualsAndHashCode() {
            Customer customer1 = new Customer(1L, "John Doe", "john@example.com", "+201234567890");
            Customer customer2 = new Customer(1L, "John Doe", "john@example.com", "+201234567890");
            Customer customer3 = new Customer(2L, "Jane Doe", "jane@example.com", "+201987654321");

            // Test equals
            assertEquals(customer1, customer2);
            assertNotEquals(customer1, customer3);

            // Test hashCode
            assertEquals(customer1.hashCode(), customer2.hashCode());
            assertNotEquals(customer1.hashCode(), customer3.hashCode());
        }
    }

    /**
     * Repository Tests
     */
    @Nested
    @DisplayName("Customer Repository Tests")
    @DataJpaTest
    class CustomerRepositoryTests {

        @Autowired
        private CustomerRepository customerRepository;

        private Customer customer1;
        private Customer customer2;

        @BeforeEach
        void setUp() {
            // Clear the repository
            customerRepository.deleteAll();

            // Create test customers
            customer1 = new Customer();
            customer1.setName("John Doe");
            customer1.setEmail("john@example.com");
            customer1.setPhoneNumber("+201234567890");

            customer2 = new Customer();
            customer2.setName("Jane Doe");
            customer2.setEmail("jane@gmail.com");
            customer2.setPhoneNumber("+201987654321");

            // Save customers to repository
            customerRepository.save(customer1);
            customerRepository.save(customer2);
        }

        @Test
        @DisplayName("Test Find All Customers")
        void testFindAll() {
            List<Customer> customers = customerRepository.findAll();
            assertEquals(2, customers.size());
        }

        @Test
        @DisplayName("Test Find Customer By ID")
        void testFindById() {
            // Find customer by ID
            Optional<Customer> foundCustomer = customerRepository.findById(customer1.getId());
            assertTrue(foundCustomer.isPresent());
            assertEquals("John Doe", foundCustomer.get().getName());
        }
    }

    /**
     * Service Tests
     */
    @Nested
    @DisplayName("Customer Service Tests")
    class CustomerServiceTests {

        @Mock
        private CustomerRepository customerRepository;

        @InjectMocks
        private CustomerService customerService;

        private Customer customer1;
        private Customer customer2;

        @BeforeEach
        void setUp() {
            MockitoAnnotations.openMocks(this);

            customer1 = new Customer();
            customer1.setId(1L);
            customer1.setName("John Doe");
            customer1.setEmail("john@example.com");
            customer1.setPhoneNumber("+201234567890");

            customer2 = new Customer();
            customer2.setId(2L);
            customer2.setName("Jane Doe");
            customer2.setEmail("jane@gmail.com");
            customer2.setPhoneNumber("+201987654321");
        }

        @Test
        @DisplayName("Test Add Customer")
        void testAddCustomer() {
            when(customerRepository.save(any(Customer.class))).thenReturn(customer1);

            Customer result = customerService.addCustomer(customer1);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(customerRepository, times(1)).save(any(Customer.class));
        }

        @Test
        @DisplayName("Test Get All Customers")
        void testGetAllCustomers() {
            when(customerRepository.findAll()).thenReturn(Arrays.asList(customer1, customer2));

            List<Customer> result = customerService.getAllCustomers();

            assertNotNull(result);
            assertEquals(2, result.size());
            verify(customerRepository, times(1)).findAll();
        }

        @Test
        @DisplayName("Test Get Customer By ID - Found")
        void testGetCustomerByIdFound() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));

            Customer result = customerService.getCustomerById(1L);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            verify(customerRepository, times(1)).findById(1L);
        }

        @Test
        @DisplayName("Test Get Customer By ID - Not Found")
        void testGetCustomerByIdNotFound() {
            when(customerRepository.findById(3L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                customerService.getCustomerById(3L);
            });

            verify(customerRepository, times(1)).findById(3L);
        }

        @Test
        @DisplayName("Test Update Customer - Found")
        void testUpdateCustomerFound() {
            Customer updatedCustomer = new Customer();
            updatedCustomer.setName("John Updated");
            updatedCustomer.setEmail("john.updated@example.com");
            updatedCustomer.setPhoneNumber("+201234567899");

            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
            when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));

            Customer result = customerService.updateCustomer(1L, updatedCustomer);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("John Updated", result.getName());
            assertEquals("john.updated@example.com", result.getEmail());
            assertEquals("+201234567899", result.getPhoneNumber());

            verify(customerRepository, times(1)).findById(1L);
            verify(customerRepository, times(1)).save(any(Customer.class));
        }

        @Test
        @DisplayName("Test Update Customer - Not Found")
        void testUpdateCustomerNotFound() {
            Customer updatedCustomer = new Customer();
            updatedCustomer.setName("John Updated");

            when(customerRepository.findById(3L)).thenReturn(Optional.empty());

            assertThrows(RuntimeException.class, () -> {
                customerService.updateCustomer(3L, updatedCustomer);
            });

            verify(customerRepository, times(1)).findById(3L);
            verify(customerRepository, never()).save(any(Customer.class));
        }

        @Test
        @DisplayName("Test Delete Customer - Found")
        void testDeleteCustomerFound() {
            when(customerRepository.findById(1L)).thenReturn(Optional.of(customer1));
            doNothing().when(customerRepository).delete(any(Customer.class));

            String result = customerService.deleteCustomer(1L);

            assertEquals("Customer deleted successfully", result);
            verify(customerRepository, times(1)).findById(1L);
            verify(customerRepository, times(1)).delete(any(Customer.class));
        }

        @Test
        @DisplayName("Test Delete Customer - Not Found")
        void testDeleteCustomerNotFound() {
            when(customerRepository.findById(3L)).thenReturn(Optional.empty());

            String result = customerService.deleteCustomer(3L);

            assertEquals("Customer not found", result);
            verify(customerRepository, times(1)).findById(3L);
            verify(customerRepository, never()).delete(any(Customer.class));
        }
    }

    /**
     * Controller Tests
     */
    @Nested
    @DisplayName("Customer Controller Tests")
    @WebMvcTest(CustomerController.class)
    class CustomerControllerTests {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private CustomerService customerService;

        @Autowired
        private ObjectMapper objectMapper;

        private Customer customer1;
        private Customer customer2;

        @BeforeEach
        void setUp() {
            customer1 = new Customer();
            customer1.setId(1L);
            customer1.setName("John Doe");
            customer1.setEmail("john@example.com");
            customer1.setPhoneNumber("+201234567890");

            customer2 = new Customer();
            customer2.setId(2L);
            customer2.setName("Jane Doe");
            customer2.setEmail("jane@gmail.com");
            customer2.setPhoneNumber("+201987654321");
        }

        @Test
        @DisplayName("Test Add Customer Endpoint")
        void testAddCustomer() throws Exception {
            when(customerService.addCustomer(any(Customer.class))).thenReturn(customer1);

            mockMvc.perform(post("/customer/addCustomer")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")))
                    .andExpect(jsonPath("$.email", is("john@example.com")))
                    .andExpect(jsonPath("$.phoneNumber", is("+201234567890")));

            verify(customerService, times(1)).addCustomer(any(Customer.class));
        }

        @Test
        @DisplayName("Test Get All Customers Endpoint")
        void testGetAllCustomers() throws Exception {
            when(customerService.getAllCustomers()).thenReturn(Arrays.asList(customer1, customer2));

            mockMvc.perform(get("/customer/allCustomers"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].name", is("John Doe")))
                    .andExpect(jsonPath("$[1].id", is(2)))
                    .andExpect(jsonPath("$[1].name", is("Jane Doe")));

            verify(customerService, times(1)).getAllCustomers();
        }

        @Test
        @DisplayName("Test Get Customer By ID Endpoint")
        void testGetCustomerById() throws Exception {
            when(customerService.getCustomerById(1L)).thenReturn(customer1);

            mockMvc.perform(get("/customer/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")));

            verify(customerService, times(1)).getCustomerById(1L);
        }

        @Test
        @DisplayName("Test Update Customer Endpoint")
        void testUpdateCustomer() throws Exception {
            when(customerService.updateCustomer(eq(1L), any(Customer.class))).thenReturn(customer1);

            mockMvc.perform(put("/customer/update/{id}", 1L)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(customer1)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("John Doe")));

            verify(customerService, times(1)).updateCustomer(eq(1L), any(Customer.class));
        }

        @Test
        @DisplayName("Test Delete Customer Endpoint")
        void testDeleteCustomer() throws Exception {
            when(customerService.deleteCustomer(1L)).thenReturn("Customer deleted successfully");

            mockMvc.perform(delete("/customer/delete/{id}", 1L))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Customer deleted successfully"));

            verify(customerService, times(1)).deleteCustomer(1L);
        }

        @Test
        @DisplayName("Test Find Customers By Email Domain Endpoint")
        void testFindCustomersByEmailDomain() throws Exception {
            when(customerService.findCustomersByEmailDomain("gmail.com")).thenReturn(List.of(customer2));

            mockMvc.perform(get("/customer/findByEmailDomain")
                            .param("domain", "gmail.com"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(2)))
                    .andExpect(jsonPath("$[0].email", is("jane@gmail.com")));

            verify(customerService, times(1)).findCustomersByEmailDomain("gmail.com");
        }

        @Test
        @DisplayName("Test Find Customers By Phone Prefix Endpoint")
        void testFindCustomersByPhonePrefix() throws Exception {
            when(customerService.findCustomersByPhonePrefix("+2012")).thenReturn(List.of(customer1));

            mockMvc.perform(get("/customer/findByPhonePrefix")
                            .param("prefix", "+2012"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id", is(1)))
                    .andExpect(jsonPath("$[0].phoneNumber", is("+201234567890")));

            verify(customerService, times(1)).findCustomersByPhonePrefix("+2012");
        }
    }
}