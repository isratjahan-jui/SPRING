package com.MHM.MultiHotelManagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRequestDTO {
    private String name;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private Date dateOfBirth;
    private String image;
}
