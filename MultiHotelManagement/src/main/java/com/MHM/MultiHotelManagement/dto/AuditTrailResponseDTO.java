package com.MHM.MultiHotelManagement.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditTrailResponseDTO {

    private Long id;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;
    private String performedBy;
    private String ipAddress;
    private LocalDateTime createdAt;
}