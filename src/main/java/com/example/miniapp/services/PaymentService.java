package com.example.miniapp.services;

import com.example.miniapp.models.Payment;
import com.example.miniapp.models.Trip;
import com.example.miniapp.repositories.PaymentRepository;
import com.example.miniapp.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service class for handling Payment-related business logic
 */
@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TripRepository tripRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, TripRepository tripRepository) {
        this.paymentRepository = paymentRepository;
        this.tripRepository = tripRepository;
    }

    /**
     * Add a new payment
     * @param payment Payment object to be added
     * @return Saved payment with generated ID, or null if there is an issue
     */
    @Transactional
    public Payment addPayment(Payment payment) {
        if (payment == null) {
            return null;
        }

        // Check if trip exists
        if (payment.getTrip() != null) {
            Trip trip = tripRepository.findById(payment.getTrip().getId()).orElse(null);

            // Check if payment already exists for this trip
            if (trip != null && paymentRepository.findOneByTripId(trip.getId()).isPresent()) {
                return null; // Payment already exists for this trip
            }

            // Set the trip and save the payment
            payment.setTrip(trip);
        }

        return paymentRepository.save(payment);
    }

    /**
     * Add a payment for a specific trip
     * @param payment Payment object to be added
     * @param tripId ID of the trip to associate with the payment
     * @return Saved payment with generated ID, or null if there is an issue
     */
    @Transactional
    public Payment addPayment(Payment payment, Long tripId) {
        if (payment == null) {
            return null;
        }

        // Find the associated trip
        Trip trip = tripRepository.findById(tripId).orElse(null);

        // Check if trip exists
        if (trip == null) {
            return null; // Trip not found
        }

        // Check if payment already exists for this trip
        if (paymentRepository.findOneByTripId(tripId).isPresent()) {
            return null; // Payment already exists for this trip
        }

        // Associate payment with the trip
        payment.setTrip(trip);

        return paymentRepository.save(payment);
    }

    /**
     * Get all payments
     * @return List of all payments
     */
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    /**
     * Get payment by ID
     * @param id ID of the payment to retrieve
     * @return Payment with the specified ID, or null if not found
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    /**
     * Update payment details
     * @param id ID of the payment to update
     * @param payment Updated payment details
     * @return Updated payment, or null if not found
     */
    @Transactional
    public Payment updatePayment(Long id, Payment payment) {
        if (payment == null) {
            return null;
        }

        // Check if payment exists
        Payment existingPayment = paymentRepository.findById(id).orElse(null);

        // If payment does not exist, return null
        if (existingPayment == null) {
            return null;
        }

        // Update fields
        existingPayment.setAmount(payment.getAmount());
        existingPayment.setPaymentMethod(payment.getPaymentMethod());
        existingPayment.setPaymentStatus(payment.getPaymentStatus());

        // Don't allow changing the trip association
        // If a new trip is provided in the update, verify it exists but don't change the association
        if (payment.getTrip() != null && payment.getTrip().getId() != null &&
                !payment.getTrip().getId().equals(existingPayment.getTrip().getId())) {
            return null; // Cannot change the trip association for an existing payment
        }

        return paymentRepository.save(existingPayment);
    }

    /**
     * Delete a payment
     * @param id ID of the payment to delete
     */
    @Transactional
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }

    /**
     * Find payments by trip ID
     * @param tripId ID of the trip
     * @return List of payments associated with the trip
     */
    public List<Payment> findPaymentsByTripId(Long tripId) {
        // Verify that the trip exists
        if (!tripRepository.existsById(tripId)) {
            return List.of(); // Return empty list if trip not found
        }

        return paymentRepository.findByTripId(tripId);
    }

    /**
     * Find payments with amount greater than threshold
     * @param threshold Minimum amount threshold
     * @return List of payments with amount greater than threshold
     */
    public List<Payment> findByAmountThreshold(Double threshold) {
        if (threshold == null || threshold < 0) {
            return List.of(); // Return empty list if invalid threshold
        }

        return paymentRepository.findByAmountGreaterThan(threshold);
    }
}