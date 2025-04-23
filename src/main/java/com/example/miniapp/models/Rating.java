package com.example.miniapp.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Document representing a rating record stored in MongoDB.
 * This class stores feedback information for captains, customers, or trips.
 */
@Document(collection = "ratings")
public class Rating {

    @Id
    private String id;

    private Long entityId;

    private String entityType;

    private Integer score;

    private String comment;

    private LocalDateTime ratingDate;

    /**
     * Default constructor
     */
    public Rating() {
        // Required by MongoDB
    }

    /**
     * Partial constructor with essential fields
     *
     * @param entityId ID of the entity being rated (captain, customer, or trip)
     * @param entityType Type of entity being rated (captain, customer, or trip)
     * @param score Rating score (1-5)
     */
    public Rating(Long entityId, String entityType, Integer score) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.score = score;
        this.ratingDate = LocalDateTime.now();
    }

    /**
     * Full constructor without ID
     *
     * @param entityId ID of the entity being rated (captain, customer, or trip)
     * @param entityType Type of entity being rated (captain, customer, or trip)
     * @param score Rating score (1-5)
     * @param comment Optional comment about the rating
     * @param ratingDate Date and time when the rating was submitted
     */
    public Rating(Long entityId, String entityType, Integer score, String comment, LocalDateTime ratingDate) {
        this.entityId = entityId;
        this.entityType = entityType;
        this.score = score;
        this.comment = comment;
        this.ratingDate = ratingDate;
    }

    /**
     * Full constructor with ID (for testing purposes)
     *
     * @param id Rating ID
     * @param entityId ID of the entity being rated (captain, customer, or trip)
     * @param entityType Type of entity being rated (captain, customer, or trip)
     * @param score Rating score (1-5)
     * @param comment Optional comment about the rating
     * @param ratingDate Date and time when the rating was submitted
     */
    public Rating(String id, Long entityId, String entityType, Integer score, String comment, LocalDateTime ratingDate) {
        this.id = id;
        this.entityId = entityId;
        this.entityType = entityType;
        this.score = score;
        this.comment = comment;
        this.ratingDate = ratingDate;
    }

    // Getters and setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getRatingDate() {
        return ratingDate;
    }

    public void setRatingDate(LocalDateTime ratingDate) {
        this.ratingDate = ratingDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return Objects.equals(id, rating.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Rating{" +
                "id='" + id + '\'' +
                ", entityId=" + entityId +
                ", entityType='" + entityType + '\'' +
                ", score=" + score +
                ", comment='" + comment + '\'' +
                ", ratingDate=" + ratingDate +
                '}';
    }
}