package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class LoginResponseDTO {

    private String  token;
    private String  tokenType = "Bearer";

    private Long    userId;
    private String  name;
    private String  email;
    private String  phone;
    private String  role;

    // Hotel info — only set if role = HotelOwner
    private Long    hotelId;
    private String  hotelName;

    private Long    OwnerId;
    private String OwnerName;
}
