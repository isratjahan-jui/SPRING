package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.SupportReplyResponseDTO;
import com.MHM.MultiHotelManagement.entity.SupportReply;

public class SupportReplyMapper {
    public static SupportReplyResponseDTO toDTO(SupportReply reply) {
        SupportReplyResponseDTO dto = new SupportReplyResponseDTO();
        dto.setId(reply.getId());
        dto.setMessage(reply.getMessage());
        dto.setTicketId(reply.getTicket().getId());
        dto.setReplierName(reply.getReplier().getName());
        dto.setReplierRole(reply.getReplier().getRole() != null ? reply.getReplier().getRole().name() : null);
        dto.setIsInternal(reply.getIsInternal());
        dto.setCreatedAt(reply.getCreatedAt());
        return dto;
    }
}
