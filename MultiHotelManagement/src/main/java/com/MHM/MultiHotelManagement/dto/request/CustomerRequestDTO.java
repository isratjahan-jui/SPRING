package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class CustomerRequestDTO {

    // User account fields (auth)
    private String name;
    private String email;
    private String phone;
    private String password;

    // Customer profile fields
    private String customerName;
    private String address;
    private String gender;       // MALE / FEMALE / OTHER
    private String dateOfBirth;  // "1995-08-21" format
    private String image;

}