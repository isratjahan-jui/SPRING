package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CouponRequestDTO {
    private String code;
    private Double discountPercent;
    private Double discountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Long hotelId;
}