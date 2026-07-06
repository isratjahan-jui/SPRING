package com.MHM.MultiHotelManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "food_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FoodItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String itemName;       // খাবারের নাম
    private String description;    // বিস্তারিত বর্ণনা
    private Double foodPrice;      // খাবারের দাম
    private String category;       // BREAKFAST, LUNCH, DINNER, BEVERAGE

    // প্রতিটি FoodItem একটি Hotel এর সাথে যুক্ত
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    // এক Booking এ একাধিক FoodItem থাকতে পারে
    @ManyToMany(mappedBy = "foodItems")
    @JsonIgnore
    private List<Booking> bookings = new ArrayList<>();

    // Cancel policy fields
    private Boolean cancelled = false;              // খাবার cancel হয়েছে কিনা
    private LocalDateTime cancellableUntil;         // নির্দিষ্ট সময় পর্যন্ত cancel করা যাবে
    private LocalDateTime orderedAt;                // কখন অর্ডার করা হলো
    private LocalDateTime cancelledAt;              // কখন cancel করা হলো

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        orderedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
