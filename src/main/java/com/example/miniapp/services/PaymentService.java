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
            // Set the trip and save the payment
            payment.setTrip(trip);
        }

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
        if(payment.getAmount() != null)
            existingPayment.setAmount(payment.getAmount());
        if(payment.getPaymentMethod() != null)
            existingPayment.setPaymentMethod(payment.getPaymentMethod());
        if(payment.getPaymentStatus() != null)
            existingPayment.setPaymentStatus(payment.getPaymentStatus());




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
        return paymentRepository.findByTripId(tripId);
    }

    /**
     * Find payments with amount greater than threshold
     * @param threshold Minimum amount threshold
     * @return List of payments with amount greater than threshold
     */
    public List<Payment> findByAmountThreshold(Double threshold) {
        return paymentRepository.findByAmountGreaterThan(threshold);
    }
}