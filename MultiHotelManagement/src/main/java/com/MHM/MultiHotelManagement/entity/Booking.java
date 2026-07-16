package com.MHM.MultiHotelManagement.entity;

import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
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
    private BigDecimal totalPrice;


    private int numberOfRooms;
    private BigDecimal discountRate;

    private BigDecimal totalAmount;
    private BigDecimal advanceAmount;
    private BigDecimal dueAmount;

    // Food Cancel policy fields
    private Date foodCancellableUntil;
    private Boolean foodCancelled = false;

    // Online check-in / ID verification
    private boolean onlineCheckIn = false;
    private String idImagePath;
    private String digitalKey;

    // Cancellation policy
    private Date cancellationDeadline;
    private String cancellationPolicyText;

    // Extra charges added by hotel (food, minibar, damages)
    private BigDecimal extraCharges = BigDecimal.ZERO;

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

    // Booking ↔ Payment (One-to-Many)
    @OneToMany(mappedBy = "booking")
    private List<Payment> payments = new ArrayList<>();


    // Booking ↔ FoodItem (Many-to-Many)
    @ManyToMany
    @JoinTable(
            name = "booking_food_items",
            joinColumns = @JoinColumn(name = "booking_id"),
            inverseJoinColumns = @JoinColumn(name = "food_item_id")
    )
    @JsonIgnore
    private List<FoodItem> foodItems = new ArrayList<>();




    // Booking ↔ ExtraService (One-to-Many)
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ExtraService> extraServices = new ArrayList<>();

    // Convenience methods
    public void addFoodItem(FoodItem foodItem) {
        foodItems.add(foodItem);
        foodItem.getBookings().add(this);
    }

    public void removeFoodItem(FoodItem foodItem) {
        foodItems.remove(foodItem);
        foodItem.getBookings().remove(this);
    }

    public void addExtraService(ExtraService extraService) {
        extraServices.add(extraService);
        extraService.setBooking(this);
    }

}
