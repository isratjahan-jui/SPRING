package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.AuditTrailResponseDTO;
import com.MHM.MultiHotelManagement.entity.AuditTrail;
import com.MHM.MultiHotelManagement.repository.AuditTrailRepository;
import com.MHM.MultiHotelManagement.service.AuditTrailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuditTrailServiceImpl implements AuditTrailService {

    private final AuditTrailRepository auditTrailRepository;

    @Override
    @Transactional
    public void logAction(String action, String entityType, Long entityId, String details, String performedBy) {
        AuditTrail trail = new AuditTrail();
        trail.setAction(action);
        trail.setEntityType(entityType);
        trail.setEntityId(entityId);
        trail.setDetails(details);
        trail.setPerformedBy(performedBy);
        auditTrailRepository.save(trail);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditTrailResponseDTO> getAuditLogByEntity(String entityType, Long entityId) {
        return auditTrailRepository.findByEntityTypeAndEntityId(entityType, entityId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditTrailResponseDTO> getAuditLogByEntityId(Long entityId) {
        return auditTrailRepository.findByEntityIdOrderByCreatedAtDesc(entityId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private AuditTrailResponseDTO toDTO(AuditTrail trail) {
        AuditTrailResponseDTO dto = new AuditTrailResponseDTO();
        dto.setId(trail.getId());
        dto.setAction(trail.getAction());
        dto.setEntityType(trail.getEntityType());
        dto.setEntityId(trail.getEntityId());
        dto.setDetails(trail.getDetails());
        dto.setPerformedBy(trail.getPerformedBy());
        dto.setIpAddress(trail.getIpAddress());
        dto.setCreatedAt(trail.getCreatedAt());
        return dto;
    }
}