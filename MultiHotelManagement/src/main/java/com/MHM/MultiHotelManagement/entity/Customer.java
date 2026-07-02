package com.MHM.MultiHotelManagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class    Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;

    @Column(unique = true, nullable = false)
    private String email;

    private String phone;
    private String address;
    private String gender;

    // LocalDate ব্যবহার করো — @Temporal লাগবে না
    private LocalDate dateOfBirth;

    private String image;

    // Auth account — source of truth for login credentials
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // একজন Customer অনেকগুলো Booking করতে পারে
    @JsonIgnore
    @OneToMany(mappedBy = "customer",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Booking> bookings = new ArrayList<>();
}