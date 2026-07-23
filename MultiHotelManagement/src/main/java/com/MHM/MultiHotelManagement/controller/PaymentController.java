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
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin("*")
@Slf4j
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
        try {
            return ResponseEntity.ok(sslCommerzService.initiatePayment(bookingId));
        } catch (Exception e) {
            Map<String, Object> error = new java.util.HashMap<>();
            error.put("error", true);
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @RequestMapping(value = "/sslcommerz/success", method = {RequestMethod.POST, RequestMethod.GET})
    public void sslCommerzSuccess(jakarta.servlet.http.HttpServletRequest request,
                                  jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        Map<String, String> params = extractParams(request);
        String tranId = params.getOrDefault("tran_id", "");
        try {
            sslCommerzService.handleSuccess(params);
        } catch (Exception e) {
            log.error("SSLCommerz success handler error: {}", e.getMessage());
        }
        response.sendRedirect("http://localhost:4200/customer/payment-result?status=success&tran_id=" + tranId);
    }

    @RequestMapping(value = "/sslcommerz/fail", method = {RequestMethod.POST, RequestMethod.GET})
    public void sslCommerzFail(jakarta.servlet.http.HttpServletRequest request,
                               jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        Map<String, String> params = extractParams(request);
        String tranId = params.getOrDefault("tran_id", "");
        try {
            sslCommerzService.handleFail(params);
        } catch (Exception e) {
            log.error("SSLCommerz fail handler error: {}", e.getMessage());
        }
        response.sendRedirect("http://localhost:4200/customer/payment-result?status=fail&tran_id=" + tranId);
    }

    @RequestMapping(value = "/sslcommerz/cancel", method = {RequestMethod.POST, RequestMethod.GET})
    public void sslCommerzCancel(jakarta.servlet.http.HttpServletRequest request,
                                 jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        Map<String, String> params = extractParams(request);
        String tranId = params.getOrDefault("tran_id", "");
        try {
            sslCommerzService.handleCancel(params);
        } catch (Exception e) {
            log.error("SSLCommerz cancel handler error: {}", e.getMessage());
        }
        response.sendRedirect("http://localhost:4200/customer/payment-result?status=cancel&tran_id=" + tranId);
    }

    @RequestMapping(value = "/sslcommerz/ipn", method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseEntity<String> sslCommerzIpn(jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> params = extractParams(request);
        sslCommerzService.handleIpn(params);
        return ResponseEntity.ok("OK");
    }

    private Map<String, String> extractParams(jakarta.servlet.http.HttpServletRequest request) {
        Map<String, String> params = new java.util.HashMap<>();
        java.util.Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            params.put(name, request.getParameter(name));
        }
        return params;
    }
}
