package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.NotificationResponseDTO;
import com.MHM.MultiHotelManagement.enums.Role;
import com.MHM.MultiHotelManagement.service.NotificationService;
import com.MHM.MultiHotelManagement.service.SseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final SseService sseService;

    @PostMapping
    public ResponseEntity<NotificationResponseDTO> create(@RequestBody NotificationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(notificationService.createNotification(dto));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getNotificationsByUser(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnread(@PathVariable Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadNotifications(userId));
    }

    @GetMapping("/user/{userId}/role/{role}")
    public ResponseEntity<List<NotificationResponseDTO>> getByUserAndRole(
            @PathVariable Long userId,
            @PathVariable Role role) {
        return ResponseEntity.ok(notificationService.getNotificationsByUserAndRole(userId, role));
    }

    @GetMapping("/user/{userId}/role/{role}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadByUserAndRole(
            @PathVariable Long userId,
            @PathVariable Role role) {
        return ResponseEntity.ok(notificationService.getUnreadByUserAndRole(userId, role));
    }

    @PostMapping("/broadcast")
    public ResponseEntity<List<NotificationResponseDTO>> broadcast(
            @RequestParam Role role,
            @RequestBody NotificationRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(notificationService.broadcastToRole(role, dto));
    }

    @PostMapping("/promotional")
    public ResponseEntity<Void> sendPromotional(@RequestBody NotificationRequestDTO dto) {
        notificationService.sendPromotionalNotification(dto.getMessage(), null);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping(value = "/subscribe/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable Long userId) {
        return sseService.subscribe(userId);
    }
}
