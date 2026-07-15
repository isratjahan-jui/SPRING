package com.MHM.MultiHotelManagement.dto.response;

import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketPriority;
import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CustomerSupportResponseDTO {
    private Long id;
    private String subject;
    private String description;
    private CustomerSupportTicketStatus status;
    private CustomerSupportTicketPriority priority;
    private String customerName;
    private String agentName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
