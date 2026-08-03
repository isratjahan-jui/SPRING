package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.CustomerSupportRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.EscalationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerSupportResponseDTO;
import com.MHM.MultiHotelManagement.dto.response.SupportStatsResponseDTO;

import java.util.List;

public interface CustomerSupportService {
    CustomerSupportResponseDTO createTicket(CustomerSupportRequestDTO dto);
    CustomerSupportResponseDTO updateTicket(Long id, CustomerSupportRequestDTO dto);
    void closeTicket(Long id);
    CustomerSupportResponseDTO getTicketById(Long id);
    List<CustomerSupportResponseDTO> getAllTickets();
    List<CustomerSupportResponseDTO> getTicketsByCustomer(Long customerId);
    List<CustomerSupportResponseDTO> getTicketsByAgent(Long agentId);
    List<CustomerSupportResponseDTO> getTicketsByHotel(Long hotelId);
    CustomerSupportResponseDTO assignAgent(Long ticketId, Long agentId);
    CustomerSupportResponseDTO escalateTicket(Long ticketId, EscalationRequestDTO dto);
    SupportStatsResponseDTO getStats();
    SupportStatsResponseDTO getStatsByHotel(Long hotelId);
}
