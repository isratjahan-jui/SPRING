package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.CommissionRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CommissionResponseDTO;
import com.MHM.MultiHotelManagement.service.CommissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/commissions")
@RequiredArgsConstructor

public class CommissionController {

    private final CommissionService commissionService;

    // ── Create ───────────────────────────────────────────────────
    // POST /api/commissions
    @PostMapping
    public ResponseEntity<CommissionResponseDTO> create(
            @RequestBody CommissionRequestDTO dto
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(commissionService.createFromBooking(dto));
    }

    // ── Get All ──────────────────────────────────────────────────
    // GET /api/commissions
    @GetMapping
    public ResponseEntity<List<CommissionResponseDTO>> getAll() {
        return ResponseEntity.ok(commissionService.getAll());
    }

    // ── Get by ID ────────────────────────────────────────────────
    // GET /api/commissions/1
    @GetMapping("/{id}")
    public ResponseEntity<CommissionResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(commissionService.getById(id));
    }

    // ── Get by Booking ID ────────────────────────────────────────
    // GET /api/commissions/booking/1
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<CommissionResponseDTO> getByBookingId(
            @PathVariable Long bookingId
    ) {
        return ResponseEntity.ok(
                commissionService.getByBookingId(bookingId)
        );
    }

    // ── Get by Owner ID ──────────────────────────────────────────
    // GET /api/commissions/owner/1
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<CommissionResponseDTO>> getByOwner(
            @PathVariable Long ownerId
    ) {
        return ResponseEntity.ok(
                commissionService.getByOwnerId(ownerId)
        );
    }

    // ── Get by Hotel ID ──────────────────────────────────────────
    // GET /api/commissions/hotel/1
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<CommissionResponseDTO>> getByHotel(
            @PathVariable Long hotelId
    ) {
        return ResponseEntity.ok(
                commissionService.getByHotelId(hotelId)
        );
    }

    // ── Admin Total Earnings ─────────────────────────────────────
    // GET /api/commissions/admin/total
    @GetMapping("/admin/total")
    public ResponseEntity<Double> getAdminTotal() {
        return ResponseEntity.ok(
                commissionService.getTotalAdminEarnings()
        );
    }

    // ── Owner Total Earnings ─────────────────────────────────────
    // GET /api/commissions/owner/1/total
    @GetMapping("/owner/{ownerId}/total")
    public ResponseEntity<Double> getOwnerTotal(
            @PathVariable Long ownerId
    ) {
        return ResponseEntity.ok(
                commissionService.getTotalOwnerEarnings(ownerId)
        );
    }

    // ── Date Range ───────────────────────────────────────────────
    // GET /api/commissions/range?start=...&end=...
    @GetMapping("/range")
    public ResponseEntity<List<CommissionResponseDTO>> getByRange(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end
    ) {
        return ResponseEntity.ok(
                commissionService.getByDateRange(start, end)
        );
    }

    // ── Monthly Report ───────────────────────────────────────────
    // GET /api/commissions/report/monthly?year=2025
    @GetMapping("/report/monthly")
    public ResponseEntity<List<Object[]>> getMonthlyReport(
            @RequestParam int year
    ) {
        return ResponseEntity.ok(
                commissionService.getMonthlyReport(year)
        );
    }
}