package com.emranhss.HotelManagement.repository;

import com.emranhss.HotelManagement.entity.PoliceStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PoliceStationRepsitory extends JpaRepository<PoliceStation, Long> {



}
