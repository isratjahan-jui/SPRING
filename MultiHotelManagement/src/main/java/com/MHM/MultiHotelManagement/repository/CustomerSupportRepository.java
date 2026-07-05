package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.CustomerSupport;
import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketPriority;
import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerSupportRepository extends JpaRepository<CustomerSupport, Long>  {
    List<CustomerSupport> findByCustomer_Id(Long customerId);
    List<CustomerSupport> findByAgent_Id(Long agentId);
    List<CustomerSupport> findByStatus(CustomerSupportTicketStatus status);
    List<CustomerSupport> findByPriority(CustomerSupportTicketPriority priority);
}
