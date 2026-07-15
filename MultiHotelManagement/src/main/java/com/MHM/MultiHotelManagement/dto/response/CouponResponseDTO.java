package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CouponResponseDTO {
    private Long id;
    private String code;
    private Double discountPercent;
    private Double discountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private String hotelName;
    private boolean active;
}