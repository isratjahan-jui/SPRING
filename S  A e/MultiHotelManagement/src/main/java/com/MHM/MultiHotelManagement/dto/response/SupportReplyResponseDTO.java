package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SupportReplyResponseDTO {
    private Long id;
    private String message;
    private Long ticketId;
    private String replierName;
    private String replierRole;
    private Boolean isInternal;
    private LocalDateTime createdAt;
}
