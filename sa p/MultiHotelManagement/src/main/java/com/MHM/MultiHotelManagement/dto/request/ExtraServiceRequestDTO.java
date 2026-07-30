package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class ExtraServiceRequestDTO {

    private String serviceType;
    private Double price;
    private String serviceStatus;   // Enum value as String: "PENDING", "COMPLETED", "CANCELLED"
    private Long bookingId;           // কোন বুকিং এর সাথে যুক্ত হবে
}
