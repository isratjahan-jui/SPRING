package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Invoice;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.InvoiceStatus;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.InvoiceRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.service.AuditTrailService;
import com.MHM.MultiHotelManagement.service.ReceiptService;
import com.MHM.MultiHotelManagement.service.SslCommerzService;
import com.MHM.MultiHotelManagement.util.SslCommerzClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class SslCommerzServiceImpl implements SslCommerzService {

    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final SslCommerzClient sslCommerzClient;
    private final InvoiceRepository invoiceRepository;
    private final ReceiptService receiptService;
    private final AuditTrailService auditTrailService;

    @Override
    @Transactional
    public Map<String, Object> initiatePayment(Long bookingId) {
        Booking booking = bookingRepository.findByIdWithDetails(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getDueAmount() == null || booking.getDueAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("No amount due for this booking");
        }

        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();

        Payment payment = paymentRepository.findByBooking_Id(bookingId).orElse(null);

        if (payment != null) {
            if (payment.getStatus() == PaymentStatus.PAID) {
                throw new BadRequestException("Payment already completed for this booking");
            }
            payment.setAmount(booking.getDueAmount());
            payment.setMethod("SSLCOMMERZ");
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionId(transactionId);
            payment.setTransactionDate(LocalDateTime.now());
        } else {
            payment = new Payment();
            payment.setBooking(booking);
            payment.setAmount(booking.getDueAmount());
            payment.setCustomerId(booking.getCustomer() != null ? booking.getCustomer().getId() : null);
            payment.setMethod("SSLCOMMERZ");
            payment.setStatus(PaymentStatus.PENDING);
            payment.setTransactionId(transactionId);
            payment.setTransactionDate(LocalDateTime.now());
        }
        payment = paymentRepository.save(payment);

        String customerName = booking.getCustomer() != null ? booking.getCustomer().getCustomerName() : "Guest";
        String customerEmail = (booking.getCustomer() != null && booking.getCustomer().getUser() != null)
                ? booking.getCustomer().getUser().getEmail() : "";
        String customerPhone = booking.getCustomer() != null ? booking.getCustomer().getPhone() : "";
        String hotelName = booking.getHotel() != null ? booking.getHotel().getHotelName() : "Hotel Booking";

        log.info("Initiating SSLCommerz for booking={}, amount={}, customer={}", bookingId, booking.getDueAmount(), customerName);

        Map<String, Object> result = sslCommerzClient.initiateSession(
                transactionId,
                booking.getDueAmount(),
                "BDT",
                customerName,
                customerEmail,
                customerPhone,
                hotelName + " - Booking #" + bookingId,
                bookingId
        );

        return result;
    }

    @Override
    @Transactional
    public void handleSuccess(Map<String, String> params) {
        String transactionId = params.get("tran_id");
        if (transactionId == null) {
            log.warn("SSLCommerz success callback missing tran_id");
            return;
        }

        Payment payment = paymentRepository.findByTransactionId(transactionId).orElse(null);
        if (payment == null) {
            log.warn("SSLCommerz success callback - payment not found for tran_id: {}", transactionId);
            return;
        }

        try {
            String valId = params.get("val_id");
            if (valId == null) {
                log.warn("SSLCommerz success callback missing val_id for tran_id: {}", transactionId);
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                return;
            }

            Map<String, Object> validation = sslCommerzClient.validateTransaction(valId);
            String status = (String) validation.get("status");
            if (!"VALIDATED".equals(status)) {
                log.warn("SSLCommerz validation failed for tran_id: {}, status: {}", transactionId, status);
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                return;
            }

            Object validatedAmountObj = validation.get("amount");
            BigDecimal validatedAmount = new BigDecimal(String.valueOf(validatedAmountObj));
            if (validatedAmount.compareTo(payment.getAmount()) != 0) {
                log.error("Amount mismatch for tran_id: expected {}, got {}", payment.getAmount(), validatedAmount);
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                return;
            }

            payment.setStatus(PaymentStatus.PAID);
            payment.setBankTransactionId((String) validation.get("bank_tran_id"));
            payment.setValidationId(valId);
            payment.setTransactionDate(LocalDateTime.now());
            paymentRepository.save(payment);

            Booking booking = payment.getBooking();
            booking.setAdvanceAmount(booking.getAdvanceAmount().add(validatedAmount));
            booking.setDueAmount(booking.getDueAmount().subtract(validatedAmount).max(BigDecimal.ZERO));
            if (booking.getDueAmount().compareTo(BigDecimal.ZERO) <= 0) {
                booking.setStatus(BookingStatus.CONFIRMED);
            }
            bookingRepository.save(booking);

            // Update existing ISSUED invoice to PAID
            updateInvoiceToPaid(payment);

            // Generate receipt
            generateReceipt(payment);
        } catch (Exception e) {
            log.error("Error processing SSLCommerz success for tran_id: {}", transactionId, e);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        }
    }

    @Override
    @Transactional
    public void handleFail(Map<String, String> params) {
        String transactionId = params.get("tran_id");
        if (transactionId == null) return;

        paymentRepository.findByTransactionId(transactionId).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        });
    }

    @Override
    @Transactional
    public void handleCancel(Map<String, String> params) {
        String transactionId = params.get("tran_id");
        if (transactionId == null) return;

        paymentRepository.findByTransactionId(transactionId).ifPresent(payment -> {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
        });
    }

    @Override
    @Transactional
    public void handleIpn(Map<String, String> params) {
        // IPN is server-to-server - always trust this over browser callbacks
        String transactionId = params.get("tran_id");
        if (transactionId == null) {
            log.warn("SSLCommerz IPN missing tran_id");
            return;
        }

        Payment payment = paymentRepository.findByTransactionId(transactionId)
                .orElse(null);
        if (payment == null) {
            log.warn("SSLCommerz IPN for unknown transaction: {}", transactionId);
            return;
        }

        // Already processed
        if (payment.getStatus() == PaymentStatus.PAID) {
            return;
        }

        // Re-validate with SSLCommerz
        String valId = params.get("val_id");
        if (valId == null) {
            log.warn("SSLCommerz IPN missing val_id for tran_id: {}", transactionId);
            return;
        }

        Map<String, Object> validation = sslCommerzClient.validateTransaction(valId);
        String status = (String) validation.get("status");
        if (!"VALIDATED".equals(status)) {
            log.warn("SSLCommerz IPN validation failed for tran_id: {}", transactionId);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            return;
        }

        // Validate amount
        Object validatedAmountObj = validation.get("amount");
        BigDecimal validatedAmount = new BigDecimal(String.valueOf(validatedAmountObj));
        if (validatedAmount.compareTo(payment.getAmount()) != 0) {
            log.error("IPN amount mismatch for tran_id: expected {}, got {}", payment.getAmount(), validatedAmount);
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            return;
        }

        // Mark paid
        payment.setStatus(PaymentStatus.PAID);
        payment.setBankTransactionId((String) validation.get("bank_tran_id"));
        payment.setValidationId(valId);
        payment.setTransactionDate(LocalDateTime.now());
        paymentRepository.save(payment);

        // Update booking
        Booking booking = payment.getBooking();
        booking.setAdvanceAmount(booking.getAdvanceAmount().add(validatedAmount));
        booking.setDueAmount(booking.getDueAmount().subtract(validatedAmount).max(BigDecimal.ZERO));
        if (booking.getDueAmount().compareTo(BigDecimal.ZERO) <= 0) {
            booking.setStatus(BookingStatus.CONFIRMED);
        }
        bookingRepository.save(booking);

        // Update existing ISSUED invoice to PAID
        updateInvoiceToPaid(payment);

        // Generate receipt
        generateReceipt(payment);
    }

    private void updateInvoiceToPaid(Payment payment) {
        Booking booking = payment.getBooking();
        List<Invoice> invoices = invoiceRepository.findByBooking_Id(booking.getId());
        for (Invoice invoice : invoices) {
            if (invoice.getStatus() != InvoiceStatus.PAID) {
                invoice.setStatus(InvoiceStatus.PAID);
                invoice.setPayment(payment);
                invoiceRepository.save(invoice);
                log.info("Invoice {} updated to PAID for payment {}", invoice.getId(), payment.getId());
                auditTrailService.logAction("INVOICE_PAID", "Invoice", invoice.getId(),
                        "Invoice updated to PAID for payment " + payment.getId(), "SYSTEM");
            }
        }
    }

    private void generateReceipt(Payment payment) {
        try {
            receiptService.generateReceipt(payment.getId());
            log.info("Receipt generated for payment {}", payment.getId());
            auditTrailService.logAction("RECEIPT_GENERATED", "Receipt", payment.getId(),
                    "Receipt generated for payment " + payment.getId(), "SYSTEM");
        } catch (Exception e) {
            log.error("Receipt generation failed for payment {}: {}", payment.getId(), e.getMessage());
        }
    }
}
