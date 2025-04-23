package com.example.miniapp.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Entity representing a captain (driver) in the ride-sharing application.
 * This class stores captain information including their rating and associated trips.
 */
@Entity
@Table(name = "captains")
public class Captain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "license_number", nullable = false, unique = true)
    private String licenseNumber;

    @Column(name = "avg_rating_score")
    private Double avgRatingScore;

    @OneToMany(mappedBy = "captain", cascade = CascadeType.ALL)
    private List<Trip> trips = new ArrayList<>();

    /**
     * Default constructor required by JPA
     */
    public Captain() {
        // Required by JPA
    }

    /**
     * Partial constructor with essential fields
     *
     * @param name Captain's name
     * @param licenseNumber Captain's license number
     */
    public Captain(String name, String licenseNumber) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.avgRatingScore = 0.0; // Default rating for new captains
    }

    /**
     * Full constructor with all fields
     *
     * @param name Captain's name
     * @param licenseNumber Captain's license number
     * @param avgRatingScore Captain's average rating score
     */
    public Captain(String name, String licenseNumber, Double avgRatingScore) {
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.avgRatingScore = avgRatingScore;
    }

    /**
     * Constructor with ID parameter (for testing purposes)
     *
     * @param id Captain's ID
     * @param name Captain's name
     * @param licenseNumber Captain's license number
     * @param avgRatingScore Captain's average rating score
     */
    public Captain(Long id, String name, String licenseNumber, Double avgRatingScore) {
        this.id = id;
        this.name = name;
        this.licenseNumber = licenseNumber;
        this.avgRatingScore = avgRatingScore;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public Double getAvgRatingScore() {
        return avgRatingScore;
    }

    public void setAvgRatingScore(Double avgRatingScore) {
        this.avgRatingScore = avgRatingScore;
    }

    public List<Trip> getTrips() {
        return trips;
    }

    public void setTrips(List<Trip> trips) {
        this.trips = trips;
    }

    /**
     * Add a trip to this captain's list of trips
     *
     * @param trip The trip to add
     */
    public void addTrip(Trip trip) {
        trips.add(trip);
        trip.setCaptain(this);
    }

    /**
     * Remove a trip from this captain's list of trips
     *
     * @param trip The trip to remove
     */
    public void removeTrip(Trip trip) {
        trips.remove(trip);
        trip.setCaptain(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Captain captain = (Captain) o;
        return Objects.equals(id, captain.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Captain{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", licenseNumber='" + licenseNumber + '\'' +
                ", avgRatingScore=" + avgRatingScore +
                '}';
    }
}