package com.example.miniapp.repositories;

import com.example.miniapp.models.Rating;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface RatingRepository extends MongoRepository<Rating, String> {


    List<Rating> findByEntityIdAndEntityType(Long entityId, String entityType);


    List<Rating> findByScoreGreaterThanEqual(Integer minScore);


    List<Rating> findByEntityType(String entityType);


    List<Rating> findByScoreBetween(Integer minScore, Integer maxScore);
}