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
    private Integer usageLimit;
    private Integer usedCount;
    private String hotelName;
    private Long hotelId;
    private boolean active;
}