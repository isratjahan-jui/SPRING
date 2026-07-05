package com.MHM.MultiHotelManagement.entity;

import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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

    // Food Cancel policy fields
    private Date foodCancellableUntil;
    private Boolean foodCancelled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private BookingStatus status;


    // one Customer many Booking korte parbe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    // one Hotel  many Booking korte parbe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    // one Room many Booking korte parbe
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    // Future: Payment entity er sathe relation
    @JsonIgnore

    // Booking ↔ Payment (One-to-One)
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private Payment payment;


    // Booking ↔ FoodItem (Many-to-Many)
    @ManyToMany
    @JoinTable(
            name = "booking_food_items",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "food_item_id")
    )
    private List<FoodItem> foodItems = new ArrayList<>();




    // Convenience methods
    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
        foodItem.getBookings().add(this);
    }

    public void removeFoodItem(FoodItem foodItem) {
        foodItems.remove(foodItem);
        foodItem.getBookings().remove(this);
    }



}

