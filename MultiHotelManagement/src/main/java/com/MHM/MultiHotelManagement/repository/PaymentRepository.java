package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByBooking_Id(Long bookingId);
    List<Payment> findByStatus(PaymentStatus status);
    List<Payment> findByCustomerId(Long customerId);
    Optional<Payment> findByTransactionId(String transactionId);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.booking b LEFT JOIN FETCH b.customer c LEFT JOIN FETCH c.user WHERE c.id = :customerId")
    List<Payment> findByCustomerIdWithDetails(@Param("customerId") Long customerId);

    @Query("SELECT p FROM Payment p LEFT JOIN FETCH p.booking b LEFT JOIN FETCH b.customer c LEFT JOIN FETCH c.user")
    List<Payment> findAllWithDetails();
}
