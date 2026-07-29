package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Invoice;
import com.MHM.MultiHotelManagement.enums.InvoiceType;
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

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.booking LEFT JOIN FETCH i.payment LEFT JOIN FETCH i.customer ORDER BY i.issuedAt DESC")
    List<Invoice> findAllWithDetails();

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.booking LEFT JOIN FETCH i.payment LEFT JOIN FETCH i.customer WHERE i.booking.id = :bookingId")
    List<Invoice> findByBookingIdWithDetails(@Param("bookingId") Long bookingId);

    boolean existsByBooking_Id(Long bookingId);

    boolean existsByBooking_IdAndInvoiceType(Long bookingId, InvoiceType invoiceType);

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.booking LEFT JOIN FETCH i.payment LEFT JOIN FETCH i.customer WHERE i.booking.hotel.id = :hotelId ORDER BY i.issuedAt DESC")
    List<Invoice> findByHotelIdWithDetails(@Param("hotelId") Long hotelId);

    @Query("SELECT i FROM Invoice i LEFT JOIN FETCH i.booking LEFT JOIN FETCH i.payment LEFT JOIN FETCH i.customer WHERE i.booking.hotel.owner.id = :ownerId ORDER BY i.issuedAt DESC")
    List<Invoice> findByOwnerIdWithDetails(@Param("ownerId") Long ownerId);
}

