package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.InvoiceMapper;
import com.MHM.MultiHotelManagement.dto.request.InvoiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.InvoiceResponseDTO;
import com.MHM.MultiHotelManagement.entity.*;
import com.MHM.MultiHotelManagement.enums.InvoiceStatus;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.*;
import com.MHM.MultiHotelManagement.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final CommissionRepository commissionRepository;
    private final InvoiceMapper mapper;

    @Override
    @Transactional
    public InvoiceResponseDTO create(InvoiceRequestDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
        Payment payment = paymentRepository.findById(dto.getPaymentId())
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Commission commission = null;
        if (dto.getCommissionId() != null) {
            commission = commissionRepository.findById(dto.getCommissionId())
                    .orElse(null);
        }

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber(UUID.randomUUID().toString());
        invoice.setBooking(booking);
        invoice.setPayment(payment);
        invoice.setCustomer(customer);
        invoice.setCommission(commission);
        invoice.setTotalAmount(dto.getTotalAmount());
        invoice.setTaxAmount(dto.getTaxAmount());
        invoice.setDiscountAmount(dto.getDiscountAmount());
        invoice.setNetAmount(dto.getTotalAmount() + dto.getTaxAmount() - dto.getDiscountAmount());
        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setIssuedAt(LocalDateTime.now());

        return mapper.toDTO(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponseDTO getById(Long id) {
        return mapper.toDTO(invoiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Invoice not found")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getByCustomer(Long customerId) {
        return invoiceRepository.findByCustomerIdWithDetails(customerId)
                .stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponseDTO> getAll() {
        return invoiceRepository.findAll()
                .stream().map(mapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        invoiceRepository.deleteById(id);
    }
}
