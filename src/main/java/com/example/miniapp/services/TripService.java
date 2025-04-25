package com.example.miniapp.services;

import com.example.miniapp.models.Trip;
import com.example.miniapp.repositories.TripRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
public class TripService {

    private final TripRepository tripRepository;

    @Autowired
    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }


    @Transactional
    public Trip addTrip(Trip trip) {
        if (trip == null) {
            return null;
        }



        return tripRepository.save(trip);
    }


    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }


    public Trip getTripById(Long id) {
        return tripRepository.findById(id).orElse(null);
    }


    @Transactional
    public Trip updateTrip(Long id, Trip trip) {
        if (trip == null) {
            return null;
        }


        Trip existingTrip = tripRepository.findById(id).orElse(null);
        if (existingTrip == null) {
            return null;
        }


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


    @Transactional
    public void deleteTrip(Long id) {
        if (tripRepository.existsById(id)) {
            tripRepository.deleteById(id);
        }
    }


    public List<Trip> findTripsWithinDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return tripRepository.findByTripDateBetween(startDate, endDate);
    }


    public List<Trip> findTripsByCaptainId(Long captainId) {
        return tripRepository.findByCaptainId(captainId);
    }
}
