package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.PaymentMapper;
import com.MHM.MultiHotelManagement.dto.request.PaymentRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.PaymentResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.ExtraService;
import com.MHM.MultiHotelManagement.entity.Invoice;
import com.MHM.MultiHotelManagement.entity.Payment;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.InvoiceStatus;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.ExtraServiceRepository;
import com.MHM.MultiHotelManagement.repository.InvoiceRepository;
import com.MHM.MultiHotelManagement.repository.PaymentRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.service.PaymentService;
import com.MHM.MultiHotelManagement.service.NotificationService;
import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private static final Logger log = LoggerFactory.getLogger(PaymentServiceImpl.class);

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;
    private final ExtraServiceRepository extraServiceRepository;
    private final RoomRepository roomRepository;
    private final InvoiceRepository invoiceRepository;
    private final NotificationService notificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              BookingRepository bookingRepository,
                              ExtraServiceRepository extraServiceRepository,
                              RoomRepository roomRepository,
                              InvoiceRepository invoiceRepository,
                              NotificationService notificationService) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
        this.extraServiceRepository = extraServiceRepository;
        this.roomRepository = roomRepository;
        this.invoiceRepository = invoiceRepository;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public PaymentResponseDTO createPayment(PaymentRequestDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        Payment payment = PaymentMapper.toEntity(dto);
        payment.setBooking(booking);
        payment.setCustomerId(dto.getCustomerId() != null ? dto.getCustomerId()
                : (booking.getCustomer() != null ? booking.getCustomer().getId() : null));
        payment.setTransactionDate(LocalDateTime.now());
        if (dto.getStatus() != null) {
            payment.setStatus(PaymentStatus.valueOf(dto.getStatus()));
        } else {
            payment.setStatus(PaymentStatus.PAID);
        }

        if (dto.getExtraServiceId() != null) {
            ExtraService extraService = extraServiceRepository.findById(dto.getExtraServiceId())
                    .orElseThrow(() -> new EntityNotFoundException("ExtraService not found"));
            payment.setExtraService(extraService);
        }

        Payment saved = paymentRepository.save(payment);

        // Auto-update booking due amount using BigDecimal
        BigDecimal paid = saved.getAmount() != null ? saved.getAmount() : BigDecimal.ZERO;
        BigDecimal currentAdvance = booking.getAdvanceAmount() != null ? booking.getAdvanceAmount() : BigDecimal.ZERO;
        BigDecimal currentDue = booking.getDueAmount() != null ? booking.getDueAmount() : BigDecimal.ZERO;
        booking.setAdvanceAmount(currentAdvance.add(paid));
        booking.setDueAmount(currentDue.subtract(paid).max(BigDecimal.ZERO));

        // Auto-confirm booking when fully paid
        if (booking.getDueAmount().compareTo(BigDecimal.ZERO) <= 0
                && booking.getStatus() == BookingStatus.PENDING) {
            booking.setStatus(BookingStatus.CONFIRMED);
        }
        bookingRepository.save(booking);

        // Auto-generate invoice for PAID payments
        if (saved.getStatus() == PaymentStatus.PAID) {
            try {
                generateInvoice(booking, saved);
                log.info("Invoice generated for payment {}, booking {}", saved.getId(), booking.getId());
            } catch (Exception e) {
                log.error("Invoice generation failed for payment {}: {}", saved.getId(), e.getMessage(), e);
            }

            // Send payment notification
            try {
                if (booking.getCustomer() != null && booking.getCustomer().getUser() != null) {
                    NotificationRequestDTO customerNotification = new NotificationRequestDTO();
                    customerNotification.setUserId(booking.getCustomer().getUser().getId());
                    customerNotification.setType(NotificationType.PAYMENT_SUCCESSFUL);
                    customerNotification.setChannel(NotificationChannel.WEB);
                    customerNotification.setMessage("Payment of ৳" + saved.getAmount() + " received for booking #" + booking.getId() + ". Thank you!");
                    notificationService.createNotification(customerNotification);
                }
                if (booking.getHotel() != null && booking.getHotel().getOwner() != null && booking.getHotel().getOwner().getUser() != null) {
                    NotificationRequestDTO ownerNotification = new NotificationRequestDTO();
                    ownerNotification.setUserId(booking.getHotel().getOwner().getUser().getId());
                    ownerNotification.setType(NotificationType.PAYMENT_SUCCESSFUL);
                    ownerNotification.setChannel(NotificationChannel.WEB);
                    ownerNotification.setMessage("Payment of ৳" + saved.getAmount() + " received for booking #" + booking.getId() + " at " + booking.getHotel().getHotelName());
                    notificationService.createNotification(ownerNotification);
                }
            } catch (Exception ignored) {}
        }

        return PaymentMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
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
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentById(Long id) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found"));
        return PaymentMapper.toResponseDTO(payment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentRepository.findAllWithDetails()
                .stream().map(PaymentMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentResponseDTO> getPaymentsByCustomer(Long customerId) {
        return paymentRepository.findByCustomerIdWithDetails(customerId)
                .stream().map(PaymentMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentResponseDTO getPaymentByBooking(Long bookingId) {
        Payment payment = paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for booking"));
        return PaymentMapper.toResponseDTO(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDTO processRefund(Long bookingId) {
        Payment payment = paymentRepository.findByBooking_Id(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Payment not found for booking"));

        Booking booking = payment.getBooking();

        // Restore room availability when refunding/cancelling
        if (booking.getStatus() != BookingStatus.CANCELLED) {
            Room room = booking.getRoom();
            room.setAvailableRooms(room.getAvailableRooms() + booking.getNumberOfRooms());
            room.setBookedRooms(Math.max(0, room.getBookedRooms() - booking.getNumberOfRooms()));
            room.setIsAvailable(true);
            roomRepository.save(room);
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        payment.setStatus(PaymentStatus.REFUNDED);
        Payment saved = paymentRepository.save(payment);

        // Send refund notification
        try {
            if (booking.getCustomer() != null && booking.getCustomer().getUser() != null) {
                NotificationRequestDTO customerNotification = new NotificationRequestDTO();
                customerNotification.setUserId(booking.getCustomer().getUser().getId());
                customerNotification.setType(NotificationType.PAYMENT_REFUNDED);
                customerNotification.setChannel(NotificationChannel.WEB);
                customerNotification.setMessage("Refund of ৳" + saved.getAmount() + " processed for booking #" + bookingId + ". Amount credited to your wallet.");
                notificationService.createNotification(customerNotification);
            }
        } catch (Exception ignored) {}

        return PaymentMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public void deletePayment(Long id) {
        if (!paymentRepository.existsById(id)) {
            throw new EntityNotFoundException("Payment not found");
        }
        paymentRepository.deleteById(id);
    }

    private void generateInvoice(Booking booking, Payment payment) {
        if (booking.getCustomer() == null) return;

        boolean alreadyExists = invoiceRepository.existsByBooking_IdAndPayment_Id(
                booking.getId(), payment.getId());
        if (alreadyExists) return;

        // Reload booking with customer fetched
        Booking loadedBooking = bookingRepository.findByIdWithDetails(booking.getId()).orElse(booking);

        Invoice invoice = new Invoice();
        invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        invoice.setBooking(loadedBooking);
        invoice.setPayment(payment);
        invoice.setCustomer(loadedBooking.getCustomer());

        double total = loadedBooking.getTotalAmount() != null ? loadedBooking.getTotalAmount().doubleValue() : 0;
        double discount = loadedBooking.getDiscountRate() != null && loadedBooking.getDiscountRate().compareTo(BigDecimal.ZERO) > 0
                ? BigDecimal.valueOf(total)
                    .multiply(loadedBooking.getDiscountRate())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                    .doubleValue() : 0;
        double tax = BigDecimal.valueOf(total - discount)
                .multiply(BigDecimal.valueOf(0.15))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();

        invoice.setTotalAmount(total);
        invoice.setDiscountAmount(discount);
        invoice.setTaxAmount(tax);
        invoice.setNetAmount(BigDecimal.valueOf(total + tax - discount)
                .setScale(2, RoundingMode.HALF_UP).doubleValue());
        invoice.setStatus(InvoiceStatus.ISSUED);
        invoice.setIssuedAt(LocalDateTime.now());

        invoiceRepository.save(invoice);
    }
}
