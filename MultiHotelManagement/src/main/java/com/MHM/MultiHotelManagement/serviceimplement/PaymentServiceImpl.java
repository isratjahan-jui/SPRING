package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.PaymentMapper;
import com.MHM.MultiHotelManagement.dto.request.PaymentRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.PaymentResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.ExtraService;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.ExtraServiceRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.service.PaymentService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ExtraServiceRepository extraServiceRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository,
                              ExtraServiceRepository extraServiceRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.extraServiceRepository = extraServiceRepository;
    }

    @Override
    public PaymentResponseDTO createPayment(PaymentRequestDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        Payment payment = PaymentMapper.toEntity(dto);
        payment.setBooking(booking);

        if (dto.getExtraServiceId() != null) {
            ExtraService extraService = extraServiceRepository.findById(dto.getExtraServiceId())
                    .orElseThrow(() -> new EntityNotFoundException("ExtraService not found"));
            payment.setExtraService(extraService);
        }

        Payment saved = paymentRepository.save(payment);
        return PaymentMapper.toResponseDTO(saved);
    }

    @Override
    public PaymentResponseDTO updatePayment(Long id, PaymentRequestDTO dto) {
        Payment existing = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        existing.setMethod(dto.getMethod());
        existing.setAmount(dto.getAmount());
        if (dto.getStatus() != null) {
            existing.setStatus(PaymentStatus.valueOf(dto.getStatus()));
        }
        Payment updated = paymentRepository.save(existing);
        return PaymentMapper.toResponseDTO(updated);
    }

    @Override
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        return PaymentMapper.toResponseDTO(payment);
    }

//    @Override
//    public PaymentResponseDTO getPaymentByBooking(Long bookingId) {
//        Payment payment = paymentRepository.findByBookingId(bookingId);
//        if (payment == null) throw new EntityNotFoundException("Payment not found for booking");
//        return PaymentMapper.toResponseDTO(payment);
//    }


    @Override
    public PaymentResponseDTO getPaymentByBooking(Long bookingId) {
        Payment payment = paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for booking"));
        return PaymentMapper.toResponseDTO(payment);
    }

    @Override
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Payment not found");
        }
        paymentRepository.deleteById(id);
    }
}
