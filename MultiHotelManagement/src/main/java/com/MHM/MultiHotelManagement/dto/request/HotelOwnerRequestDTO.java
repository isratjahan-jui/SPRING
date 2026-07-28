package com.MHM.MultiHotelManagement.dto.request;

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

    private String password;



}
