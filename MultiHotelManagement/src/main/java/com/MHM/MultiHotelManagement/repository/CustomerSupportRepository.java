package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.CustomerSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerSupportRepository extends JpaRepository<CustomerSupport, Long>  {


}
