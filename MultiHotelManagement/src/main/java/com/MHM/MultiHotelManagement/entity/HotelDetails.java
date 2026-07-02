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

    // Owner এর বক্তব্য
    @Column(length = 1000, name = "owner_info", nullable = false)
    private String ownerSpeach;

    // হোটেলের বিস্তারিত বর্ণনা
    @Column(length = 1000)
    private String description;

    // হোটেলের নীতিমালা
    @Column(length = 1000)
    private String hotelPolicy;

    private Double    pricePerNight;

    // Check-in / Check-out সময়
    private String checkInTime;
    private String checkOutTime;

    // যোগাযোগের তথ্য
    private String contactEmail;
    private String contactPhone;

    // Extra policies
    @Column(length = 1000)
    private String cancellationPolicy;

    @Column(length = 1000)
    private String petPolicy;

    @Column(length = 1000)
    private String smokingPolicy;

    @Column(length = 1000)
    private String childPolicy;

    @Column(length = 1000)
    private String languages;

    @Column(length = 1000)
    private String nearbyAttractions;

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
