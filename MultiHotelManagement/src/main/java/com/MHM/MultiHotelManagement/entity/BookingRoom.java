package com.MHM.MultiHotelManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "booking_rooms")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int numberOfRooms;   // kotogulo room book kora hoyeche
    private int adults;          // koyjon adults
    private int children;        // koyjon children
    private Double price;        // total room price

    // BookingRoom ↔ Booking (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // BookingRoom ↔ Room (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;
}
