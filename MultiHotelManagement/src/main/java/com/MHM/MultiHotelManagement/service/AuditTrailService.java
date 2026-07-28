package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.AuditTrailResponseDTO;

import java.util.List;

public interface AuditTrailService {

    void logAction(String action, String entityType, Long entityId, String details, String performedBy);

    List<AuditTrailResponseDTO> getAuditLogByEntity(String entityType, Long entityId);

    List<AuditTrailResponseDTO> getAuditLogByEntityId(Long entityId);
}