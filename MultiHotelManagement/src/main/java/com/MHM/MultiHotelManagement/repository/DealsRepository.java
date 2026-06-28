package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Deals;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DealsRepository extends JpaRepository<Deals, Long> {
    List<Deals> findByHotel_Id(Long hotelId);
    List<Deals> findByIsActive(Boolean isActive);
}
