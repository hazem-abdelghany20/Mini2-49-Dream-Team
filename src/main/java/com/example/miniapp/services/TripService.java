package com.example.miniapp.services;

import com.example.miniapp.models.Trip;
import com.example.miniapp.models.Captain;
import com.example.miniapp.models.Customer;
import com.example.miniapp.repositories.TripRepository;
import com.example.miniapp.repositories.CaptainRepository;
import com.example.miniapp.repositories.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TripService {

    private final TripRepository tripRepository;
    private final CaptainRepository captainRepository;
    private final CustomerRepository customerRepository;

    @Autowired
    public TripService(TripRepository tripRepository,
                       CaptainRepository captainRepository,
                       CustomerRepository customerRepository) {
        this.tripRepository = tripRepository;
        this.captainRepository = captainRepository;
        this.customerRepository = customerRepository;
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Optional<Trip> getTripById(Long id) {
        return tripRepository.findById(id);
    }

    public Trip createTrip(Trip trip, Long customerId, Long captainId) {
        // Fetch the customer and captain entities
        Customer customer = customerRepository.findById(customerId)
            .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + customerId));
        Captain captain = captainRepository.findById(captainId)
            .orElseThrow(() -> new EntityNotFoundException("Captain not found with id: " + captainId));

        // Associate them with the trip
        trip.setCustomer(customer);
        trip.setCaptain(captain);

        // Set initial status and time if not provided
        if (trip.getStartTime() == null) {
            trip.setStartTime(LocalDateTime.now());
        }
        if (trip.getStatus() == null) {
            trip.setStatus(Trip.TripStatus.REQUESTED);
        }

        return tripRepository.save(trip);
    }

    public Optional<Trip> updateTrip(Long id, Trip tripDetails) {
         return tripRepository.findById(id)
            .map(existingTrip -> {
                existingTrip.setStartLocation(tripDetails.getStartLocation());
                existingTrip.setEndLocation(tripDetails.getEndLocation());
                existingTrip.setStartTime(tripDetails.getStartTime());
                existingTrip.setEndTime(tripDetails.getEndTime());
                existingTrip.setStatus(tripDetails.getStatus());
                // Note: Updating captain/customer might require specific logic or separate endpoints
                return tripRepository.save(existingTrip);
            });
    }

    public boolean deleteTrip(Long id) {
        return tripRepository.findById(id)
            .map(trip -> {
                tripRepository.delete(trip);
                return true;
            }).orElse(false);
    }

    // Custom query methods
    public List<Trip> getTripsByCaptain(Long captainId) {
        return tripRepository.findByCaptainId(captainId);
    }

    public List<Trip> getTripsByCustomer(Long customerId) {
        return tripRepository.findByCustomerId(customerId);
    }

    public List<Trip> getTripsByStatus(Trip.TripStatus status) {
        return tripRepository.findByStatus(status);
    }

    // Method to update trip status (example business logic)
    public Optional<Trip> updateTripStatus(Long id, Trip.TripStatus newStatus) {
        return tripRepository.findById(id)
            .map(trip -> {
                trip.setStatus(newStatus);
                if (newStatus == Trip.TripStatus.COMPLETED || newStatus == Trip.TripStatus.CANCELLED) {
                    trip.setEndTime(LocalDateTime.now());
                }
                return tripRepository.save(trip);
            });
    }
}
