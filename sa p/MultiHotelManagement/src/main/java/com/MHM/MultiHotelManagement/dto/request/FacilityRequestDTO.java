package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class FacilityRequestDTO {

    private Long hotelId;           // কোন hotel এর facility
    private String facilityName;    // WiFi, AC, Pool ইত্যাদি
    private String description;     // বিস্তারিত বর্ণনা
}