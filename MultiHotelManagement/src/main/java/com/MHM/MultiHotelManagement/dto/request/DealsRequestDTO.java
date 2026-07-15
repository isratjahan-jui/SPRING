package com.MHM.MultiHotelManagement.dto.request;

import com.MHM.MultiHotelManagement.enums.DealType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DealsRequestDTO {
    private String dealTitle;
    private String description;
    private Double discountPercent;
    private Double discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long hotelId;
    private Long roomId;
    private DealType dealType;
}
