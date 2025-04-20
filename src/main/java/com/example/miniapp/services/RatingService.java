package com.example.miniapp.services;

import com.example.miniapp.models.Rating;
import com.example.miniapp.models.Captain;
import com.example.miniapp.models.Customer;
import com.example.miniapp.repositories.RatingRepository;
import com.example.miniapp.repositories.TripRepository; // To validate trip exists
import com.example.miniapp.repositories.CustomerRepository; // To validate customer exists
import com.example.miniapp.repositories.CaptainRepository; // To validate captain exists
import jakarta.persistence.EntityNotFoundException; // Reusing JPA exception for simplicity
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
// No @Transactional needed usually for MongoRepository methods unless combining with JPA

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;
    // Inject relational repositories if validation is needed
    private final TripRepository tripRepository;
    private final CustomerRepository customerRepository;
    private final CaptainRepository captainRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository,
                         TripRepository tripRepository,
                         CustomerRepository customerRepository,
                         CaptainRepository captainRepository) {
        this.ratingRepository = ratingRepository;
        this.tripRepository = tripRepository;
        this.customerRepository = customerRepository;
        this.captainRepository = captainRepository;
    }

    public List<Rating> getAllRatings() {
        return ratingRepository.findAll();
    }

    public Optional<Rating> getRatingById(String id) {
        return ratingRepository.findById(id);
    }

    public Rating createRating(Rating rating) {
        // --- Validation --- 
        // 1. Validate Trip exists
        tripRepository.findById(rating.getTripId())
            .orElseThrow(() -> new EntityNotFoundException("Cannot create rating: Trip not found with id " + rating.getTripId()));

        // 2. Validate Rater and Rated entities exist based on RaterType
        if (rating.getRaterType() == Rating.RaterType.CUSTOMER) {
            customerRepository.findById(rating.getRaterId())
                .orElseThrow(() -> new EntityNotFoundException("Cannot create rating: Rater (Customer) not found with id " + rating.getRaterId()));
            captainRepository.findById(rating.getRatedId())
                .orElseThrow(() -> new EntityNotFoundException("Cannot create rating: Rated (Captain) not found with id " + rating.getRatedId()));
        } else { // Rater is CAPTAIN
             captainRepository.findById(rating.getRaterId())
                .orElseThrow(() -> new EntityNotFoundException("Cannot create rating: Rater (Captain) not found with id " + rating.getRaterId()));
            customerRepository.findById(rating.getRatedId())
                .orElseThrow(() -> new EntityNotFoundException("Cannot create rating: Rated (Customer) not found with id " + rating.getRatedId()));
        }
        
        // 3. Check rating value bounds
        if (rating.getRatingValue() < 1 || rating.getRatingValue() > 5) {
             throw new IllegalArgumentException("Rating value must be between 1 and 5.");
        }

        // 4. TODO: Optional - Check if a rating already exists for this trip/rater/rated combo?

        // --- End Validation ---

        // Set rating time if not provided
        if (rating.getRatingTime() == null) {
            rating.setRatingTime(LocalDateTime.now());
        }

        return ratingRepository.save(rating);
    }

    public Optional<Rating> updateRating(String id, Rating ratingDetails) {
        return ratingRepository.findById(id)
            .map(existingRating -> {
                // Usually, only comment and maybe ratingValue can be updated
                existingRating.setComment(ratingDetails.getComment());
                if (ratingDetails.getRatingValue() >= 1 && ratingDetails.getRatingValue() <= 5) {
                    existingRating.setRatingValue(ratingDetails.getRatingValue());
                } else {
                     throw new IllegalArgumentException("Rating value must be between 1 and 5.");
                }
                // You generally wouldn't change tripId, raterId, ratedId, raterType, or ratingTime
                return ratingRepository.save(existingRating);
            });
    }

    public boolean deleteRating(String id) {
        return ratingRepository.findById(id)
            .map(rating -> {
                ratingRepository.delete(rating);
                return true;
            }).orElse(false);
    }

    // Custom query methods
    public List<Rating> getRatingsByTripId(Long tripId) {
        return ratingRepository.findByTripId(tripId);
    }

    public List<Rating> getRatingsByRater(Long raterId) {
        return ratingRepository.findByRaterId(raterId);
    }

    public List<Rating> getRatingsForRated(Long ratedId) {
        return ratingRepository.findByRatedId(ratedId);
    }

    public List<Rating> getRatingsByRaterType(Rating.RaterType raterType) {
        return ratingRepository.findByRaterType(raterType);
    }
    
    // Example: Calculate average rating for a Captain
    public Double getAverageRatingForCaptain(Long captainId) {
        List<Rating> ratings = ratingRepository.findByRatedIdAndRaterType(captainId, Rating.RaterType.CUSTOMER);
        if (ratings.isEmpty()) {
            return null; // Or 0.0, depending on requirements
        }
        return ratings.stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0.0); // Default if stream is somehow empty after filtering
    }

     // Example: Calculate average rating for a Customer
    public Double getAverageRatingForCustomer(Long customerId) {
        List<Rating> ratings = ratingRepository.findByRatedIdAndRaterType(customerId, Rating.RaterType.CAPTAIN);
         if (ratings.isEmpty()) {
            return null; // Or 0.0
        }
        return ratings.stream()
                .mapToInt(Rating::getRatingValue)
                .average()
                .orElse(0.0); 
    }

}
