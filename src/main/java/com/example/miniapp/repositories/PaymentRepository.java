package com.example.miniapp.repositories;

import com.example.miniapp.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    // Find a payment by its associated trip ID
    Optional<Payment> findByTripId(Long tripId);

    // Find payments by status
    List<Payment> findByStatus(Payment.PaymentStatus status);

    // Find payments by payment method
    List<Payment> findByPaymentMethod(Payment.PaymentMethod paymentMethod);
}
