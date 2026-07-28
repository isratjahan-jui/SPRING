package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.ReceiptMapper;
import com.MHM.MultiHotelManagement.dto.response.ReceiptResponseDTO;
import com.MHM.MultiHotelManagement.entity.*;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.*;
import com.MHM.MultiHotelManagement.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReceiptServiceImpl implements ReceiptService {

    private final ReceiptRepository receiptRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final CustomerRepository customerRepository;
    private final ReceiptMapper receiptMapper;

    @Override
    @Transactional
    public ReceiptResponseDTO generateReceipt(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with id: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PAID) {
            throw new RuntimeException("Cannot generate receipt for unpaid payment");
        }

        if (receiptRepository.findByPaymentId(paymentId).isPresent()) {
            return receiptMapper.toDTO(receiptRepository.findByPaymentId(paymentId).get());
        }

        Booking booking = payment.getBooking();
        Customer customer = booking.getCustomer();

        Invoice invoice = null;
        List<Invoice> invoices = invoiceRepository.findByBooking_Id(booking.getId());
        if (!invoices.isEmpty()) {
            invoice = invoices.get(0);
        }

        String receiptNumber = generateReceiptNumber();

        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(receiptNumber);
        receipt.setPayment(payment);
        receipt.setInvoice(invoice);
        receipt.setBooking(booking);
        receipt.setCustomer(customer);
        receipt.setAmount(payment.getAmount());

        if (invoice != null) {

            receipt.setTaxAmount(BigDecimal.valueOf(invoice.getTaxAmount()));
            receipt.setTotalAmount(BigDecimal.valueOf(invoice.getNetAmount()));

            receipt.setTaxAmount(invoice.getTaxAmount() != null ? invoice.getTaxAmount() : BigDecimal.ZERO);
            receipt.setTotalAmount(invoice.getNetAmount() != null ? invoice.getNetAmount() : payment.getAmount());

        } else {
            receipt.setTaxAmount(BigDecimal.ZERO);
            receipt.setTotalAmount(payment.getAmount());
        }

        receipt.setPaymentMethod(payment.getMethod());
        receipt.setTransactionId(payment.getTransactionId());

        Receipt saved = receiptRepository.save(receipt);
        log.info("Receipt generated: {} for payment {}", receiptNumber, paymentId);
        return receiptMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptResponseDTO getReceiptById(Long id) {
        Receipt receipt = receiptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found with id: " + id));
        return receiptMapper.toDTO(receipt);
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptResponseDTO getReceiptByNumber(String receiptNumber) {
        Receipt receipt = receiptRepository.findByReceiptNumber(receiptNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found: " + receiptNumber));
        return receiptMapper.toDTO(receipt);
    }

    @Override
    @Transactional(readOnly = true)
    public ReceiptResponseDTO getReceiptByPaymentId(Long paymentId) {
        Receipt receipt = receiptRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Receipt not found for payment id: " + paymentId));
        return receiptMapper.toDTO(receipt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReceiptResponseDTO> getReceiptsByCustomerId(Long customerId) {
        return receiptRepository.findByCustomerIdWithDetails(customerId)
                .stream()
                .map(receiptMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReceiptResponseDTO> getReceiptsByHotelId(Long hotelId) {
        return receiptRepository.findByHotelIdWithDetails(hotelId)
                .stream()
                .map(receiptMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReceiptResponseDTO> getReceiptsByOwnerId(Long ownerId) {
        return receiptRepository.findByOwnerIdWithDetails(ownerId)
                .stream()
                .map(receiptMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long countReceiptsByHotelId(Long hotelId) {
        return receiptRepository.countByHotelId(hotelId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal sumReceiptsByHotelId(Long hotelId) {
        return receiptRepository.sumTotalAmountByHotelId(hotelId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReceiptResponseDTO> getAllReceipts() {
        return receiptRepository.findAllWithDetails()
                .stream()
                .map(receiptMapper::toDTO)
                .collect(Collectors.toList());
    }

    private String generateReceiptNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "RCP-" + timestamp + "-" + random;
    }
}
