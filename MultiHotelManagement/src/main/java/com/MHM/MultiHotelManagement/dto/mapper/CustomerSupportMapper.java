package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.CustomerSupportResponseDTO;
import com.MHM.MultiHotelManagement.entity.CustomerSupport;

public class CustomerSupportMapper {
    public static CustomerSupportResponseDTO toDTO(CustomerSupport ticket) {
        CustomerSupportResponseDTO dto = new CustomerSupportResponseDTO();
        dto.setId(ticket.getId());
        dto.setSubject(ticket.getSubject());
        dto.setDescription(ticket.getDescription());
        dto.setStatus(ticket.getStatus());
        dto.setPriority(ticket.getPriority());
        dto.setCustomerName(ticket.getCustomer().getCustomerName());
        dto.setAgentName(ticket.getAgent() != null ? ticket.getAgent().getName() : null);
        dto.setCreatedAt(ticket.getCreatedAt());
        dto.setUpdatedAt(ticket.getUpdatedAt());
        return dto;
    }
}
