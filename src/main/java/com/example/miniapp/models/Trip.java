package com.example.miniapp.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;


@Entity
@Table(name = "trips")
public class Trip {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "trip_date", nullable = false)
    private LocalDateTime tripDate;

    @Column(nullable = false)
    private String origin;

    @Column(nullable = false)
    private String destination;

    @Column(name = "trip_cost", nullable = false)
    private Double tripCost;

    @ManyToOne
    @JoinColumn(name = "captain_id", nullable = true)
    private Captain captain;

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private Customer customer;

    @OneToOne(mappedBy = "trip", cascade = CascadeType.ALL)
    private Payment payment;


    public Trip() {
        // Required by JPA
    }


    public Trip(LocalDateTime tripDate, String origin, String destination) {
        this.tripDate = tripDate;
        this.origin = origin;
        this.destination = destination;
    }


    public Trip(LocalDateTime tripDate, String origin, String destination, Double tripCost) {
        this.tripDate = tripDate;
        this.origin = origin;
        this.destination = destination;
        this.tripCost = tripCost;
    }


    public Trip(LocalDateTime tripDate, String origin, String destination, Double tripCost,
                Captain captain, Customer customer) {
        this.tripDate = tripDate;
        this.origin = origin;
        this.destination = destination;
        this.tripCost = tripCost;
        this.captain = captain;
        this.customer = customer;
    }





    public Trip(Long id, LocalDateTime tripDate, String origin, String destination, Double tripCost) {
        this.id = id;
        this.tripDate = tripDate;
        this.origin = origin;
        this.destination = destination;
        this.tripCost = tripCost;
    }



    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTripDate() {
        return tripDate;
    }

    public void setTripDate(LocalDateTime tripDate) {
        this.tripDate = tripDate;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public Double getTripCost() {
        return tripCost;
    }

    public void setTripCost(Double tripCost) {
        this.tripCost = tripCost;
    }

    public Captain getCaptain() {
        return captain;
    }

    public void setCaptain(Captain captain) {
        this.captain = captain;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return Objects.equals(id, trip.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", tripDate=" + tripDate +
                ", origin='" + origin + '\'' +
                ", destination='" + destination + '\'' +
                ", tripCost=" + tripCost +
                '}';
    }
}