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

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true)
    private String phoneNumber;

    // One Captain can have Many Trips
    @OneToMany(mappedBy = "captain", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Trip> trips;

    // Example custom method or property
    private String vehicleDetails; // e.g., "Toyota Camry 2020"

}
