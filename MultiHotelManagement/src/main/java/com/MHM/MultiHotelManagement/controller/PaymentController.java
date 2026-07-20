package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.PaymentRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.PaymentResponseDTO;
import com.MHM.MultiHotelManagement.service.PaymentService;
import com.MHM.MultiHotelManagement.service.SslCommerzService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final SslCommerzService sslCommerzService;

    public PaymentController(PaymentService paymentService, SslCommerzService sslCommerzService) {
        this.paymentService = paymentService;
        this.sslCommerzService = sslCommerzService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<PaymentResponseDTO> createPayment(@Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.createPayment(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDTO> updatePayment(@PathVariable Long id,
                                                            @Valid @RequestBody PaymentRequestDTO dto) {
        return ResponseEntity.ok(paymentService.updatePayment(id, dto));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PaymentResponseDTO>> getAll() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<PaymentResponseDTO> getPaymentById(@PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getPaymentById(id));
    }

    @GetMapping("/booking/{bookingId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'HOTEL_OWNER')")
    public ResponseEntity<PaymentResponseDTO> getPaymentByBooking(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentByBooking(bookingId));
    }

    @GetMapping("/customer/{customerId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER')")
    public ResponseEntity<List<PaymentResponseDTO>> getByCustomer(@PathVariable Long customerId) {
        return ResponseEntity.ok(paymentService.getPaymentsByCustomer(customerId));
    }

    @PostMapping("/{bookingId}/refund")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PaymentResponseDTO> refund(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.processRefund(bookingId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    // ── SSLCommerz Endpoints ───────────────────────────────────────

    @PostMapping("/sslcommerz/init/{bookingId}")
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<Map<String, Object>> initiateSslCommerz(@PathVariable Long bookingId) {
        return ResponseEntity.ok(sslCommerzService.initiatePayment(bookingId));
    }

    @PostMapping("/sslcommerz/success")
    public ResponseEntity<Void> sslCommerzSuccess(@RequestParam Map<String, String> params,
                                                   jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        sslCommerzService.handleSuccess(params);
        String tranId = params.getOrDefault("tran_id", "");
        response.sendRedirect("http://localhost:4200/customer/payment-result?status=success&tran_id=" + tranId);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @PostMapping("/sslcommerz/fail")
    public ResponseEntity<Void> sslCommerzFail(@RequestParam Map<String, String> params,
                                                jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        sslCommerzService.handleFail(params);
        String tranId = params.getOrDefault("tran_id", "");
        response.sendRedirect("http://localhost:4200/customer/payment-result?status=fail&tran_id=" + tranId);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @PostMapping("/sslcommerz/cancel")
    public ResponseEntity<Void> sslCommerzCancel(@RequestParam Map<String, String> params,
                                                  jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        sslCommerzService.handleCancel(params);
        String tranId = params.getOrDefault("tran_id", "");
        response.sendRedirect("http://localhost:4200/customer/payment-result?status=cancel&tran_id=" + tranId);
        return ResponseEntity.status(HttpStatus.FOUND).build();
    }

    @PostMapping("/sslcommerz/ipn")
    public ResponseEntity<String> sslCommerzIpn(@RequestParam Map<String, String> params) {
        sslCommerzService.handleIpn(params);
        return ResponseEntity.ok("OK");
    }
}
