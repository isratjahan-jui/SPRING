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
    private String category;
    private String customerName;
    private Long customerId;
    private String hotelName;
    private Long hotelId;
    private String agentName;
    private Long agentId;
    private Integer replyCount;
    private Boolean escalated;
    private LocalDateTime firstResponseAt;
    private LocalDateTime resolvedAt;
    private Long responseTimeMinutes;
    private Long resolutionTimeMinutes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
