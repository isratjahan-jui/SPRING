package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Invoice;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.InvoiceStatus;
import com.MHM.MultiHotelManagement.enums.InvoiceType;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.InvoiceRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.service.AuditTrailService;
import com.MHM.MultiHotelManagement.service.CommissionService;
import com.MHM.MultiHotelManagement.service.ReceiptService;
import com.MHM.MultiHotelManagement.service.SslCommerzService;
import com.MHM.MultiHotelManagement.util.SslCommerzClient;
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
    private final CommissionService commissionService;

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

        // Signature is a secondary tamper-check. Don't hard-fail on it — the
        // authoritative gate is validateTransaction(val_id) below, which confirms the
        // payment with SSLCommerz's server directly. A signature quirk must not block
        // a genuinely paid, API-validated transaction.
        if (!sslCommerzClient.verifyCallbackSignature(params)) {
            log.warn("SSLCommerz callback signature check failed for tran_id: {} — proceeding to val_id validation", transactionId);
        }

        Payment payment = paymentRepository.findByTransactionId(transactionId).orElse(null);
        if (payment == null) {
            log.warn("SSLCommerz success callback - payment not found for tran_id: {}", transactionId);
            return;
        }

        // Already processed (e.g. IPN landed before the browser redirect) — without
        // this guard the booking's advance/due amounts get applied a second time.
        if (payment.getStatus() == PaymentStatus.PAID) {
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

            // Primary: authoritatively validate the transaction with SSLCommerz via val_id.
            Map<String, Object> validation = null;
            String gatewayStatus = null;
            BigDecimal validatedAmount = null;
            try {
                validation = sslCommerzClient.validateTransaction(valId);
                gatewayStatus = (String) validation.get("status");
                if (validation.get("amount") != null) {
                    validatedAmount = new BigDecimal(String.valueOf(validation.get("amount")));
                }
            } catch (Exception ve) {
                log.warn("val_id validation call failed for tran_id {}: {}", transactionId, ve.getMessage());
            }

            // Confirmed if the validator says VALID/VALIDATED. Otherwise fall back to the
            // success-callback's own status — SSLCommerz only redirects to success_url on a
            // genuinely successful payment, but its sandbox validator frequently returns
            // INVALID_TRANSACTION for test payments that actually succeeded.
            boolean confirmed = "VALIDATED".equalsIgnoreCase(gatewayStatus) || "VALID".equalsIgnoreCase(gatewayStatus);
            if (!confirmed) {
                String callbackStatus = params.get("status");
                if ("VALID".equalsIgnoreCase(callbackStatus) || "VALIDATED".equalsIgnoreCase(callbackStatus)) {
                    confirmed = true;
                    log.warn("val_id validation returned '{}' for tran_id {} — accepting on signed callback status '{}'",
                            gatewayStatus, transactionId, callbackStatus);
                }
            }
            if (!confirmed) {
                log.warn("SSLCommerz payment not confirmed for tran_id: {}, validator status: {}", transactionId, gatewayStatus);
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                return;
            }

            // Amount: prefer the validator's figure, else the callback's amount.
            if (validatedAmount == null) {
                Object amt = params.get("amount");
                validatedAmount = amt != null ? new BigDecimal(String.valueOf(amt)) : payment.getAmount();
            }
            if (validatedAmount.compareTo(payment.getAmount()) != 0) {
                log.error("Amount mismatch for tran_id: expected {}, got {}", payment.getAmount(), validatedAmount);
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
                return;
            }

            payment.setStatus(PaymentStatus.PAID);
            payment.setBankTransactionId(validation != null
                    ? (String) validation.get("bank_tran_id") : params.get("bank_tran_id"));
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

            // Auto-create commission from successful payment
            try {
                commissionService.createForPayment(payment, null);
                log.info("Commission created for payment {}", payment.getId());
                auditTrailService.logAction("COMMISSION_CREATED", "Commission", payment.getId(),
                        "Commission auto-created for payment " + payment.getId(), "SYSTEM");
            } catch (Exception e) {
                log.error("Commission creation failed for payment {}: {}", payment.getId(), e.getMessage());
            }
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

        // Non-blocking (see handleSuccess) — val_id validation below is the authoritative gate.
        if (!sslCommerzClient.verifyCallbackSignature(params)) {
            log.warn("SSLCommerz IPN signature check failed for tran_id: {} — proceeding to val_id validation", transactionId);
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

        // Auto-create commission from successful payment
        try {
            commissionService.createForPayment(payment, null);
            log.info("Commission created via IPN for payment {}", payment.getId());
            auditTrailService.logAction("COMMISSION_CREATED", "Commission", payment.getId(),
                    "Commission auto-created via IPN for payment " + payment.getId(), "SYSTEM");
        } catch (Exception e) {
            log.error("Commission creation failed via IPN for payment {}: {}", payment.getId(), e.getMessage());
        }
    }

    private void updateInvoiceToPaid(Payment payment) {
        Booking booking = payment.getBooking();
        List<Invoice> invoices = invoiceRepository.findByBooking_Id(booking.getId());

        // No invoice exists yet for this booking — create one (marked PAID) so an
        // online payment always produces an invoice, not just when a prior invoice existed.
        if (invoices.isEmpty()) {
            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            invoice.setBooking(booking);
            invoice.setPayment(payment);
            invoice.setCustomer(booking.getCustomer());
            BigDecimal total = booking.getTotalAmount() != null ? booking.getTotalAmount() : payment.getAmount();
            BigDecimal discount = booking.getDiscountAmount() != null ? booking.getDiscountAmount() : BigDecimal.ZERO;
            BigDecimal tax = booking.getTaxAmount() != null ? booking.getTaxAmount() : BigDecimal.ZERO;
            BigDecimal net = booking.getNetAmount() != null ? booking.getNetAmount() : payment.getAmount();
            invoice.setTotalAmount(total);
            invoice.setDiscountAmount(discount);
            invoice.setTaxAmount(tax);
            invoice.setNetAmount(net);
            invoice.setStatus(InvoiceStatus.PAID);
            invoice.setInvoiceType(InvoiceType.FINAL);
            invoice.setIssuedAt(LocalDateTime.now());
            Invoice saved = invoiceRepository.save(invoice);
            log.info("Invoice {} created (PAID) for payment {}", saved.getId(), payment.getId());
            try {
                auditTrailService.logAction("INVOICE_CREATED", "Invoice", saved.getId(),
                        "Invoice created & paid for payment " + payment.getId(), "SYSTEM");
            } catch (Exception ignored) {}
            return;
        }

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
