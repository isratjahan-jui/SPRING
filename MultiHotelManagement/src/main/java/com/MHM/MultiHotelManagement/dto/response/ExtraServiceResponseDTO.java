package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

@Data
public class ExtraServiceResponseDTO {
    private Long id;
    private String serviceType;
    private Double price;
    private String serviceStatus;
    private Long bookingId;

    private String bookingReference;   // বুকিং এর রেফারেন্স/কোড দেখানোর জন্য
}
