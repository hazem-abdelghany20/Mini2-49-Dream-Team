package com.example.miniapp.services;

import com.example.miniapp.models.Payment;
import com.example.miniapp.models.Trip;
import com.example.miniapp.repositories.PaymentRepository;
import com.example.miniapp.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

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
     * @return Saved payment with generated ID
     */
    @Transactional
    public Payment addPayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        // Check if trip exists
        if (payment.getTrip() != null) {
            Trip trip = tripRepository.findById(payment.getTrip().getId())
                    .orElse(null);

            // Check if payment already exists for this trip
            if (trip != null) {
                if (paymentRepository.findOneByTripId(trip.getId()).isPresent()) {
                    throw new IllegalStateException("Payment already exists for trip with ID: " + trip.getId());

                }
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
     * @return Saved payment with generated ID
     */
    @Transactional
    public Payment addPayment(Payment payment, Long tripId) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        // Find the associated trip
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found with ID: " + tripId));

        // Check if payment already exists for this trip
        if (paymentRepository.findOneByTripId(tripId).isPresent()) {
            throw new IllegalStateException("Payment already exists for trip with ID: " + tripId);
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
     * @return Payment with the specified ID
     * @throws EntityNotFoundException if payment not found
     */
    public Payment getPaymentById(Long id) {
        return paymentRepository.findById(id)
                .orElse(null);
    }

    /**
     * Update payment details
     * @param id ID of the payment to update
     * @param payment Updated payment details
     * @return Updated payment
     * @throws EntityNotFoundException if payment not found
     */
    @Transactional
    public Payment updatePayment(Long id, Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("Payment cannot be null");
        }

        // Check if payment exists
        Payment existingPayment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found with ID: " + id));

        // Update fields
        existingPayment.setAmount(payment.getAmount());
        existingPayment.setPaymentMethod(payment.getPaymentMethod());
        existingPayment.setPaymentStatus(payment.getPaymentStatus());

        // Don't allow changing the trip association
        // If a new trip is provided in the update, verify it exists but don't change the association
        if (payment.getTrip() != null && payment.getTrip().getId() != null &&
                !payment.getTrip().getId().equals(existingPayment.getTrip().getId())) {
            throw new IllegalArgumentException("Cannot change the trip association for an existing payment");
        }

        return paymentRepository.save(existingPayment);
    }

    /**
     * Delete a payment
     * @param id ID of the payment to delete
     * @throws EntityNotFoundException if payment not found
     */
    @Transactional
    public void deletePayment(Long id) {
//        if (!paymentRepository.existsById(id)) {
//            throw new EntityNotFoundException("Payment not found with ID: " + id);
//        }
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
            throw new EntityNotFoundException("Trip not found with ID: " + tripId);
        }

        return paymentRepository.findByTripId(tripId);
    }

    /**
     * Find payments with amount greater than threshold
     * @param threshold Minimum amount threshold
     * @return List of payments with amount greater than threshold
     */
    public List<Payment> findByAmountThreshold(Double threshold) {
        if (threshold == null) {
            throw new IllegalArgumentException("Threshold amount cannot be null");
        }

        if (threshold < 0) {
            throw new IllegalArgumentException("Threshold amount cannot be negative");
        }

        return paymentRepository.findByAmountGreaterThan(threshold);
    }
}