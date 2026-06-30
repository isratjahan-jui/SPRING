package com.MHM.MultiHotelManagement.serviceImpl;

import com.MHM.MultiHotelManagement.dto.mapper.CommissionMapper;
import com.MHM.MultiHotelManagement.dto.request.CommissionRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CommissionResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Commission;
import com.MHM.MultiHotelManagement.exception.AlreadyExistsException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.CommissionRepository;
import com.MHM.MultiHotelManagement.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {

    private final CommissionRepository commissionRepo;
    private final BookingRepository bookingRepo;

    // Default commission rate — 10%
    private static final Double DEFAULT_RATE = 10.0;

    // ── Booking থেকে Commission তৈরি ─────────────────────────────
    @Override
    @Transactional
    public CommissionResponseDTO createFromBooking(
            CommissionRequestDTO dto
    ) {
        // Booking আছে কিনা check
        Booking booking = bookingRepo
                .findByIdWithDetails(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: "
                                + dto.getBookingId()
                ));

        // এই booking এর commission আগে আছে কিনা
        if (commissionRepo.existsByBooking_Id(dto.getBookingId())) {
            throw new AlreadyExistsException(
                    "Commission already exists for booking id: "
                            + dto.getBookingId()
            );
        }

        // Commission rate ঠিক করো
        Double rate = dto.getCommissionRate() != null
                ? dto.getCommissionRate()
                : DEFAULT_RATE;

        // Calculate করো
        Double totalPrice = booking.getTotalPrice();
        Double adminEarnings = totalPrice * rate / 100;
        Double ownerEarnings = totalPrice - adminEarnings;

        // Commission তৈরি করো
        Commission commission = new Commission();
        commission.setBooking(booking);
        commission.setCommissionRate(rate);
        commission.setAdminEarnings(adminEarnings);
        commission.setHotelOwnerEarnings(ownerEarnings);

        Commission saved = commissionRepo.save(commission);

        return CommissionMapper.toDTO(
                commissionRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
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

    // ── ID দিয়ে খোঁজো ─────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommissionResponseDTO getById(Long id) {
        Commission commission = commissionRepo
                .findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commission not found with id: " + id
                ));
        return CommissionMapper.toDTO(commission);
    }

    // ── Booking ID দিয়ে খোঁজো ────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CommissionResponseDTO getByBookingId(Long bookingId) {
        Commission commission = commissionRepo
                .findByBooking_Id(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Commission not found for booking id: "
                                + bookingId
                ));
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
    public List<CommissionResponseDTO> getByDateRange(
            LocalDateTime startDate,
            LocalDateTime endDate
    ) {
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