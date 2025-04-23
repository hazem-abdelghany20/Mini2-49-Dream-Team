package com.example.miniapp.services;

import com.example.miniapp.models.Captain;
import com.example.miniapp.models.Rating;
import com.example.miniapp.repositories.CaptainRepository;
import com.example.miniapp.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service class for handling Rating-related business logic
 */
@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final CaptainRepository captainRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository, CaptainRepository captainRepository) {
        this.ratingRepository = ratingRepository;
        this.captainRepository = captainRepository;
    }

    /**
     * Add a new rating
     * @param rating Rating object to be added
     * @return Saved rating with generated ID
     */
    public Rating addRating(Rating rating) {
        if (rating == null) {
            throw new IllegalArgumentException("Rating cannot be null");
        }

        // Validate required fields
        if (rating.getEntityId() == null) {
            throw new IllegalArgumentException("Entity ID is required");
        }
        if (rating.getEntityType() == null || rating.getEntityType().trim().isEmpty()) {
            throw new IllegalArgumentException("Entity type is required");
        }
        if (rating.getScore() == null || rating.getScore() < 1 || rating.getScore() > 5) {
            throw new IllegalArgumentException("Valid score between 1 and 5 is required");
        }

        // Set rating date if not provided
        if (rating.getRatingDate() == null) {
            rating.setRatingDate(LocalDateTime.now());
        }

        // Save the rating
        Rating savedRating = ratingRepository.save(rating);

        // Update captain's average rating if this is a captain rating
        if ("captain".equalsIgnoreCase(rating.getEntityType())) {
            updateCaptainAverageRating(rating.getEntityId());
        }

        return savedRating;
    }

    /**
     * Update an existing rating
     * @param id ID of the rating to update
     * @param updatedRating Updated rating details
     * @return Updated rating
     */
    public Rating updateRating(String id, Rating updatedRating) {
        if (updatedRating == null) {
            throw new IllegalArgumentException("Rating cannot be null");
        }

        // Check if rating exists
        Rating existingRating = ratingRepository.findById(id)
                .orElse(null);
        if( existingRating == null) {
            return null;
        }

        // Update fields if provided
        if (updatedRating.getScore() != null && updatedRating.getScore() >= 1 && updatedRating.getScore() <= 5) {
            existingRating.setScore(updatedRating.getScore());
        }
        if (updatedRating.getComment() != null) {
            existingRating.setComment(updatedRating.getComment());
        }

        // Save updated rating
        Rating savedRating = ratingRepository.save(existingRating);

        // Update captain's average rating if this is a captain rating
        if ("captain".equalsIgnoreCase(existingRating.getEntityType())) {
            updateCaptainAverageRating(existingRating.getEntityId());
        }

        return savedRating;
    }

    /**
     * Delete a rating by ID
     * @param id ID of the rating to delete
     */
    public void deleteRating(String id) {
        Rating rating = ratingRepository.findById(id) .orElse(null);

        if(rating == null) {
            return;
        }


        // Store entity details before deletion for possible updates
        Long entityId = rating.getEntityId();
        String entityType = rating.getEntityType();

        // Delete the rating
        ratingRepository.deleteById(id);

        // Update captain's average rating if this was a captain rating
        if ("captain".equalsIgnoreCase(entityType)) {
            updateCaptainAverageRating(entityId);
        }
    }

    /**
     * Get ratings for a specific entity by ID and type
     * @param entityId ID of the entity
     * @param entityType Type of the entity
     * @return List of ratings for the entity
     */
    public List<Rating> getRatingsByEntity(Long entityId, String entityType) {
        if (entityId == null) {
            throw new IllegalArgumentException("Entity ID cannot be null");
        }
        if (entityType == null || entityType.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity type cannot be null or empty");
        }

        return ratingRepository.findByEntityIdAndEntityType(entityId, entityType);
    }

    /**
     * Find ratings with a score greater than or equal to a minimum value
     * @param minScore Minimum score threshold
     * @return List of ratings with scores above the threshold
     */
    public List<Rating> findRatingsAboveScore(int minScore) {
        if (minScore < 1 || minScore > 5) {
            throw new IllegalArgumentException("Score must be between 1 and 5");
        }

        return ratingRepository.findByScoreGreaterThanEqual(minScore);
    }

    /**
     * Get all ratings
     * @return List of all ratings
     */
    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    /**
     * Get rating by ID
     * @param id ID of the rating to retrieve
     * @return Rating with the specified ID
     */
    public Rating getRatingById(String id) {
        return ratingRepository.findById(id)
                .orElse(null);
    }

    /**
     * Find ratings by entity type
     * @param entityType Type of the entity
     * @return List of ratings for the entity type
     */
    public List<Rating> findRatingsByEntityType(String entityType) {
        if (entityType == null || entityType.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity type cannot be null or empty");
        }

        return ratingRepository.findByEntityType(entityType);
    }

    /**
     * Helper method to update a captain's average rating score
     * @param captainId ID of the captain
     */
    private void updateCaptainAverageRating(Long captainId) {
        List<Rating> captainRatings = ratingRepository.findByEntityIdAndEntityType(captainId, "captain");

        if (captainRatings.isEmpty()) {
            return;
        }

        // Calculate average rating
        double averageRating = captainRatings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);

        // Update captain's average rating in the database
        captainRepository.findById(captainId).ifPresent(captain -> {
            captain.setAvgRatingScore(averageRating);
            captainRepository.save(captain);
        });
    }
}