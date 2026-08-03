package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class EscalationRequestDTO {
    private String reason;
    private Long escalatedByUserId;
}
