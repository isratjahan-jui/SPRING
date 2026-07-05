package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Deals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DealsRepository extends JpaRepository<Deals, Long> {

    // যদি Deals entity তে isActive field থাকে
    List<Deals> findByIsActive(Boolean isActive);

    // Hotel ভিত্তিক সব deals
    List<Deals> findByHotel_Id(Long hotelId);

    // Room ভিত্তিক deals
    List<Deals> findByRoom_Id(Long roomId);

    // Active deals (startDate <= now && endDate >= now)
    List<Deals> findByHotel_IdAndStartDateBeforeAndEndDateAfter(
            Long hotelId,
            LocalDateTime currentDate1,
            LocalDateTime currentDate2
    );

    // Deal title দিয়ে search
    List<Deals> findByDealTitleContainingIgnoreCase(String keyword);
}