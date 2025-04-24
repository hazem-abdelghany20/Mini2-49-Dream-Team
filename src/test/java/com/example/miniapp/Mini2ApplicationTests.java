package com.example.miniapp;

import org.springframework.beans.factory.annotation.Value;
import com.example.miniapp.models.*;
import com.example.miniapp.repositories.*;
import com.example.miniapp.services.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.*;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Sql(statements = {
        // Drop tables if they exist — order matters due to FK constraints
        "DROP TABLE IF EXISTS payments;",
        "DROP TABLE IF EXISTS trips;",
        "DROP TABLE IF EXISTS customers;",
        "DROP TABLE IF EXISTS captains;",

        // Create captains table
        "CREATE TABLE captains (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "license_number VARCHAR(50) NOT NULL UNIQUE, " +
                "avg_rating_score DOUBLE PRECISION" +
                ");",

        // Create customers table
        "CREATE TABLE customers (" +
                "id SERIAL PRIMARY KEY, " +
                "name VARCHAR(255) NOT NULL, " +
                "email VARCHAR(255) NOT NULL UNIQUE, " +
                "phone_number VARCHAR(15) NOT NULL" +
                ");",

        // Create trips table — note FK references captains and customers
        "CREATE TABLE trips (" +
                "id SERIAL PRIMARY KEY, " +
                "trip_date TIMESTAMP NOT NULL, " +
                "origin VARCHAR(255) NOT NULL, " +
                "destination VARCHAR(255) NOT NULL, " +
                "trip_cost DOUBLE PRECISION NOT NULL, " +
                "captain_id INT REFERENCES captains(id), " +
                "customer_id INT REFERENCES customers(id)" +  // optional, can add REFERENCES customers(id) if needed
                ");",

        // Create payments table — FK to trips
        "CREATE TABLE payments (" +
                "id SERIAL PRIMARY KEY, " +
                "amount DOUBLE PRECISION NOT NULL, " +
                "payment_method VARCHAR(50) NOT NULL, " +
                "payment_status BOOLEAN NOT NULL, " +
                "trip_id INT REFERENCES trips(id)" +
                ");",

}, executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)

class Mini2ApplicationTests {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CaptainService captainService;
    @Autowired
    private CaptainRepository captainRepository;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private TripService tripService;
    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private RatingService ratingService;
    @Autowired
    private RatingRepository ratingRepository;

    private final String BASE_URL_CAPTAIN = "http://localhost:8080/captain";
    private final String BASE_URL_CUSTOMER = "http://localhost:8080/customer";
    private final String BASE_URL_PAYMENT = "http://localhost:8080/payment";
    private final String BASE_URL_TRIP = "http://localhost:8080/trip";
    private final String BASE_URL_RATING = "http://localhost:8080/rating";

    @Value("${ModelsPath.Captain}")
    String CaptainPath;

    @Value("${ModelsPath.Customer}")
    String CustomerPath;

    @Value("${ModelsPath.Payment}")
    String PaymentPath;

    @Value("${ModelsPath.Rating}")
    String RatingPath;

    @Value("${ModelsPath.Trip}")
    String TripPath;

