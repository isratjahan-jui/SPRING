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
    @PostMapping
    public ResponseEntity<CommissionResponseDTO> create(@RequestBody CommissionRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commissionService.createFromBooking(dto));
    }

    // ── Update ───────────────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<CommissionResponseDTO> update(@PathVariable Long id, @RequestBody CommissionRequestDTO dto) {
        return ResponseEntity.ok(commissionService.updateCommission(id, dto));
    }

    // ── Delete ───────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        commissionService.deleteCommission(id);
        return ResponseEntity.noContent().build();
    }

    // ── Get All ──────────────────────────────────────────────────
    @GetMapping
    public ResponseEntity<List<CommissionResponseDTO>> getAll() {
        return ResponseEntity.ok(commissionService.getAll());
    }

    // ── Get by ID ────────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<CommissionResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(commissionService.getById(id));
    }

    // ── Get by Booking ID ────────────────────────────────────────
    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<CommissionResponseDTO> getByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(commissionService.getByBookingId(bookingId));
    }

    // ── Exists check by Booking ID ───────────────────────────────
    @GetMapping("/booking/{bookingId}/exists")
    public ResponseEntity<Boolean> existsByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(commissionService.existsByBookingId(bookingId));
    }

    // ── Get by Payment ID ────────────────────────────────────────
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<CommissionResponseDTO> getByPaymentId(@PathVariable Long paymentId) {
        return ResponseEntity.ok(commissionService.getByPaymentId(paymentId));
    }

    // ── Get by ExtraService ID ───────────────────────────────────
    @GetMapping("/extra/{extraServiceId}")
    public ResponseEntity<CommissionResponseDTO> getByExtraServiceId(@PathVariable Long extraServiceId) {
        return ResponseEntity.ok(commissionService.getByExtraServiceId(extraServiceId));
    }

    // ── Get by Owner ID ──────────────────────────────────────────
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<List<CommissionResponseDTO>> getByOwner(@PathVariable Long ownerId) {
        return ResponseEntity.ok(commissionService.getByOwnerId(ownerId));
    }

    // ── Get by Hotel ID ──────────────────────────────────────────
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<CommissionResponseDTO>> getByHotel(@PathVariable Long hotelId) {
        return ResponseEntity.ok(commissionService.getByHotelId(hotelId));
    }

    // ── Get by Commission Rate ───────────────────────────────────
    @GetMapping("/rate/{rate}")
    public ResponseEntity<List<CommissionResponseDTO>> getByRate(@PathVariable Double rate) {
        return ResponseEntity.ok(commissionService.getByCommissionRate(rate));
    }

    // ── Admin Total Earnings ─────────────────────────────────────
    @GetMapping("/admin/total")
    public ResponseEntity<Double> getAdminTotal() {
        return ResponseEntity.ok(commissionService.getTotalAdminEarnings());
    }

    // ── Owner Total Earnings ─────────────────────────────────────
    @GetMapping("/owner/{ownerId}/total")
    public ResponseEntity<Double> getOwnerTotal(@PathVariable Long ownerId) {
        return ResponseEntity.ok(commissionService.getTotalOwnerEarnings(ownerId));
    }

    // ── Date Range ───────────────────────────────────────────────
    @GetMapping("/range")
    public ResponseEntity<List<CommissionResponseDTO>> getByRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(commissionService.getByDateRange(start, end));
    }

    // ── Monthly Report ───────────────────────────────────────────
    @GetMapping("/report/monthly")
    public ResponseEntity<List<Object[]>> getMonthlyReport(@RequestParam int year) {
        return ResponseEntity.ok(commissionService.getMonthlyReport(year));
    }
}
