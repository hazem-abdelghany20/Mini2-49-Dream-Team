package com.example.miniapp.controllers;

import com.example.miniapp.models.Trip;
import com.example.miniapp.services.TripService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;


@RestController
@RequestMapping("/trip")
public class TripController {

    private final TripService tripService;

    @Autowired
    public TripController(TripService tripService) {
        this.tripService = tripService;
    }


    @PostMapping("/addTrip")
    public ResponseEntity<Trip> addTrip(@RequestBody Trip trip) {
        try {
            Trip savedTrip = tripService.addTrip(trip);
            return new ResponseEntity<>(savedTrip, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/allTrips")
    public ResponseEntity<List<Trip>> getAllTrips() {
        List<Trip> trips = tripService.getAllTrips();
        return new ResponseEntity<>(trips, HttpStatus.OK);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripById(@PathVariable Long id) {
        try {
            Trip trip = tripService.getTripById(id);
            return new ResponseEntity<>(trip, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable Long id, @RequestBody Trip trip) {
        try {
            Trip updatedTrip = tripService.updateTrip(id, trip);
            return new ResponseEntity<>(updatedTrip, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteTrip(@PathVariable Long id) {
        try {
            tripService.deleteTrip(id);
            return new ResponseEntity<>("Trip with ID: " + id + " has been deleted successfully", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>("Trip with ID: " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/findByDateRange")
    public ResponseEntity<List<Trip>> findTripsWithinDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            List<Trip> trips = tripService.findTripsWithinDateRange(startDate, endDate);
            return new ResponseEntity<>(trips, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/findByCaptainId")
    public ResponseEntity<List<Trip>> findTripsByCaptainId(@RequestParam Long captainId) {
        try {
            List<Trip> trips = tripService.findTripsByCaptainId(captainId);
            return new ResponseEntity<>(trips, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}