    @BeforeEach
    public void setup() {
        // Ensure all records are deleted

        // paymentRepository.deleteAll();
        // tripRepository.deleteAll();
        ratingRepository.deleteAll();
        // captainRepository.deleteAll();
        // customerRepository.deleteAll();
    }
    public static Field findFieldIgnoreCase(Class<?> clazz, String fieldName) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                return field;
            }
        }
        return null; // Field not found
    }

    Field getID(String ClassPath) throws ClassNotFoundException, NoSuchFieldException {
        Field ID = findFieldIgnoreCase(Class.forName(ClassPath),"id");
        ID.setAccessible(true);
        return ID;
    }

    @Test
    void contextLoads() {

    }
    @Test
    public void testControllerAddCaptain() {
        Captain newCaptain = new Captain("John Doe", "54321", 4.5);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Captain> request = new HttpEntity<>(newCaptain, headers);

        ResponseEntity<Captain> response = restTemplate.postForEntity(BASE_URL_CAPTAIN + "/addCaptain", request, Captain.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newCaptain.getName(), response.getBody().getName());
    }

    @Test
    public void testControllerGetAllCaptains() {
        ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL_CAPTAIN + "/allCaptains", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }
    @Test
    public void testControllerGetCaptainById() {
        Captain captain = new Captain("Anna Taylor", "67890", 3.8);
        captainService.addCaptain(captain); // Add to database for controller testing
        ResponseEntity<Captain> response = restTemplate.getForEntity(BASE_URL_CAPTAIN + "/" + captain.getId(), Captain.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(captain.getName(), response.getBody().getName());
    }

    @Test
    public void testControllerGetCaptainsByRating() {
        // Add sample captains to the database
        captainService.addCaptain(new Captain("Captain A", "123", 4.5));
        captainService.addCaptain(new Captain("Captain B", "456", 3.2));

        // Set the rating threshold
        double ratingThreshold = 4.0;

        // Make the GET request to the endpoint
        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_CAPTAIN + "/filterByRating?ratingThreshold=" + ratingThreshold,
                List.class
        );

        // Validate the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testControllerGetAllTrips() {
        ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL_TRIP + "/allTrips", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }


    @Test
    public void testControllerAddCustomer() {
        Customer newCustomer = new Customer("John Doe", "john@example.com", "1234567890");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Customer> request = new HttpEntity<>(newCustomer, headers);

        ResponseEntity<Customer> response = restTemplate.postForEntity(BASE_URL_CUSTOMER + "/addCustomer", request, Customer.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newCustomer.getName(), response.getBody().getName());
    }

    @Test
    public void testControllerGetAllCustomers() {
        ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL_CUSTOMER + "/allCustomers", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testControllerGetCustomerById() {
        Customer customer = new Customer("Anna Taylor", "anna@example.com", "5555555555");
        customerService.addCustomer(customer); // Add to database for controller testing
        ResponseEntity<Customer> response = restTemplate.getForEntity(BASE_URL_CUSTOMER + "/" + customer.getId(), Customer.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(customer.getName(), response.getBody().getName());
    }

    @Test
    public void testControllerUpdateCustomer() {
        Customer customer = new Customer("Mark Taylor", "mark@example.com", "6666666666");
        customerService.addCustomer(customer);

        customer.setName("Mark Updated");
        customer.setEmail("mark_updated@example.com");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Customer> request = new HttpEntity<>(customer, headers);

        ResponseEntity<Customer> response = restTemplate.exchange(
                BASE_URL_CUSTOMER + "/update/" + customer.getId(),
                HttpMethod.PUT,
                request,
                Customer.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Mark Updated", response.getBody().getName());
    }

    @Test
    public void testControllerDeleteCustomer() {
        Customer customer = new Customer("Sarah Connor", "sarah@example.com", "7777777777");
        customerService.addCustomer(customer);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL_CUSTOMER + "/delete/" + customer.getId(),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testControllerDeleteNonExistingCustomer() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL_CUSTOMER + "/delete/" + 1,
                HttpMethod.DELETE,
                null,
                String.class
        );

    }

    @Test
    public void testControllerFindCustomersByEmailDomain() {
        customerService.addCustomer(new Customer("Alice", "alice@domain.com", "1111111111"));
        customerService.addCustomer(new Customer("Bob", "bob@domain.com", "2222222222"));

        String domain = "domain.com";
        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_CUSTOMER + "/findByEmailDomain?domain=" + domain,
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testControllerFindCustomersByPhonePrefix() {
        customerService.addCustomer(new Customer("Alice", "alice@example.com", "5551111111"));
        customerService.addCustomer(new Customer("Bob", "bob@example.com", "5552222222"));

        String prefix = "555";
        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_CUSTOMER + "/findByPhonePrefix?prefix=" + prefix,
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testControllerAddPayment() {
        Payment newPayment = new Payment(120.0, "Bank Transfer", true);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Payment> request = new HttpEntity<>(newPayment, headers);

        ResponseEntity<Payment> response = restTemplate.postForEntity(BASE_URL_PAYMENT + "/addPayment", request, Payment.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newPayment.getAmount(), response.getBody().getAmount());
    }

    @Test
    public void testControllerGetAllPayments() {
        ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL_PAYMENT + "/allPayments", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testControllerGetPaymentById() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Payment payment = new Payment(200.0, "Credit Card", true);
        paymentService.addPayment(payment); // Add to database for controller testing
        ResponseEntity<Payment> response = restTemplate.getForEntity(BASE_URL_PAYMENT + "/" + ((Long) getID(PaymentPath).get(payment)), Payment.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(payment.getAmount(), response.getBody().getAmount());
    }


    @Test
    public void testControllerUpdatePayment() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Payment payment = new Payment(250.0, "PayPal", true);
        paymentService.addPayment(payment);

        payment.setAmount(500.0);
        payment.setPaymentMethod("Updated PayPal");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Payment> request = new HttpEntity<>(payment, headers);

        ResponseEntity<Payment> response = restTemplate.exchange(
                BASE_URL_PAYMENT + "/update/" + ((Long) getID(PaymentPath).get(payment)),
                HttpMethod.PUT,
                request,
                Payment.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(500.0, response.getBody().getAmount());
    }


    @Test
    public void testControllerDeletePayment() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Payment payment = new Payment(300.0, "Cash", false);
        paymentService.addPayment(payment);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL_PAYMENT + "/delete/" + ((Long) getID(PaymentPath).get(payment)),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testControllerDeleteNonExistingPayment() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL_PAYMENT + "/delete/" + 1,
                HttpMethod.DELETE,
                null,
                String.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testControllerFindPaymentsByAmountThreshold() {
        Payment payment = new Payment(550.0, "Credit Card", true);
        paymentService.addPayment(payment);

        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_PAYMENT + "/findByAmountThreshold?threshold=300.0",
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().size() > 0);
    }

    @Test
    public void testControllerFindPaymentsWithAmountGreaterThan() {
        paymentService.addPayment(new Payment(100.0, "Card", true));
        paymentService.addPayment(new Payment(200.0, "Cash", true));

        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_PAYMENT + "/findByAmountThreshold?threshold=150.0",
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        // assertTrue(response.getBody().size() >= 1);
    }

    @Test
    public void testControllerGetCaptainByLicenseNumberFound() {
        Captain captain = captainService.addCaptain(new Captain("Test Captain 2", "LC456", 4.2));
        ResponseEntity<Captain> response = restTemplate.getForEntity(
                BASE_URL_CAPTAIN + "/filterByLicenseNumber?licenseNumber=LC456",
                Captain.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Test Captain 2", response.getBody().getName());
    }

    @Test
    public void testControllerGetCaptainByLicenseNumberNotFound() {
        ResponseEntity<Captain> response = restTemplate.getForEntity(
                BASE_URL_CAPTAIN + "/filterByLicenseNumber?licenseNumber=NONEXISTENT",
                Captain.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode()); // Or potentially HttpStatus.NOT_FOUND depending on your controller logic
        assertNull(response.getBody()); // Or handle the 404 case appropriately
    }

    @Test
    public void testControllerFindRatingsByEntityNotFound() {
        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_RATING + "/findByEntity?entityId=999&entityType=captain",
                List.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testControllerFindRatingsAboveScoreNoneFound() {
        ratingService.addRating(new Rating(7L, "customer", 2, "Bad.", LocalDateTime.now()));
        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_RATING + "/findAboveScore?minScore=5",
                List.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testControllerFindTripsWithinDateRangeNoResults() {
        LocalDateTime now = LocalDateTime.now();
        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_TRIP + "/findByDateRange?startDate=" + now.plusDays(7).toString() + "&endDate=" + now.plusDays(8).toString(),
                List.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testControllerFindTripsByCaptainIdNotFound() {
        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_TRIP + "/findByCaptainId?captainId=999",
                List.class
        );
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isEmpty());
    }

    @Test
    public void testControllerDeleteNonExistingTrip() {
        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL_TRIP + "/delete/" + 1,
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testControllerAddRating() {
        Rating newRating = new Rating(1L, "customer", 5, "Excellent service!", LocalDateTime.now());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Rating> request = new HttpEntity<>(newRating, headers);

        ResponseEntity<Rating> response = restTemplate.postForEntity(BASE_URL_RATING + "/addRating", request, Rating.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newRating.getScore(), response.getBody().getScore());
    }

    @Test
    public void testControllerUpdateRating() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Rating rating = new Rating(2L, "customer", 4, "Good service.", LocalDateTime.now());
        ratingService.addRating(rating);

        rating.setComment("Updated service.");
        rating.setScore(5);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Rating> request = new HttpEntity<>(rating, headers);

        ResponseEntity<Rating> response = restTemplate.exchange(
                BASE_URL_RATING + "/update/" + (getID(RatingPath).get(rating)),
                HttpMethod.PUT,
                request,
                Rating.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(5, response.getBody().getScore());
    }

    @Test
    public void testControllerDeleteRating() throws NoSuchFieldException, ClassNotFoundException, IllegalAccessException {
        Rating rating = new Rating(3L, "captain", 3, "Average captain.", LocalDateTime.now());
        ratingService.addRating(rating);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL_RATING + "/delete/" + getID(RatingPath).get(rating),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testControllerFindRatingsByEntity() {
        Rating rating1 = new Rating(4L, "trip", 5, "Excellent trip!", LocalDateTime.now());
        Rating rating2 = new Rating(4L, "trip", 4, "Good trip!", LocalDateTime.now());
        ratingService.addRating(rating1);
        ratingService.addRating(rating2);

        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_RATING + "/findByEntity?entityId=4&entityType=trip",
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testControllerFindRatingsByNonExistingEntity() {
        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_RATING + "/findByEntity?entityId=4&entityType=trip",
                List.class
        );

        assertEquals(response.getBody().size(),0);
    }

    @Test
    public void testControllerFindRatingsAboveScore() {
        Rating rating1 = new Rating(5L, "customer", 3, "Okay service.", LocalDateTime.now());
        Rating rating2 = new Rating(6L, "customer", 5, "Awesome service.", LocalDateTime.now());
        ratingService.addRating(rating1);
        ratingService.addRating(rating2);

        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_RATING + "/findAboveScore?minScore=4",
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    public void testControllerGetCaptainsByRatingNoResults() {
        captainService.addCaptain(new Captain("Low Rating", "LR789", 2.0));
        ResponseEntity<List> response = restTemplate.getForEntity(BASE_URL_CAPTAIN + "/filterByRating?ratingThreshold=4.0", List.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    @Test
    public void testControllerAddTrip() {
        Trip newTrip = new Trip(LocalDateTime.now(), "Origin A", "Destination A", 100.0);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Trip> request = new HttpEntity<>(newTrip, headers);

        ResponseEntity<Trip> response = restTemplate.postForEntity(BASE_URL_TRIP + "/addTrip", request, Trip.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(newTrip.getOrigin(), response.getBody().getOrigin());
    }

    @Test
    public void testControllerGetTripById() {
        Trip trip = new Trip(LocalDateTime.now(), "Origin B", "Destination B", 200.0);
        tripService.addTrip(trip); // Add to database for controller testing
        ResponseEntity<Trip> response = restTemplate.getForEntity(BASE_URL_TRIP + "/" + trip.getId(), Trip.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(trip.getOrigin(), response.getBody().getOrigin());
    }

    @Test
    public void testControllerUpdateTrip() {
        Trip trip = new Trip(LocalDateTime.now(), "Origin C", "Destination C", 300.0);
        tripService.addTrip(trip);

        trip.setDestination("Updated Destination");
        trip.setTripCost(400.0);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Trip> request = new HttpEntity<>(trip, headers);

        ResponseEntity<Trip> response = restTemplate.exchange(
                BASE_URL_TRIP + "/update/" + trip.getId(),
                HttpMethod.PUT,
                request,
                Trip.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Updated Destination", response.getBody().getDestination());
    }

    @Test
    public void testControllerDeleteTrip() {
        Trip trip = new Trip(LocalDateTime.now(), "Origin D", "Destination D", 500.0);
        tripService.addTrip(trip);

        ResponseEntity<String> response = restTemplate.exchange(
                BASE_URL_TRIP + "/delete/" + trip.getId(),
                HttpMethod.DELETE,
                null,
                String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testControllerFindTripsWithinDateRange() {
        Trip trip = new Trip(LocalDateTime.now().minusDays(1), "Origin E", "Destination E", 150.0);
        tripService.addTrip(trip);

        LocalDateTime startDate = LocalDateTime.now().minusDays(2);
        LocalDateTime endDate = LocalDateTime.now();

        ResponseEntity<List> response = restTemplate.getForEntity(
                BASE_URL_TRIP + "/findByDateRange?startDate=" + startDate + "&endDate=" + endDate,
                List.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }


    @Test
    public void testServiceAddCaptain() {
        Captain captain = new Captain("Jane Doe", "98765", 4.9);
        Captain savedCaptain = captainService.addCaptain(captain);
        assertNotNull(savedCaptain);
        assertEquals("Jane Doe", savedCaptain.getName());
    }

    @Test
    public void testServiceAddNullCaptain() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            captainService.addCaptain(null);
        });
    }

    @Test
    public void testServiceGetAllEmptyCaptains() {
        List<Captain> captains = captainService.getAllCaptains();
        assertNotNull(captains);
        assertTrue(captains.isEmpty());
    }

    @Test
    public void testServiceGetAllCaptains() {
        Captain captain1 = new Captain("Captain A", "12345", 4.5);
        Captain captain2 = new Captain("Captain B", "67890", 4.8);
        captainService.addCaptain(captain1);
        captainService.addCaptain(captain2);
        List<Captain> captains = captainService.getAllCaptains();
        assertNotNull(captains);
        assertTrue(captains.size()==2);
    }

    @Test
    public void testServiceGetNullCaptainById() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> {
            captainService.getCaptainById(null);
        });
    }

    @Test
    public void testServiceGetCaptainsByRating() {
        // Add sample captains
        Captain captain1 = new Captain("Captain A", "12345", 4.5);
        Captain captain2 = new Captain("Captain B", "67890", 4.8);
        captainService.addCaptain(captain1);
        captainService.addCaptain(captain2);

        // Act: Retrieve captains with rating above threshold
        Double ratingThreshold = 4.0;
        List<Captain> retrievedCaptains = captainService.getCaptainsByRating(ratingThreshold);

        // Assert: Check that the correct captains are retrieved
        assertNotNull(retrievedCaptains);
        assertEquals(2, retrievedCaptains.size());
        assertTrue(retrievedCaptains.get(0).getName().equals("Captain A"));
        assertTrue(retrievedCaptains.get(1).getName().equals("Captain B"));
    }

    @Test
    public void testServiceGetFromEmptyListCaptainsByRating() {
        // Act: Retrieve captains with rating above threshold
        Double ratingThreshold = 4.0;
        List<Captain> retrievedCaptains = captainService.getCaptainsByRating(ratingThreshold);

        // Assert: Check that the correct captains are retrieved
        assertNotNull(retrievedCaptains);
        assertEquals(0, retrievedCaptains.size());
    }

    @Test
    public void testServiceAddCustomer() {
        Customer customer = new Customer("Jane Doe", "jane@example.com", "1234567890");
        Customer savedCustomer = customerService.addCustomer(customer);
        assertNotNull(savedCustomer);
        assertEquals("Jane Doe", savedCustomer.getName());
    }

    @Test
    public void testServiceGetAllCustomers() {
        Customer customer1 = new Customer("Alice", "alice@domain.com", "1111111111");
        Customer customer2 = new Customer("Bob", "bob@domain.com", "2222222222");
        customerService.addCustomer(customer1);
        customerService.addCustomer(customer2);
        List<Customer> customers = customerService.getAllCustomers();
        assertNotNull(customers);
        assertTrue(customers.size()==2);
    }

}
