package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Receipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReceiptRepository extends JpaRepository<Receipt, Long> {

    Optional<Receipt> findByReceiptNumber(String receiptNumber);

    Optional<Receipt> findByPaymentId(Long paymentId);

    @Query("SELECT r FROM Receipt r LEFT JOIN FETCH r.payment LEFT JOIN FETCH r.invoice LEFT JOIN FETCH r.booking LEFT JOIN FETCH r.customer WHERE r.customer.id = :customerId ORDER BY r.issuedAt DESC")
    List<Receipt> findByCustomerIdWithDetails(@Param("customerId") Long customerId);

    @Query("SELECT r FROM Receipt r LEFT JOIN FETCH r.payment LEFT JOIN FETCH r.invoice LEFT JOIN FETCH r.booking LEFT JOIN FETCH r.customer WHERE r.booking.hotel.id = :hotelId ORDER BY r.issuedAt DESC")
    List<Receipt> findByHotelIdWithDetails(@Param("hotelId") Long hotelId);

    @Query("SELECT r FROM Receipt r LEFT JOIN FETCH r.payment LEFT JOIN FETCH r.invoice LEFT JOIN FETCH r.booking LEFT JOIN FETCH r.customer WHERE r.booking.hotel.owner.id = :ownerId ORDER BY r.issuedAt DESC")
    List<Receipt> findByOwnerIdWithDetails(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(r) FROM Receipt r WHERE r.booking.hotel.id = :hotelId")
    long countByHotelId(@Param("hotelId") Long hotelId);

    @Query("SELECT COALESCE(SUM(r.totalAmount), 0) FROM Receipt r WHERE r.booking.hotel.id = :hotelId")
    BigDecimal sumTotalAmountByHotelId(@Param("hotelId") Long hotelId);
}
