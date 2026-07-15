package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.NotificationResponseDTO;
import com.MHM.MultiHotelManagement.entity.Notification;

public class NotificationMapper {
    public static NotificationResponseDTO toDTO(Notification notification) {
        NotificationResponseDTO dto = new NotificationResponseDTO();
        dto.setId(notification.getId());
        dto.setMessage(notification.getMessage());
        dto.setType(notification.getType());
        dto.setChannel(notification.getChannel());
        dto.setReadStatus(notification.getReadStatus());
        dto.setUserName(notification.getUser().getName());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());
        return dto;
    }
}
