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
@Table(name = "hotels")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hotelName;
    private String address;            //ঠিকানা
    private String description;
    private String rating;        // রেটিং (স্টার বা স্কোর)
    private String image;         // হোটেলের ছবি
    private Boolean approved;   // Admin approval status

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
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();

    // Hotel ↔ FoodItem (One-to-Many)
    @JsonIgnore
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<FoodItem> foodItems = new ArrayList<>();

//    // Hotel ↔ HotelAmenities (One-to-One)
//    @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL)
//    private HotelAmenities hotelAmenities;

    // Hotel ↔ HotelInformation (One-to-One)
    @OneToOne(mappedBy = "hotel", cascade = CascadeType.ALL)
    private HotelDetails hotelDetails;

    // Hotel ↔ HotelPhoto (One-to-Many)
    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<Gallery> galleries = new ArrayList<>();

}
