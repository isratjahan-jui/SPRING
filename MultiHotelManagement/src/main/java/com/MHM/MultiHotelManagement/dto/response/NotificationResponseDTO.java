package com.MHM.MultiHotelManagement.dto.response;

import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponseDTO {
    private Long id;
    private String message;
    private NotificationType type;
    private NotificationChannel channel;
    private Boolean readStatus;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
