package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.AuditTrailResponseDTO;
import com.MHM.MultiHotelManagement.service.AuditTrailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
public class AuditTrailController {

    private final AuditTrailService auditTrailService;

    @PostMapping("/log")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_OWNER', 'CUSTOMER')")
    public ResponseEntity<Void> logAction(
            @RequestParam String action,
            @RequestParam String entityType,
            @RequestParam Long entityId,
            @RequestParam String details) {
        auditTrailService.logAction(action, entityType, entityId, details, "system");
        return ResponseEntity.ok().build();
    }

    @GetMapping("/entity/{entityType}/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_OWNER')")
    public ResponseEntity<List<AuditTrailResponseDTO>> getAuditLogByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditTrailService.getAuditLogByEntity(entityType, entityId));
    }

    @GetMapping("/entity/{entityId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<List<AuditTrailResponseDTO>> getAuditLogByEntityId(
            @PathVariable Long entityId) {
        return ResponseEntity.ok(auditTrailService.getAuditLogByEntityId(entityId));
    }
}