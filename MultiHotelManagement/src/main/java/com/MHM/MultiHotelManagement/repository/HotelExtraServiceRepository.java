package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.HotelExtraService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HotelExtraServiceRepository extends JpaRepository<HotelExtraService, Long> {
    List<HotelExtraService> findByHotel_Id(Long hotelId);
    List<HotelExtraService> findByHotel_IdAndIsActiveTrue(Long hotelId);
}
