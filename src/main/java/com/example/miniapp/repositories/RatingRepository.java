package com.example.miniapp.repositories;

import com.example.miniapp.models.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository interface for Rating document operations in MongoDB
 */
@Repository
public interface RatingRepository extends MongoRepository<Rating, String> {

    /**
     * Find ratings for a specific entity by ID and type
     * @param entityId ID of the entity (captain, customer, or trip)
     * @param entityType Type of the entity (captain, customer, or trip)
     * @return List of ratings for the entity
     */
    List<Rating> findByEntityIdAndEntityType(Long entityId, String entityType);

    /**
     * Find ratings with a score greater than or equal to a minimum value
     * @param minScore Minimum score threshold
     * @return List of ratings with scores greater than or equal to minScore
     */
    List<Rating> findByScoreGreaterThanEqual(Integer minScore);

    /**
     * Find ratings for a specific entity type
     * @param entityType Type of the entity (captain, customer, or trip)
     * @return List of ratings for the entity type
     */
    List<Rating> findByEntityType(String entityType);

    /**
     * Find ratings with scores in a specific range
     * @param minScore Minimum score
     * @param maxScore Maximum score
     * @return List of ratings with scores in the specified range
     */
    List<Rating> findByScoreBetween(Integer minScore, Integer maxScore);
}