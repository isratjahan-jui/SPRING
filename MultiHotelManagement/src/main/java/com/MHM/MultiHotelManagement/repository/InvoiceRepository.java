package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByCustomer_Id(Long customerId);
    List<Invoice> findByBooking_Id(Long bookingId);
    boolean existsByBooking_IdAndPayment_Id(Long bookingId, Long paymentId);

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.booking LEFT JOIN FETCH i.payment LEFT JOIN FETCH i.customer WHERE i.customer.id = :customerId ORDER BY i.issuedAt DESC")
    List<Invoice> findByCustomerIdWithDetails(@Param("customerId") Long customerId);
}

