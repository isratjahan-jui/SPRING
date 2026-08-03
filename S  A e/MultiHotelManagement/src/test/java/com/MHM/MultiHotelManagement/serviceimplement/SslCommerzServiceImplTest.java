package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.util.SslCommerzClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SslCommerzServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private SslCommerzClient sslCommerzClient;

    @InjectMocks
    private SslCommerzServiceImpl sslCommerzService;

    private Booking booking;
    private Payment payment;

    @BeforeEach
    void setUp() {
        booking = new Booking();
        booking.setId(1L);
        booking.setDueAmount(new BigDecimal("5000.00"));
        booking.setAdvanceAmount(BigDecimal.ZERO);
        booking.setTotalAmount(new BigDecimal("5000.00"));
        booking.setStatus(BookingStatus.PENDING);

        payment = new Payment();
        payment.setId(1L);
        payment.setBooking(booking);
        payment.setAmount(new BigDecimal("5000.00"));
        payment.setTransactionId("TXN-TEST123");
        payment.setStatus(PaymentStatus.PENDING);
    }

    // ── SSLCommerz Amount Validation Tests ──────────────────────

    @Test
    void handleSuccess_ValidAmountMarksPaymentPaid() {
        Map<String, String> params = new HashMap<>();
        params.put("tran_id", "TXN-TEST123");
        params.put("val_id", "VAL123");

        when(paymentRepository.findByTransactionId("TXN-TEST123")).thenReturn(Optional.of(payment));
        when(sslCommerzClient.validateTransaction("VAL123")).thenReturn(Map.of(
                "status", "VALIDATED",
                "amount", "5000.00",
                "bank_tran_id", "BANK123"
        ));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(bookingRepository.save(any())).thenReturn(booking);

        sslCommerzService.handleSuccess(params);

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertEquals("BANK123", payment.getBankTransactionId());
        assertEquals("VAL123", payment.getValidationId());
        assertEquals(new BigDecimal("5000.00"), booking.getAdvanceAmount());
        assertEquals(0, BigDecimal.ZERO.compareTo(booking.getDueAmount()));
    }

    @Test
    void handleSuccess_AmountMismatchMarksPaymentFailed() {
        Map<String, String> params = new HashMap<>();
        params.put("tran_id", "TXN-TEST123");
        params.put("val_id", "VAL123");

        when(paymentRepository.findByTransactionId("TXN-TEST123")).thenReturn(Optional.of(payment));
        when(sslCommerzClient.validateTransaction("VAL123")).thenReturn(Map.of(
                "status", "VALIDATED",
                "amount", "3000.00"  // Mismatched amount
        ));
        when(paymentRepository.save(any())).thenReturn(payment);

        sslCommerzService.handleSuccess(params);

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }

    @Test
    void handleSuccess_ValidationFailedMarksPaymentFailed() {
        Map<String, String> params = new HashMap<>();
        params.put("tran_id", "TXN-TEST123");
        params.put("val_id", "VAL123");

        when(paymentRepository.findByTransactionId("TXN-TEST123")).thenReturn(Optional.of(payment));
        when(sslCommerzClient.validateTransaction("VAL123")).thenReturn(Map.of(
                "status", "INVALID"
        ));
        when(paymentRepository.save(any())).thenReturn(payment);

        sslCommerzService.handleSuccess(params);

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }

    @Test
    void handleSuccess_MissingValIdMarksPaymentFailed() {
        Map<String, String> params = new HashMap<>();
        params.put("tran_id", "TXN-TEST123");

        when(paymentRepository.findByTransactionId("TXN-TEST123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenReturn(payment);

        sslCommerzService.handleSuccess(params);

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }

    @Test
    void handleSuccess_UnknownTransactionDoesNothing() {
        Map<String, String> params = new HashMap<>();
        params.put("tran_id", "UNKNOWN");

        when(paymentRepository.findByTransactionId("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> sslCommerzService.handleSuccess(params));
    }

    @Test
    void handleSuccess_PartialPaymentUpdatesDueAmountCorrectly() {
        booking.setDueAmount(new BigDecimal("5000.00"));
        payment.setAmount(new BigDecimal("2000.00"));

        Map<String, String> params = new HashMap<>();
        params.put("tran_id", "TXN-TEST123");
        params.put("val_id", "VAL123");

        when(paymentRepository.findByTransactionId("TXN-TEST123")).thenReturn(Optional.of(payment));
        when(sslCommerzClient.validateTransaction("VAL123")).thenReturn(Map.of(
                "status", "VALIDATED",
                "amount", "2000.00",
                "bank_tran_id", "BANK123"
        ));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(bookingRepository.save(any())).thenReturn(booking);

        sslCommerzService.handleSuccess(params);

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertEquals(new BigDecimal("2000.00"), booking.getAdvanceAmount());
        assertEquals(new BigDecimal("3000.00"), booking.getDueAmount());
    }

    @Test
    void handleIpn_ValidAmountMarksPaidAndConfirmsBooking() {
        booking.setDueAmount(new BigDecimal("1000.00"));
        payment.setAmount(new BigDecimal("1000.00"));

        Map<String, String> params = new HashMap<>();
        params.put("tran_id", "TXN-TEST123");
        params.put("val_id", "VAL456");

        when(paymentRepository.findByTransactionId("TXN-TEST123")).thenReturn(Optional.of(payment));
        when(sslCommerzClient.validateTransaction("VAL456")).thenReturn(Map.of(
                "status", "VALIDATED",
                "amount", "1000.00",
                "bank_tran_id", "BANK456"
        ));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(bookingRepository.save(any())).thenReturn(booking);

        sslCommerzService.handleIpn(params);

        assertEquals(PaymentStatus.PAID, payment.getStatus());
        assertEquals(BookingStatus.CONFIRMED, booking.getStatus());
        assertEquals(0, BigDecimal.ZERO.compareTo(booking.getDueAmount()));
    }

    @Test
    void handleFail_MarksPaymentFailed() {
        Map<String, String> params = new HashMap<>();
        params.put("tran_id", "TXN-TEST123");

        when(paymentRepository.findByTransactionId("TXN-TEST123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenReturn(payment);

        sslCommerzService.handleFail(params);

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }

    @Test
    void handleCancel_MarksPaymentFailed() {
        Map<String, String> params = new HashMap<>();
        params.put("tran_id", "TXN-TEST123");

        when(paymentRepository.findByTransactionId("TXN-TEST123")).thenReturn(Optional.of(payment));
        when(paymentRepository.save(any())).thenReturn(payment);

        sslCommerzService.handleCancel(params);

        assertEquals(PaymentStatus.FAILED, payment.getStatus());
    }

    @Test
    void initiatePayment_ZeroDueThrowsBadRequest() {
        booking.setDueAmount(BigDecimal.ZERO);

        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> sslCommerzService.initiatePayment(1L));
    }

    @Test
    void initiatePayment_NegativeDueThrowsBadRequest() {
        booking.setDueAmount(new BigDecimal("-100.00"));

        when(bookingRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> sslCommerzService.initiatePayment(1L));
    }

    @Test
    void handleSuccess_DuplicatePaidNotificationDoesNotDoubleCredit() {
        payment.setStatus(PaymentStatus.PAID);
        booking.setAdvanceAmount(new BigDecimal("5000.00"));
        booking.setDueAmount(BigDecimal.ZERO);

        Map<String, String> params = new HashMap<>();
        params.put("tran_id", "TXN-TEST123");
        params.put("val_id", "VAL123");

        when(paymentRepository.findByTransactionId("TXN-TEST123")).thenReturn(Optional.of(payment));
        when(sslCommerzClient.validateTransaction("VAL123")).thenReturn(Map.of(
                "status", "VALIDATED",
                "amount", "5000.00",
                "bank_tran_id", "BANK123"
        ));
        when(paymentRepository.save(any())).thenReturn(payment);
        when(bookingRepository.save(any())).thenReturn(booking);

        sslCommerzService.handleSuccess(params);

        // Should still be PAID, amounts unchanged
        assertEquals(PaymentStatus.PAID, payment.getStatus());
    }
}
