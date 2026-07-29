package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.SupportReply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportReplyRepository extends JpaRepository<SupportReply, Long> {

    @Query("""
        SELECT sr FROM SupportReply sr
        LEFT JOIN FETCH sr.replier r
        WHERE sr.ticket.id = :ticketId
        ORDER BY sr.createdAt ASC
    """)
    List<SupportReply> findByTicket_IdOrderByCreatedAtAsc(@Param("ticketId") Long ticketId);

    long countByTicket_Id(Long ticketId);
}
