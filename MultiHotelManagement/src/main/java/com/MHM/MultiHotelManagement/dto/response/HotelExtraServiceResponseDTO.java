package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class HotelExtraServiceResponseDTO {
    private Long id;
    private String serviceName;
    private String description;
    private Double price;
    private Boolean isActive;
    private Long hotelId;
    private String hotelName;
}
