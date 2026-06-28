package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.HotelOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HotelOwnerRepository extends JpaRepository<HotelOwner, Long> {
    Optional<HotelOwner> findByUser_Id(Long userId);
    Boolean existsByUser_Id(Long userId);
}
