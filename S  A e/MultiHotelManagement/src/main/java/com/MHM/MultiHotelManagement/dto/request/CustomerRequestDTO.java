package com.MHM.MultiHotelManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerRequestDTO {

    // User account fields (auth)
    private String name;
    private String email;
    private String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    // Customer profile fields
    private String customerName;
    private String address;
    private String gender;       // MALE / FEMALE / OTHER
    private String dateOfBirth;  // "1995-08-21" format
    private String image;

}