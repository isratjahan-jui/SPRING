package com.MHM.MultiHotelManagement.dto.response;

import com.MHM.MultiHotelManagement.enums.DealType;
import lombok.Data;

import java.time.LocalDateTime;
@Data
public class DealsResponseDTO {
    private Long id;
    private String dealTitle;
    private String description;
    private Double discountPercent;
    private Double discountAmount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private DealType dealType;
    private String hotelName;
    private String roomType;
    private Boolean isActive;
}
