package com.MHM.MultiHotelManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "hotel_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HotelDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 1000, name = "owner_info", nullable = false)
    private String ownerSpeach;       // Owner এর বক্তব্য

    @Column(length = 1000)
    private String description;       // হোটেলের বিস্তারিত বর্ণনা

    @Column(length = 1000)
    private String hotelPolicy;       // হোটেলের নীতিমালা

    private String checkInTime;       // চেক-ইন সময়
    private String checkOutTime;      // চেক-আউট সময়
    private String contactEmail;      // যোগাযোগের ইমেইল
    private String contactPhone;      // যোগাযোগের ফোন নম্বর

    // প্রতিটি Hotel এর সাথে একটি HotelDetails যুক্ত থাকবে
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

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
