package com.example.miniapp.repositories;

import com.example.miniapp.models.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingRepository extends MongoRepository<Rating, String> { // ID is String for MongoDB

    // Find all ratings for a specific trip
    List<Rating> findByTripId(Long tripId);

    // Find all ratings given by a specific rater (customer or captain)
    List<Rating> findByRaterId(Long raterId);

    // Find all ratings received by a specific entity (customer or captain)
    List<Rating> findByRatedId(Long ratedId);

    // Find ratings by rater type
    List<Rating> findByRaterType(Rating.RaterType raterType);

    // Find ratings given by a specific rater of a specific type
    List<Rating> findByRaterIdAndRaterType(Long raterId, Rating.RaterType raterType);

    // Find ratings received by a specific entity, potentially filtered by rater type
    List<Rating> findByRatedIdAndRaterType(Long ratedId, Rating.RaterType raterType);
}
