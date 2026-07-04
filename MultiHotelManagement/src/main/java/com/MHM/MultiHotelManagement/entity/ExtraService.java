package com.MHM.MultiHotelManagement.entity;

import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.ServiceStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "extra_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtraService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String serviceType;   // Laundry, Transport, Spa ইত্যাদি
    private Double price;


    // Cancel policy fields
    private Boolean cancelled = false;
    private LocalDateTime cancellableUntil;
    private LocalDateTime cancelledAt;

    // Enum ব্যবহার করলে ভুল status এড়ানো যাবে
    @Enumerated(EnumType.STRING)
    private ServiceStatus serviceStatus;   // PENDING, COMPLETED, CANCELLED

    // প্রতিটি ExtraService একটি Booking এর সাথে যুক্ত
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // ExtraService ↔ Payment (Optional)
    @OneToOne(mappedBy = "extraService")
    private Payment payment;



    // Audit fields
    private String createdBy;
    private String updatedBy;

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
