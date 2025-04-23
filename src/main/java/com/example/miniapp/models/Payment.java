package com.example.miniapp.models;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Entity representing a payment record for a trip in the ride-sharing application.
 * This class stores payment-related information like amount, method, and status.
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double amount;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod;

    @Column(name = "payment_status", nullable = false)
    private Boolean paymentStatus;

    // One-to-One relationship with Trip
    @OneToOne
    @JoinColumn(name = "trip_id", unique = true)
    private Trip trip;

    /**
     * Default constructor
     */
    public Payment() {
        // Required by JPA
    }

    /**
     * Partial constructor
     * @param amount Payment amount
     * @param paymentMethod Method of payment (e.g., card, cash)
     */
    public Payment(Double amount, String paymentMethod) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = false; // Default to unpaid
    }

    /**
     * Constructor for testing
     * @param amount Payment amount
     * @param paymentMethod Method of payment (e.g., card, cash)
     * @param paymentStatus Payment status (true if paid)
     */
    public Payment(Double amount, String paymentMethod, Boolean paymentStatus) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    /**
     * Full constructor
     * @param amount Payment amount
     * @param paymentMethod Method of payment (e.g., card, cash)
     * @param paymentStatus Payment status (true if paid)
     * @param trip Associated trip
     */
    public Payment(Double amount, String paymentMethod, Boolean paymentStatus, Trip trip) {
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.trip = trip;
    }

    /**
     * Constructor with ID for testing
     * @param id Payment ID
     * @param amount Payment amount
     * @param paymentMethod Method of payment (e.g., card, cash)
     * @param paymentStatus Payment status (true if paid)
     */
    public Payment(Long id, Double amount, String paymentMethod, Boolean paymentStatus) {
        this.id = id;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Boolean getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Boolean paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Trip getTrip() {
        return trip;
    }

    public void setTrip(Trip trip) {
        this.trip = trip;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", amount=" + amount +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", paymentStatus=" + paymentStatus +
                '}';
    }
}