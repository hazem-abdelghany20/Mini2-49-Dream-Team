package com.example.miniapp.services;

import com.example.miniapp.models.Trip;
import com.example.miniapp.repositories.TripRepository;
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
     * @return Saved trip with generated ID, or null if invalid input
     */
    @Transactional
    public Trip addTrip(Trip trip) {
        if (trip == null) {
            return null;
        }

        // Validate required fields
        if (trip.getTripDate() == null || trip.getOrigin() == null || trip.getOrigin().trim().isEmpty() ||
                trip.getDestination() == null || trip.getDestination().trim().isEmpty() || trip.getTripCost() == null || trip.getTripCost() < 0) {
            return null; // Return null if any required field is missing or invalid
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
     * @return Trip with the specified ID, or null if not found
     */
    public Trip getTripById(Long id) {
        return tripRepository.findById(id).orElse(null);
    }

    /**
     * Update trip details
     * @param id ID of the trip to update
     * @param trip Updated trip details
     * @return Updated trip, or null if trip not found
     */
    @Transactional
    public Trip updateTrip(Long id, Trip trip) {
        if (trip == null) {
            return null;
        }

        // Check if trip exists
        Trip existingTrip = tripRepository.findById(id).orElse(null);
        if (existingTrip == null) {
            return null; // Trip not found
        }

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
     */
    @Transactional
    public void deleteTrip(Long id) {
        if (tripRepository.existsById(id)) {
            tripRepository.deleteById(id);
        }
    }

    /**
     * Find trips within a date range
     * @param startDate Start of the date range
     * @param endDate End of the date range
     * @return List of trips within the date range, or empty list if invalid dates
     */
    public List<Trip> findTripsWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            return List.of(); // Return empty list if dates are invalid
        }

        return tripRepository.findByTripDateBetween(startDate, endDate);
    }

    /**
     * Find trips by captain ID
     * @param captainId ID of the captain
     * @return List of trips associated with the captain, or empty list if captainId is invalid
     */
    public List<Trip> findTripsByCaptainId(Long captainId) {
        if (captainId == null) {
            return List.of(); // Return empty list if captainId is invalid
        }

        return tripRepository.findByCaptainId(captainId);
    }
}
