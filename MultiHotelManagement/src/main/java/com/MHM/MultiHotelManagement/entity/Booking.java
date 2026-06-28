package com.MHM.MultiHotelManagement.entity;

import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String contractPersonName;
    private String phone;
    private Date bookingDate;
    private Date checkInDate;
    private Date checkOutDate;

    private int totalGuests;
    private Double totalPrice;


    private int numberOfRooms;
    private double discountRate;

    private double totalAmount;
    private double advanceAmount;
    private double dueAmount;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;


    // একজন Customer অনেকগুলো Booking করতে পারে
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // একটি Hotel এ অনেক Booking হতে পারে
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    // একটি Room এ অনেক Booking হতে পারে
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // Future: Payment entity এর সাথে সম্পর্ক
    @JsonIgnore

    // Booking ↔ Payment (One-to-One)
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;


}

