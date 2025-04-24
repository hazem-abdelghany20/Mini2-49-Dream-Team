package com.example.miniapp.repositories;

import com.example.miniapp.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;


@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {


    List<Payment> findByTripId(Long tripId);


    List<Payment> findByAmountGreaterThan(Double threshold);


    Optional<Payment> findOneByTripId(Long tripId);
}