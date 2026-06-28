package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.CustomerSupport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerSupportRepository extends JpaRepository<CustomerSupport, Long>  {
    List<CustomerSupport> findByCustomer_Id(Long customerId);
    List<CustomerSupport> findByStatus(String status);

}
