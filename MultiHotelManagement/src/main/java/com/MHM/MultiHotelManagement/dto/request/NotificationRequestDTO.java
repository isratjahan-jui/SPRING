package com.MHM.MultiHotelManagement.dto.request;

import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import lombok.Data;

@Data
public class NotificationRequestDTO {
    private String message;
    private NotificationType type;
    private NotificationChannel channel;
    private Long userId;
}
