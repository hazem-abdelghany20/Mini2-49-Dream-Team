package com.example.miniapp.repositories;

import com.example.miniapp.models.Trip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Trip entity operations
 */
@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {

    /**
     * Find all trips for a specific captain
     * @param captainId ID of the captain
     * @return List of trips associated with the captain
     */
    List<Trip> findByCaptainId(Long captainId);

    /**
     * Find all trips within a specified date range
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of trips within the date range
     */
    List<Trip> findByTripDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find all trips for a specific customer
     * @param customerId ID of the customer
     * @return List of trips associated with the customer
     */
    List<Trip> findByCustomerId(Long customerId);

    /**
     * Find all trips with a specific status
     * @param status Status of the trip
     * @return List of trips with the specified status
     */

}