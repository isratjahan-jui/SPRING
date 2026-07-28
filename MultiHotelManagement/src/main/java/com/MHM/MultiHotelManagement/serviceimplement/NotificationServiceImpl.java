package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.NotificationMapper;
import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.NotificationResponseDTO;
import com.MHM.MultiHotelManagement.entity.Notification;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import com.MHM.MultiHotelManagement.enums.Role;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.NotificationRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SseService sseService;

    @Override
    @Transactional
    public NotificationResponseDTO createNotification(NotificationRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Notification notification = new Notification();
        notification.setMessage(dto.getMessage());
        notification.setType(dto.getType());
        notification.setChannel(dto.getChannel());
        notification.setUser(user);
        notification.setReadStatus(false);

        Notification saved = notificationRepository.save(notification);
        NotificationResponseDTO response = NotificationMapper.toDTO(saved);
        sseService.sendNotification(dto.getUserId(), response);
        return response;
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setReadStatus(true);
        notificationRepository.save(notification);
    }

    @Override
    @Transactional
    public void deleteNotification(Long id) {
        notificationRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUser_Id(userId)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUser_IdAndReadStatus(userId, false)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByUserAndRole(Long userId, Role role) {
        return notificationRepository.findByUser_IdAndUser_Role(userId, role)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadByUserAndRole(Long userId, Role role) {
        return notificationRepository.findByUser_IdAndUser_RoleAndReadStatus(userId, role, false)
                .stream()
                .map(NotificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<NotificationResponseDTO> broadcastToRole(Role role, NotificationRequestDTO dto) {
        List<User> users = userRepository.findByRole(role);
        List<NotificationResponseDTO> responses = new ArrayList<>();

        for (User user : users) {
            Notification notification = new Notification();
            notification.setMessage(dto.getMessage());
            notification.setType(dto.getType());
            notification.setChannel(dto.getChannel());
            notification.setUser(user);
            notification.setReadStatus(false);

            Notification saved = notificationRepository.save(notification);
            NotificationResponseDTO response = NotificationMapper.toDTO(saved);
            sseService.sendNotification(user.getId(), response);
            responses.add(response);
        }

        return responses;
    }

    @Override
    @Transactional
    public void sendPromotionalNotification(String message, Long hotelId) {
        List<User> customers = userRepository.findByRole(Role.CUSTOMER);

        for (User customer : customers) {
            Notification notification = new Notification();
            notification.setMessage(message);
            notification.setType(NotificationType.PROMOTIONAL);
            notification.setChannel(NotificationChannel.WEB);
            notification.setUser(customer);
            notification.setReadStatus(false);

            Notification saved = notificationRepository.save(notification);
            NotificationResponseDTO response = NotificationMapper.toDTO(saved);
            sseService.sendNotification(customer.getId(), response);
        }
    }
}
