package com.MHM.MultiHotelManagement.service;


import com.MHM.MultiHotelManagement.dto.request.CustomerSupportRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerSupportResponseDTO;

import java.util.List;

public interface CustomerSupportService {
    CustomerSupportResponseDTO createTicket(CustomerSupportRequestDTO dto);
    CustomerSupportResponseDTO updateTicket(Long id, CustomerSupportRequestDTO dto);
    void closeTicket(Long id);
    List<CustomerSupportResponseDTO> getTicketsByCustomer(Long customerId);
    List<CustomerSupportResponseDTO> getTicketsByAgent(Long agentId);
}
