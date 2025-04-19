package com.example.miniapp.repositories;

import com.example.miniapp.models.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    // Find all trips for a specific captain
    List<Trip> findByCaptainId(Long captainId);

    // Find all trips for a specific customer
    List<Trip> findByCustomerId(Long customerId);

    // Find all trips with a specific status
    List<Trip> findByStatus(Trip.TripStatus status);

    // Find trips for a customer with a specific status
    List<Trip> findByCustomerIdAndStatus(Long customerId, Trip.TripStatus status);

    // Find trips for a captain with a specific status
    List<Trip> findByCaptainIdAndStatus(Long captainId, Trip.TripStatus status);

}
