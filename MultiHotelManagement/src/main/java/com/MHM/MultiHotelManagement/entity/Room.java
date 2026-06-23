package com.MHM.MultiHotelManagement.entity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String roomType;       // Single, Double, Suite ইত্যাদি
    private String image;
    private int totalRooms;
    private int adults;
    private int children;
    private Double price;
    private int availableRooms;
    private int bookedRooms;

    private String amenities;      // WiFi, AC, TV ইত্যাদি
    private String description;    // রুমের বিস্তারিত বর্ণনা

    // Room ↔ Hotel (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id")
    @JsonBackReference(value = "hotel-rooms")
    private Hotel hotel;

    // Room ↔ Booking (One-to-Many)
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "room-bookings")
    private List<Booking> bookings = new ArrayList<>();

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

