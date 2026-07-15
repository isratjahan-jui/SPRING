package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Report;
import com.MHM.MultiHotelManagement.enums.ReportType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

    @Query("""
        SELECT r FROM Report r
        LEFT JOIN FETCH r.hotel h
        WHERE h.id = :hotelId
    """)
    List<Report> findByHotel_Id(@Param("hotelId") Long hotelId);

    @Query("""
        SELECT r FROM Report r
        LEFT JOIN FETCH r.hotel h
        WHERE r.type = :type
    """)
    List<Report> findByType(@Param("type") ReportType type);
}
