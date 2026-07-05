package com.MHM.MultiHotelManagement.entity;
import com.MHM.MultiHotelManagement.enums.HotelStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hotelName;
    private String address;
    private String description;
    private Double pricePerNight;
    private String rating;
    private String image;

    @Enumerated(EnumType.STRING)
    private HotelStatus status;

//    private Boolean approved;   // Admin approval status

    // Extra fields for food service
    private Boolean foodAvailable;
    private String foodServiceHours;


    // Location ↔ Hotel (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    // Owner ↔ Hotel (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private HotelOwner owner;

    // Hotel ↔ Facility (One-to-Many)
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Facility> facilities = new ArrayList<>();

    // Hotel ↔ Room (One-to-Many)
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Room> rooms = new ArrayList<>();

    // Hotel ↔ Booking (One-to-Many)
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    // Hotel ↔ FoodItem (One-to-Many)
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FoodItem> foodItems = new ArrayList<>();



    // Hotel ↔ HotelInformation (One-to-One)
    @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    private HotelDetails hotelDetails;

    // Hotel ↔ HotelPhoto (One-to-Many)
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Gallery> galleries = new ArrayList<>();



    // Convenience methods for FoodItem
    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
        foodItem.setHotel(this);
    }

    public void removeFoodItem(FoodItem foodItem) {
        foodItems.remove(foodItem);
        foodItem.setHotel(null);
    }


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
