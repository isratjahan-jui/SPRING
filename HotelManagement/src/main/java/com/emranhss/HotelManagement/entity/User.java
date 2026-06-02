package com.emranhss.HotelManagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String name;
    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;

    @Size(max = 20, min = 4)
    private String password;



    private String role;


}
