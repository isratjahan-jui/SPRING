package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.AuditTrail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditTrailRepository extends JpaRepository<AuditTrail, Long> {

    List<AuditTrail> findByEntityIdOrderByCreatedAtDesc(Long entityId);

    List<AuditTrail> findByEntityTypeOrderByCreatedAtDesc(String entityType);

    @Query("SELECT a FROM AuditTrail a WHERE a.entityType = :entityType AND a.entityId = :entityId ORDER BY a.createdAt DESC")
    List<AuditTrail> findByEntityTypeAndEntityId(@Param("entityType") String entityType, @Param("entityId") Long entityId);
}