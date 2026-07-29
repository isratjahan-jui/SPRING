package com.MHM.MultiHotelManagement.dto.request;

import lombok.Data;

@Data
public class SupportReplyRequestDTO {
    private String message;
    private Long ticketId;
    private Long replierId;
    private Boolean isInternal = false;
}
