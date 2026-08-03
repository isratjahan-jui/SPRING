package com.MHM.MultiHotelManagement.entity;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "commissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Commission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal commissionRate;
    private BigDecimal paymentAmount;
    private BigDecimal adminEarnings;
    private BigDecimal hotelOwnerEarnings;

    @Column(nullable = false)
    private String commissionStatus = "ACTIVE";

    // protiti Commission ekti Booking er sathe jukto
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Commission ↔ Payment (One-to-One)
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payment payment;

//    @OneToOne
//    @JoinColumn(name = "payment_id", nullable = true)
//    private Payment payment;

    // Commission ↔ ExtraService (Optional)
    @OneToOne
    @JoinColumn(name = "extra_service_id", nullable = true)
    private ExtraService extraService;




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

