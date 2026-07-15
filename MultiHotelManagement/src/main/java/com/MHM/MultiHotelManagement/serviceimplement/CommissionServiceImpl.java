package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.CommissionMapper;
import com.MHM.MultiHotelManagement.dto.request.CommissionRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CommissionResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Commission;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.entity.ExtraService;
import com.MHM.MultiHotelManagement.exception.AlreadyExistsException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.CommissionRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.repository.ExtraServiceRepository;
import com.MHM.MultiHotelManagement.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {

    private final CommissionRepository commissionRepo;
    private final BookingRepository bookingRepo;
    private final PaymentRepository paymentRepo;
    private final ExtraServiceRepository extraServiceRepo;

    private static final Double DEFAULT_RATE = 10.0;

    // ── Booking থেকে Commission তৈরি ─────────────────────────────
    @Override
    @Transactional
    public CommissionResponseDTO createFromBooking(CommissionRequestDTO dto) {
        Booking booking = bookingRepo.findByIdWithDetails(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + dto.getBookingId()));

        if (commissionRepo.existsByBooking_Id(dto.getBookingId())) {
            throw new AlreadyExistsException("Commission already exists for booking id: " + dto.getBookingId());
        }

        Double rate = dto.getCommissionRate() != null ? dto.getCommissionRate() : DEFAULT_RATE;
        BigDecimal totalPrice = booking.getTotalPrice();
        BigDecimal adminEarningsBd = totalPrice.multiply(BigDecimal.valueOf(rate)).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        BigDecimal ownerEarningsBd = totalPrice.subtract(adminEarningsBd);
        Double adminEarnings = adminEarningsBd.doubleValue();
        Double ownerEarnings = ownerEarningsBd.doubleValue();

        Commission commission = new Commission();
        commission.setBooking(booking);
        commission.setCommissionRate(rate);
        commission.setAdminEarnings(adminEarnings);
        commission.setHotelOwnerEarnings(ownerEarnings);

        Commission saved = commissionRepo.save(commission);
        return CommissionMapper.toDTO(commissionRepo.findByIdWithDetails(saved.getId()).orElse(saved));
    }

    // ── Commission update ─────────────────────────────────────────
    @Override
    @Transactional
    public CommissionResponseDTO updateCommission(Long id, CommissionRequestDTO dto) {
        Commission commission = commissionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + id));

        commission.setCommissionRate(dto.getCommissionRate() != null ? dto.getCommissionRate() : commission.getCommissionRate());
        commission.setAdminEarnings(dto.getAdminEarnings() != null ? dto.getAdminEarnings() : commission.getAdminEarnings());
        commission.setHotelOwnerEarnings(dto.getHotelOwnerEarnings() != null ? dto.getHotelOwnerEarnings() : commission.getHotelOwnerEarnings());

        Commission updated = commissionRepo.save(commission);
        return CommissionMapper.toDTO(updated);
    }

    // ── Commission delete ─────────────────────────────────────────
    @Override
    @Transactional
    public void deleteCommission(Long id) {
        Commission commission = commissionRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + id));
        commissionRepo.delete(commission);
    }

    // ── সব Commission ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<CommissionResponseDTO> getAll() {
        return commissionRepo.findAllWithDetails()
                .stream()
                .map(CommissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommissionResponseDTO> getAllWithDetails() {
        return commissionRepo.findAllWithDetails()
                .stream()
                .map(CommissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── ID দিয়ে খোঁজো ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommissionResponseDTO getById(Long id) {
        Commission commission = commissionRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found with id: " + id));
        return CommissionMapper.toDTO(commission);
    }

    // ── Booking ID দিয়ে খোঁজো ────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommissionResponseDTO getByBookingId(Long bookingId) {
        Commission commission = commissionRepo.findByBooking_Id(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found for booking id: " + bookingId));
        return CommissionMapper.toDTO(commission);
    }

    // ── Payment ID দিয়ে খোঁজো ────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommissionResponseDTO getByPaymentId(Long paymentId) {
        Commission commission = commissionRepo.findByPayment_Id(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found for payment id: " + paymentId));
        return CommissionMapper.toDTO(commission);
    }

    // ── ExtraService ID দিয়ে খোঁজো ───────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommissionResponseDTO getByExtraServiceId(Long extraServiceId) {
        Commission commission = commissionRepo.findByExtraService_Id(extraServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Commission not found for extra service id: " + extraServiceId));
        return CommissionMapper.toDTO(commission);
    }

    // ── Owner এর সব Commission ────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<CommissionResponseDTO> getByOwnerId(Long ownerId) {
        return commissionRepo.findByOwnerId(ownerId)
                .stream()
                .map(CommissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Hotel এর সব Commission ───────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<CommissionResponseDTO> getByHotelId(Long hotelId) {
        return commissionRepo.findByHotelId(hotelId)
                .stream()
                .map(CommissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Commission Rate দিয়ে খোঁজো ───────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<CommissionResponseDTO> getByCommissionRate(Double commissionRate) {
        return commissionRepo.findByCommissionRate(commissionRate)
                .stream()
                .map(CommissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Commission exists check ───────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public boolean existsByBookingId(Long bookingId) {
        return commissionRepo.existsByBooking_Id(bookingId);
    }

    // ── Admin এর মোট আয় ──────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Double getTotalAdminEarnings() {
        return commissionRepo.getTotalAdminEarnings();
    }

    // ── Owner এর মোট আয় ──────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Double getTotalOwnerEarnings(Long ownerId) {
        return commissionRepo.getTotalOwnerEarnings(ownerId);
    }

    // ── Date Range দিয়ে খোঁজো ────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<CommissionResponseDTO> getByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return commissionRepo.findByDateRange(startDate, endDate)
                .stream()
                .map(CommissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Monthly Report ────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<Object[]> getMonthlyReport(int year) {
        return commissionRepo.getMonthlyReport(year);
    }
}
