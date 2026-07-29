package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.CustomerSupportResponseDTO;
import com.MHM.MultiHotelManagement.entity.CustomerSupport;

import java.time.Duration;

public class CustomerSupportMapper {
    public static CustomerSupportResponseDTO toDTO(CustomerSupport ticket) {
        CustomerSupportResponseDTO dto = new CustomerSupportResponseDTO();
        dto.setId(ticket.getId());
        dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        dto.setPriority(ticket.getPriority());
        dto.setCategory(ticket.getCategory());
        dto.setCustomerName(ticket.getCustomer().getCustomerName());
        dto.setCustomerId(ticket.getCustomer().getId());
        dto.setHotelName(ticket.getHotel() != null ? ticket.getHotel().getHotelName() : null);
        dto.setHotelId(ticket.getHotel() != null ? ticket.getHotel().getId() : null);
        dto.setAgentName(ticket.getAgent() != null ? ticket.getAgent().getName() : null);
        dto.setAgentId(ticket.getAgent() != null ? ticket.getAgent().getId() : null);
        dto.setReplyCount(ticket.getReplies() != null ? ticket.getReplies().size() : 0);
        dto.setEscalated(ticket.getEscalated() != null ? ticket.getEscalated() : false);
        dto.setFirstResponseAt(ticket.getFirstResponseAt());
        dto.setResolvedAt(ticket.getResolvedAt());

        if (ticket.getFirstResponseAt() != null && ticket.getCreatedAt() != null) {
            dto.setResponseTimeMinutes(Duration.between(ticket.getCreatedAt(), ticket.getFirstResponseAt()).toMinutes());
        }
        if (ticket.getResolvedAt() != null && ticket.getCreatedAt() != null) {
            dto.setResolutionTimeMinutes(Duration.between(ticket.getCreatedAt(), ticket.getResolvedAt()).toMinutes());
        }

        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        return dto;
    }
}
