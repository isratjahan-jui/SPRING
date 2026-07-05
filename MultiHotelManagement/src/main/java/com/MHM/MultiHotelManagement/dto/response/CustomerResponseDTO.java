package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class CustomerResponseDTO {

    private Long id;

    // From User (auth account)
    private Long userId;
    private String name;
    private String email;
    private String phone;
    private String role;

    // Customer profile
    private String customerName;
    private String address;
    private String gender;
    private String dateOfBirth;
    private String image;
}