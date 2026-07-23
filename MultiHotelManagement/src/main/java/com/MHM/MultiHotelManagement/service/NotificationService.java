package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.NotificationResponseDTO;
import com.MHM.MultiHotelManagement.enums.Role;

import java.util.List;

public interface NotificationService {
    NotificationResponseDTO createNotification(NotificationRequestDTO dto);
    void markAsRead(Long id);
    void deleteNotification(Long id);
    List<NotificationResponseDTO> getNotificationsByUser(Long userId);
    List<NotificationResponseDTO> getUnreadNotifications(Long userId);

    List<NotificationResponseDTO> getNotificationsByUserAndRole(Long userId, Role role);
    List<NotificationResponseDTO> getUnreadByUserAndRole(Long userId, Role role);
    List<NotificationResponseDTO> broadcastToRole(Role role, NotificationRequestDTO dto);
    void sendPromotionalNotification(String message, Long hotelId);
}
