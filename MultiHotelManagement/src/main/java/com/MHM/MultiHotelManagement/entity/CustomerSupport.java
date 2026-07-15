package com.MHM.MultiHotelManagement.entity;

import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketPriority;
import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "support_tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerSupport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;          // সমস্যার শিরোনাম
    private String description;      // সমস্যার বিস্তারিত

    @Enumerated(EnumType.STRING)
    private CustomerSupportTicketStatus status;     // PENDING, IN_PROGRESS, RESOLVED, CLOSED

    @Enumerated(EnumType.STRING)
    private CustomerSupportTicketPriority priority; // LOW, MEDIUM, HIGH, URGENT

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;       // কোন গ্রাহক ticket দিয়েছে

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id")
    private User agent;              // কোন agent ticket handle করছে

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