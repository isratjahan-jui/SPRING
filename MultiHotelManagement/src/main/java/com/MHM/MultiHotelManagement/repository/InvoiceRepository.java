package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    Optional<Invoice> findByBooking_Id(Long bookingId);
    Optional<Invoice> findByInvoiceNumber(String number);
}

