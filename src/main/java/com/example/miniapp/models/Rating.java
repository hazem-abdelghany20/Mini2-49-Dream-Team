package com.example.miniapp.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Document(collection = "ratings") // Specifies the MongoDB collection name
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rating {

    @Id
    private String id; // MongoDB uses String IDs by default

    @Field("trip_id") // Map to a specific field name in MongoDB
    private Long tripId; // Reference to the relational Trip ID (PostgreSQL)

    @Field("rater_id")
    private Long raterId; // Can be Customer ID or Captain ID depending on who is rating

    @Field("rated_id")
    private Long ratedId; // Can be Captain ID or Customer ID depending on who is being rated

    @Field("rating_value")
    private int ratingValue; // e.g., 1 to 5 stars

    private String comment;

    @Field("rating_time")
    private LocalDateTime ratingTime;

    @Field("rater_type")
    private RaterType raterType; // To distinguish if the rater is a Customer or Captain

    public enum RaterType {
        CUSTOMER, CAPTAIN
    }
}
