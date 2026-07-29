package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class HotelExtraServiceRequestDTO {
    private String serviceName;
    private String description;
    private Double price;
    private Long hotelId;
}
