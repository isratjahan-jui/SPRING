package com.MHM.MultiHotelManagement.entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String method;     // CARD, CASH, BKASH ইত্যাদি
    private Double amount;
    private String status;     // PENDING, SUCCESS, FAILED

    // Payment ↔ Booking (One-to-One)
    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;




    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

