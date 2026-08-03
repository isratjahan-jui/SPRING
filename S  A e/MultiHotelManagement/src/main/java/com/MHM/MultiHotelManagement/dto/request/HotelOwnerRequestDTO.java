package com.MHM.MultiHotelManagement.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.Date;

@Data
public class HotelOwnerRequestDTO {

    private String name;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private Date dateOfBirth;
    private String image;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;



}
