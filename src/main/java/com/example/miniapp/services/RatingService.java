package com.example.miniapp.services;

import com.example.miniapp.models.Captain;
import com.example.miniapp.models.Rating;
import com.example.miniapp.repositories.CaptainRepository;
import com.example.miniapp.repositories.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    private final CaptainRepository captainRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository, CaptainRepository captainRepository) {
        this.ratingRepository = ratingRepository;
        this.captainRepository = captainRepository;
    }


    public Rating addRating(Rating rating) {
        if (rating == null) {
            return null;
        }


        if (rating.getEntityId() == null || rating.getEntityType() == null || rating.getEntityType().trim().isEmpty() ||
                rating.getScore() == null || rating.getScore() < 1 || rating.getScore() > 5) {
            return null;
        }


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


    public Rating updateRating(String id, Rating updatedRating) {
        if (updatedRating == null) {
            return null;
        }


        Rating existingRating = ratingRepository.findById(id).orElse(null);
        if (existingRating == null) {
            return null; // Rating not found
        }


        if (updatedRating.getScore() != null && updatedRating.getScore() >= 1 && updatedRating.getScore() <= 5) {
            existingRating.setScore(updatedRating.getScore());
        }
        if (updatedRating.getComment() != null) {
            existingRating.setComment(updatedRating.getComment());
        }


        Rating savedRating = ratingRepository.save(existingRating);


        if ("captain".equalsIgnoreCase(existingRating.getEntityType())) {
            updateCaptainAverageRating(existingRating.getEntityId());
        }

        return savedRating;
    }


    public void deleteRating(String id) {
        Rating rating = ratingRepository.findById(id).orElse(null);

        if (rating == null) {
            return; // Rating not found, nothing to delete
        }


        Long entityId = rating.getEntityId();
        String entityType = rating.getEntityType();


        ratingRepository.deleteById(id);

        // Update captain's average rating if this was a captain rating
        if ("captain".equalsIgnoreCase(entityType)) {
            updateCaptainAverageRating(entityId);
        }
    }


    public List<Rating> getRatingsByEntity(Long entityId, String entityType) {
        return ratingRepository.findByEntityIdAndEntityType(entityId, entityType);
    }


    public List<Rating> findRatingsAboveScore(int minScore) {
        return ratingRepository.findByScoreGreaterThanEqual(minScore);
    }

    public Rating getRatingById(String id) {
        return ratingRepository.findById(id).orElse(null);
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }


    public List<Rating> findRatingsByEntityType(String entityType) {
        return ratingRepository.findByEntityType(entityType);
    }

    private void updateCaptainAverageRating(Long captainId) {
        List<Rating> captainRatings = ratingRepository.findByEntityIdAndEntityType(captainId, "captain");

        if (captainRatings.isEmpty()) {
            return;
        }


        double averageRating = captainRatings.stream()
                .mapToInt(Rating::getScore)
                .average()
                .orElse(0.0);


        captainRepository.findById(captainId).ifPresent(captain -> {
            captain.setAvgRatingScore(averageRating);
            captainRepository.save(captain);
        });
    }





}
