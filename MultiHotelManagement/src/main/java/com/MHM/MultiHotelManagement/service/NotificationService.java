package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.NotificationResponseDTO;

import java.util.List;

public interface NotificationService {
    NotificationResponseDTO createNotification(NotificationRequestDTO dto);
    void markAsRead(Long id);
    void deleteNotification(Long id);
    List<NotificationResponseDTO> getNotificationsByUser(Long userId);
    List<NotificationResponseDTO> getUnreadNotifications(Long userId);
}
