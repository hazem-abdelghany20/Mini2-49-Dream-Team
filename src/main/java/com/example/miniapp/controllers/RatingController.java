package com.example.miniapp.controllers;

import com.example.miniapp.models.Rating;
import com.example.miniapp.services.RatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Controller class for handling Rating-related HTTP requests
 */
@RestController
@RequestMapping("/rating")
public class RatingController {

    private final RatingService ratingService;

    @Autowired
    public RatingController(RatingService ratingService) {
        this.ratingService = ratingService;
    }

    /**
     * Add a new rating
     * @param rating Rating object to be added
     * @return The saved rating with generated ID
     */
    @PostMapping("/addRating")
    public ResponseEntity<Rating> addRating(@RequestBody Rating rating) {
        try {
            Rating savedRating = ratingService.addRating(rating);
            return new ResponseEntity<>(savedRating, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Update a rating
     * @param id ID of the rating to update
     * @param updatedRating Updated rating details
     * @return The updated rating
     */
    @PutMapping("/update/{id}")
    public ResponseEntity<Rating> updateRating(@PathVariable String id, @RequestBody Rating updatedRating) {
        try {
            Rating rating = ratingService.updateRating(id, updatedRating);
            return new ResponseEntity<>(rating, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete a rating
     * @param id ID of the rating to delete
     * @return Status message
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRating(@PathVariable String id) {
        try {
            ratingService.deleteRating(id);
            return new ResponseEntity<>("Rating with ID: " + id + " has been deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>("Rating with ID: " + id + " not found", HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Find ratings for a specific entity
     * @param entityId ID of the entity (captain, customer, or trip)
     * @param entityType Type of the entity (captain, customer, or trip)
     * @return List of ratings for the entity
     */
    @GetMapping("/findByEntity")
    public ResponseEntity<List<Rating>> findRatingsByEntity(
            @RequestParam Long entityId,
            @RequestParam String entityType) {
        try {
            List<Rating> ratings = ratingService.getRatingsByEntity(entityId, entityType);
            return new ResponseEntity<>(ratings, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Find ratings with score above a specific value
     * @param minScore Minimum score threshold
     * @return List of ratings with scores above the threshold
     */
    @GetMapping("/findAboveScore")
    public ResponseEntity<List<Rating>> findRatingsAboveScore(@RequestParam int minScore) {
        try {
            List<Rating> ratings = ratingService.findRatingsAboveScore(minScore);
            return new ResponseEntity<>(ratings, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all ratings
     * @return List of all ratings
     */
    @GetMapping("/allRatings")
    public ResponseEntity<List<Rating>> getAllRatings() {
        List<Rating> ratings = ratingService.getAllRatings();
        return new ResponseEntity<>(ratings, HttpStatus.OK);
    }

    /**
     * Get rating by ID
     * @param id ID of the rating to retrieve
     * @return Rating with the specified ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Rating> getRatingById(@PathVariable String id) {
        try {
            Rating rating = ratingService.getRatingById(id);
            return new ResponseEntity<>(rating, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Find ratings by entity type
     * @param entityType Type of the entity (captain, customer, or trip)
     * @return List of ratings for the entity type
     */
    @GetMapping("/findByEntityType")
    public ResponseEntity<List<Rating>> findRatingsByEntityType(@RequestParam String entityType) {
        try {
            List<Rating> ratings = ratingService.findRatingsByEntityType(entityType);
            return new ResponseEntity<>(ratings, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}