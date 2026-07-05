package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Notification;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // User ভিত্তিক সব notification
    List<Notification> findByUser_Id(Long userId);

    // User ভিত্তিক unread/read notification
    List<Notification> findByUser_IdAndReadStatus(Long userId, Boolean readStatus);

    // সব unread notification
    List<Notification> findByReadStatusFalse();

    // Type ভিত্তিক notification (Enum ব্যবহার করা হলো)
    List<Notification> findByType(NotificationType type);

    // Channel ভিত্তিক notification (Enum ব্যবহার করা হলো)
    List<Notification> findByChannel(NotificationChannel channel);



}