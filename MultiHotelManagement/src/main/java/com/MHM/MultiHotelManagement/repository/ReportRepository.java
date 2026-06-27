package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
}
