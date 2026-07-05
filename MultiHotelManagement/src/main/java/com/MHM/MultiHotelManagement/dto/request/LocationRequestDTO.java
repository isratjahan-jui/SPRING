package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class LocationRequestDTO {

    private String locationName;
    private String city;
    // image আলাদা MultipartFile হিসেবে আসবে
}