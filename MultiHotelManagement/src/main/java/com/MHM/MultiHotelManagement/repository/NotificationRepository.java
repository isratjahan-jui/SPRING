package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUser_Id(Long userId);
    List<Notification> findByUser_IdAndIsRead(
            Long userId, Boolean isRead
    );
}
