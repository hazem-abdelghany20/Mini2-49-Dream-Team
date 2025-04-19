package com.example.miniapp.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime paymentTime;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // e.g., PENDING, COMPLETED, FAILED

    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod; // e.g., CREDIT_CARD, CASH

    // One Payment is associated with One Trip
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id", referencedColumnName = "id", nullable = false, unique = true)
    private Trip trip;

    public enum PaymentStatus {
        PENDING, COMPLETED, FAILED
    }

    public enum PaymentMethod {
        CREDIT_CARD, DEBIT_CARD, CASH, WALLET
    }
}
