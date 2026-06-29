package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
}
