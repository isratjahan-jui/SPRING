package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.CommissionRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CommissionResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface CommissionService {

    // Booking confirm হলে commission তৈরি হবে
    CommissionResponseDTO createFromBooking(CommissionRequestDTO dto);

    // Commission update
    CommissionResponseDTO updateCommission(Long id, CommissionRequestDTO dto);

    // Commission delete
    void deleteCommission(Long id);

    // সব Commission
    List<CommissionResponseDTO> getAll();

    // সব Commission details সহ
    List<CommissionResponseDTO> getAllWithDetails();

    // ID দিয়ে খোঁজো
    CommissionResponseDTO getById(Long id);

    // Booking ID দিয়ে খোঁজো
    CommissionResponseDTO getByBookingId(Long bookingId);

    // Payment ID দিয়ে খোঁজো
    CommissionResponseDTO getByPaymentId(Long paymentId);

    // ExtraService ID দিয়ে খোঁজো
    CommissionResponseDTO getByExtraServiceId(Long extraServiceId);

    // Owner এর সব Commission
    List<CommissionResponseDTO> getByOwnerId(Long ownerId);

    // Hotel এর সব Commission
    List<CommissionResponseDTO> getByHotelId(Long hotelId);

    // Commission Rate দিয়ে খোঁজো
    List<CommissionResponseDTO> getByCommissionRate(Double commissionRate);

    // Commission exists check
    boolean existsByBookingId(Long bookingId);

    // Admin এর মোট আয়
    Double getTotalAdminEarnings();

    // Owner এর মোট আয়
    Double getTotalOwnerEarnings(Long ownerId);

    // Date range দিয়ে খোঁজো
    List<CommissionResponseDTO> getByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    // Monthly report
    List<Object[]> getMonthlyReport(int year);
}
