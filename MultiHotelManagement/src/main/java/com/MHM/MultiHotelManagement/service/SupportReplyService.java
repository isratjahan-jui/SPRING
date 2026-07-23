package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.SupportReplyRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.SupportReplyResponseDTO;

import java.util.List;

public interface SupportReplyService {
    SupportReplyResponseDTO addReply(SupportReplyRequestDTO dto);
    List<SupportReplyResponseDTO> getRepliesByTicket(Long ticketId);
}
