package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.response.ReceiptResponseDTO;
import com.MHM.MultiHotelManagement.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/receipts")
@RequiredArgsConstructor
public class ReceiptController {

    private final ReceiptService receiptService;

    @PostMapping("/generate/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<ReceiptResponseDTO> generateReceipt(@PathVariable Long paymentId) {
        return ResponseEntity.ok(receiptService.generateReceipt(paymentId));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<ReceiptResponseDTO> getReceiptById(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.getReceiptById(id));
    }

    @GetMapping("/number/{receiptNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<ReceiptResponseDTO> getReceiptByNumber(@PathVariable String receiptNumber) {
        return ResponseEntity.ok(receiptService.getReceiptByNumber(receiptNumber));
    }

    @GetMapping("/payment/{paymentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<ReceiptResponseDTO> getReceiptByPaymentId(@PathVariable Long paymentId) {
        return ResponseEntity.ok(receiptService.getReceiptByPaymentId(paymentId));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<ReceiptResponseDTO>> getReceiptsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(receiptService.getReceiptsByCustomerId(customerId));
    }

    @GetMapping("/hotel/{hotelId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_OWNER')")
    public ResponseEntity<List<ReceiptResponseDTO>> getReceiptsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(receiptService.getReceiptsByHotelId(hotelId));
    }

    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_OWNER')")
    public ResponseEntity<List<ReceiptResponseDTO>> getReceiptsByOwnerId(@PathVariable Long ownerId) {
        return ResponseEntity.ok(receiptService.getReceiptsByOwnerId(ownerId));
    }

    @GetMapping("/hotel/{hotelId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_OWNER')")
    public ResponseEntity<Long> countReceiptsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(receiptService.countReceiptsByHotelId(hotelId));
    }

    @GetMapping("/hotel/{hotelId}/total")
    @PreAuthorize("hasAnyRole('ADMIN', 'HOTEL_OWNER')")
    public ResponseEntity<java.math.BigDecimal> sumReceiptsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(receiptService.sumReceiptsByHotelId(hotelId));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReceiptResponseDTO>> getAllReceipts() {
        return ResponseEntity.ok(receiptService.getAllReceipts());
    }
}
