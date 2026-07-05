package com.MHM.MultiHotelManagement.entity;

import com.MHM.MultiHotelManagement.enums.DealType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "deals")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Deals {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String dealTitle;       // অফারের নাম
    private String description;     // অফারের বিস্তারিত
    private Double discountPercent; // কত % ছাড়
    private Double discountAmount;  // নির্দিষ্ট টাকা ছাড়

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    private DealType dealType;   // Enum ব্যবহার করা হলো

    private Boolean isActive = true; // Active status field যোগ করা হলো

    // Deals ↔ Hotel (Many-to-One)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    // Deals ↔ Room (Optional Many-to-One)
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

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
