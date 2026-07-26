package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class LocationRequestDTO {

    private String locationName;
    private String city;
    private String district;
    private String division;
    private String upazila;
}
