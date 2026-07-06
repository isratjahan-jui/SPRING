package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.CustomerSupport;
import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketPriority;
import com.MHM.MultiHotelManagement.enums.CustomerSupportTicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerSupportRepository extends JpaRepository<CustomerSupport, Long> {

    @Query("""
        SELECT cs FROM CustomerSupport cs
        LEFT JOIN FETCH cs.customer c
        LEFT JOIN FETCH cs.agent a
        WHERE c.id = :customerId
    """)
    List<CustomerSupport> findByCustomer_Id(@Param("customerId") Long customerId);

    @Query("""
        SELECT cs FROM CustomerSupport cs
        LEFT JOIN FETCH cs.customer c
        LEFT JOIN FETCH cs.agent a
        WHERE a.id = :agentId
    """)
    List<CustomerSupport> findByAgent_Id(@Param("agentId") Long agentId);

    List<CustomerSupport> findByStatus(CustomerSupportTicketStatus status);

    List<CustomerSupport> findByPriority(CustomerSupportTicketPriority priority);
}
