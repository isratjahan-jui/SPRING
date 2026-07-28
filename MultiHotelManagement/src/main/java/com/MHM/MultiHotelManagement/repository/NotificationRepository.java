package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Notification;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import com.MHM.MultiHotelManagement.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    @Query("""
        SELECT n FROM Notification n
        LEFT JOIN FETCH n.user u
        WHERE u.id = :userId
    """)
    List<Notification> findByUser_Id(@Param("userId") Long userId);

    @Query("""
        SELECT n FROM Notification n
        LEFT JOIN FETCH n.user u
        WHERE u.id = :userId AND n.readStatus = :readStatus
    """)
    List<Notification> findByUser_IdAndReadStatus(
            @Param("userId") Long userId,
            @Param("readStatus") Boolean readStatus
    );

    @Query("""
        SELECT n FROM Notification n
        LEFT JOIN FETCH n.user u
        WHERE u.id = :userId AND u.role = :role
    """)
    List<Notification> findByUser_IdAndUser_Role(
            @Param("userId") Long userId,
            @Param("role") Role role
    );

    @Query("""
        SELECT n FROM Notification n
        LEFT JOIN FETCH n.user u
        WHERE u.id = :userId AND u.role = :role AND n.readStatus = :readStatus
    """)
    List<Notification> findByUser_IdAndUser_RoleAndReadStatus(
            @Param("userId") Long userId,
            @Param("role") Role role,
            @Param("readStatus") Boolean readStatus
    );

    @Query("""
        SELECT n FROM Notification n
        LEFT JOIN FETCH n.user u
        WHERE u.role = :role AND n.type = :type
    """)
    List<Notification> findByUser_RoleAndType(
            @Param("role") Role role,
            @Param("type") NotificationType type
    );

    List<Notification> findByReadStatusFalse();

    List<Notification> findByType(NotificationType type);

    List<Notification> findByChannel(NotificationChannel channel);
}