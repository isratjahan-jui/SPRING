package com.MHM.MultiHotelManagement.dto.request;

import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketPriority;
import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketStatus;
import lombok.Data;

@Data
public class CustomerSupportRequestDTO {
    private String subject;
    private String description;
    private CustomerSupportTicketStatus status;
    private CustomerSupportTicketPriority priority;
    private String category;
    private Long customerId;
    private Long hotelId;
    private Long agentId;
}
