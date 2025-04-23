package com.example.miniapp.services;

import com.example.miniapp.models.Trip;
import com.example.miniapp.repositories.TripRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service class for handling Trip-related business logic
 */
@Service
public class TripService {

    private final TripRepository tripRepository;

    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    /**
     * Add a new trip
     * @param trip Trip object to be added
     * @return Saved trip with generated ID
     */
    @Transactional
    public Trip addTrip(Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }

        // Validate required fields
        if (trip.getTripDate() == null) {
            throw new IllegalArgumentException("Trip date is required");
        }
        if (trip.getOrigin() == null || trip.getOrigin().trim().isEmpty()) {
            throw new IllegalArgumentException("Origin location is required");
        }
        if (trip.getDestination() == null || trip.getDestination().trim().isEmpty()) {
            throw new IllegalArgumentException("Destination location is required");
        }
        if (trip.getTripCost() == null || trip.getTripCost() < 0) {
            throw new IllegalArgumentException("Valid trip cost is required");
        }

        return tripRepository.save(trip);
    }

    /**
     * Get all trips
     * @return List of all trips
     */
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    /**
     * Get trip by ID
     * @param id ID of the trip to retrieve
     * @return Trip with the specified ID
     * @throws EntityNotFoundException if trip not found
     */
    public Trip getTripById(Long id) {
        return tripRepository.findById(id)
                .orElse(null);
    }

    /**
     * Update trip details
     * @param id ID of the trip to update
     * @param trip Updated trip details
     * @return Updated trip
     * @throws EntityNotFoundException if trip not found
     */
    @Transactional
    public Trip updateTrip(Long id, Trip trip) {
        if (trip == null) {
            throw new IllegalArgumentException("Trip cannot be null");
        }

        // Check if trip exists
        Trip existingTrip = tripRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Trip not found with ID: " + id));

        // Update fields if provided
        if (trip.getTripDate() != null) {
            existingTrip.setTripDate(trip.getTripDate());
        }
        if (trip.getOrigin() != null && !trip.getOrigin().trim().isEmpty()) {
            existingTrip.setOrigin(trip.getOrigin());
        }
        if (trip.getDestination() != null && !trip.getDestination().trim().isEmpty()) {
            existingTrip.setDestination(trip.getDestination());
        }
        if (trip.getTripCost() != null && trip.getTripCost() >= 0) {
            existingTrip.setTripCost(trip.getTripCost());
        }
        if (trip.getCaptain() != null) {
            existingTrip.setCaptain(trip.getCaptain());
        }
        if (trip.getCustomer() != null) {
            existingTrip.setCustomer(trip.getCustomer());
        }

        return tripRepository.save(existingTrip);
    }

    /**
     * Delete a trip
     * @param id ID of the trip to delete
     * @throws EntityNotFoundException if trip not found
     */
    @Transactional
    public void deleteTrip(Long id) {
        if (tripRepository.existsById(id)) {
           return;
        }
        tripRepository.deleteById(id);
    }

    /**
     * Find trips within a date range
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of trips within the date range
     */
    public List<Trip> findTripsWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Start date and end date cannot be null");
        }

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("Start date cannot be after end date");
        }

        return tripRepository.findByTripDateBetween(startDate, endDate);
    }

    /**
     * Find trips by captain ID
     * @param captainId ID of the captain
     * @return List of trips associated with the captain
     */
    public List<Trip> findTripsByCaptainId(Long captainId) {
        if (captainId == null) {
            throw new IllegalArgumentException("Captain ID cannot be null");
        }

        return tripRepository.findByCaptainId(captainId);
    }
}