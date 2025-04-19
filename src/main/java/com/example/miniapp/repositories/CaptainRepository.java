package com.example.miniapp.repositories;

import com.example.miniapp.models.Captain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CaptainRepository extends JpaRepository<Captain, Long> {

    // Example custom query method: Find a captain by email
    Optional<Captain> findByEmail(String email);

    // Example custom query method: Find a captain by phone number
    Optional<Captain> findByPhoneNumber(String phoneNumber);

    // You can add more custom query methods here as needed
    // For example, find captains by vehicle details, etc.
    // List<Captain> findByVehicleDetailsContaining(String keyword);
}
