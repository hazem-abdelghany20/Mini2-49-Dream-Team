package com.example.miniapp.services;

import com.example.miniapp.models.Payment;
import com.example.miniapp.models.Trip;
import com.example.miniapp.repositories.PaymentRepository;
import com.example.miniapp.repositories.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final TripRepository tripRepository;

    @Autowired
    public PaymentService(PaymentRepository paymentRepository, TripRepository tripRepository) {
        this.paymentRepository = paymentRepository;
        this.tripRepository = tripRepository;
    }

    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }

    public Optional<Payment> getPaymentByTripId(Long tripId) {
        return paymentRepository.findByTripId(tripId);
    }

    public Payment createPayment(Payment payment, Long tripId) {
        // Find the associated trip
        Trip trip = tripRepository.findById(tripId)
            .orElseThrow(() -> new EntityNotFoundException("Trip not found with id: " + tripId));

        // Check if payment already exists for this trip
        if(paymentRepository.findByTripId(tripId).isPresent()){
            throw new IllegalStateException("Payment already exists for trip id: " + tripId);
        }

        // Associate payment with the trip
        payment.setTrip(trip);

        // Set default values if needed
        if (payment.getPaymentTime() == null) {
            payment.setPaymentTime(LocalDateTime.now());
        }
        if (payment.getStatus() == null) {
            payment.setStatus(Payment.PaymentStatus.PENDING);
        }
        // Automatically set amount based on trip details (example - needs logic)
        // payment.setAmount(calculateTripFare(trip));

        Payment savedPayment = paymentRepository.save(payment);

        // Update trip to link payment (if not already handled by cascade)
        // trip.setPayment(savedPayment);
        // tripRepository.save(trip); // May not be needed depending on cascade/mapping

        return savedPayment;
    }

    public Optional<Payment> updatePaymentStatus(Long paymentId, Payment.PaymentStatus newStatus) {
        return paymentRepository.findById(paymentId)
            .map(payment -> {
                payment.setStatus(newStatus);
                // Add any other logic needed when status changes
                return paymentRepository.save(payment);
            });
    }

    // Update method (example - could be more specific)
    public Optional<Payment> updatePayment(Long id, Payment paymentDetails) {
        return paymentRepository.findById(id)
            .map(existingPayment -> {
                existingPayment.setAmount(paymentDetails.getAmount());
                existingPayment.setStatus(paymentDetails.getStatus());
                existingPayment.setPaymentMethod(paymentDetails.getPaymentMethod());
                existingPayment.setPaymentTime(paymentDetails.getPaymentTime());
                // Updating the trip association might be complex/restricted
                return paymentRepository.save(existingPayment);
            });
    }

    public boolean deletePayment(Long id) {
        return paymentRepository.findById(id)
            .map(payment -> {
                // Maybe add logic here - e.g., cannot delete completed payment?
                paymentRepository.delete(payment);
                return true;
            }).orElse(false);
    }

    // Find payments by status
    public List<Payment> getPaymentsByStatus(Payment.PaymentStatus status) {
        return paymentRepository.findByStatus(status);
    }

     // Find payments by payment method
    public List<Payment> getPaymentsByMethod(Payment.PaymentMethod method) {
        return paymentRepository.findByPaymentMethod(method);
    }

    // Example helper method placeholder
    // private BigDecimal calculateTripFare(Trip trip) {
    //     // Implement logic to calculate fare based on distance, time, etc.
    //     return new BigDecimal("10.00"); // Placeholder
    // }
}
