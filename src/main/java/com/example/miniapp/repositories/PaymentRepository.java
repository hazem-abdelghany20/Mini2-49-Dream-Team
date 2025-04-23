package com.example.miniapp.repositories;

import com.example.miniapp.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity operations
 */
@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    /**
     * Find payments by trip ID
     * @param tripId ID of the trip
     * @return List of payments associated with the trip
     */
    List<Payment> findByTripId(Long tripId);

    /**
     * Find payments with amount greater than a specified threshold
     * @param threshold Minimum amount threshold
     * @return List of payments with amount greater than threshold
     */
    List<Payment> findByAmountGreaterThan(Double threshold);

    /**
     * Find payment by trip ID (for one-to-one relationship validation)
     * @param tripId ID of the trip
     * @return Optional containing the payment if found
     */
    Optional<Payment> findOneByTripId(Long tripId);
}