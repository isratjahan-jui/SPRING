package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByCustomer_Id(Long customerId);
    List<Invoice> findByBooking_Id(Long bookingId);
}

