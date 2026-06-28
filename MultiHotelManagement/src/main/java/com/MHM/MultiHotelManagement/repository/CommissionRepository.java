package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {

    List<Commission> findByOwner_Id(Long ownerId);

}
