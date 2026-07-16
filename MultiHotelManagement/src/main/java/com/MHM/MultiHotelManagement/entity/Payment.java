package com.MHM.MultiHotelManagement.entity;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String method;
    private BigDecimal amount;
    private Long customerId;
    private LocalDateTime transactionDate;


    @Enumerated(EnumType.STRING)
    private PaymentStatus status; // PENDING, SUCCESS, FAILED


    // Payment ↔ Booking (Many-to-One)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    // Payment ↔ ExtraService (Optional)
    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "extra_service_id", nullable = true)
    private ExtraService extraService;

    // Payment ↔ Commission (One-to-One)
    @JsonIgnore
    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Commission commission;

    // SSLCommerz fields
    private String transactionId;
    private String bankTransactionId;
    private String validationId;

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
