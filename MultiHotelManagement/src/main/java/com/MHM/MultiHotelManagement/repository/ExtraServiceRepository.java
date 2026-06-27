package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.ExtraService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExtraServiceRepository extends JpaRepository<ExtraService, Long> {
}
