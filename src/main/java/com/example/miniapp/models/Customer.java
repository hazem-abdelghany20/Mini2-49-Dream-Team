package com.example.miniapp.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a customer in the ride-sharing application.
 * This class stores customer information and their associated trips.
 */
@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<Trip> trips = new ArrayList<>();

    /**
     * Default constructor required by JPA
     */
    public Customer() {
        // Required by JPA
    }

    /**
     * Partial constructor with essential fields
     *
     * @param name Customer's name
     * @param email Customer's email
     */
    public Customer(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Full constructor with all fields
     *
     * @param name Customer's name
     * @param email Customer's email
     * @param phoneNumber Customer's phone number
     */
    public Customer(String name, String email, String phoneNumber) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    /**
     * Constructor with ID parameter (for testing purposes)
     *
     * @param id Customer's ID
     * @param name Customer's name
     * @param email Customer's email
     * @param phoneNumber Customer's phone number
     */
    public Customer(Long id, String name, String email, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    /**
     * Add a trip to this customer's list of trips
     *
     * @param trip The trip to add
     */
    public void addTrip(Trip trip) {
        trips.add(trip);
        trip.setCustomer(this);
    }

    /**
     * Remove a trip from this customer's list of trips
     *
     * @param trip The trip to remove
     */
    public void removeTrip(Trip trip) {
        trips.remove(trip);
        trip.setCustomer(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                '}';
    }
}