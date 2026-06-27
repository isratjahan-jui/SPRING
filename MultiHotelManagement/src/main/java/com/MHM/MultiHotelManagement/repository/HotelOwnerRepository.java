package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.HotelOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HotelOwnerRepository extends JpaRepository<HotelOwner, Long> {
}
