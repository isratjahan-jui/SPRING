package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Deals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DealsRepository extends JpaRepository<Deals, Long> {

    List<Deals> findByIsActive(Boolean isActive);

    @Query("""
        SELECT d FROM Deals d
        LEFT JOIN FETCH d.hotel h
        LEFT JOIN FETCH d.room r
        WHERE h.id = :hotelId
    """)
    List<Deals> findByHotel_Id(@Param("hotelId") Long hotelId);

    @Query("""
        SELECT d FROM Deals d
        LEFT JOIN FETCH d.hotel h
        LEFT JOIN FETCH d.room r
        WHERE r.id = :roomId
    """)
    List<Deals> findByRoom_Id(@Param("roomId") Long roomId);

    @Query("""
        SELECT d FROM Deals d
        LEFT JOIN FETCH d.hotel h
        LEFT JOIN FETCH d.room r
        WHERE h.id = :hotelId
        AND d.startDate <= :currentDate
        AND d.endDate >= :currentDate
    """)
    List<Deals> findByHotel_IdAndStartDateBeforeAndEndDateAfter(
            @Param("hotelId") Long hotelId,
            @Param("currentDate") LocalDateTime currentDate1,
            @Param("currentDate2") LocalDateTime currentDate2
    );

    @Query("""
        SELECT d FROM Deals d
        LEFT JOIN FETCH d.hotel h
        LEFT JOIN FETCH d.room r
        WHERE LOWER(d.dealTitle) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Deals> findByDealTitleContainingIgnoreCase(@Param("keyword") String keyword);
}