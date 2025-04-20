package com.example.miniapp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Entity
@Table(name = "captains")
@Data // Lombok annotation for getters, setters, toString, equals, hashCode
@NoArgsConstructor
@AllArgsConstructor
public class Captain {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String licenseNumber;

    private Double avgRatingScore;
}
