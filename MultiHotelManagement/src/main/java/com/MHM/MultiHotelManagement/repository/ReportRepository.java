package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Report;
import com.MHM.MultiHotelManagement.enums.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByHotel_Id(Long hotelId);
    List<Report> findByType(ReportType type);
}
