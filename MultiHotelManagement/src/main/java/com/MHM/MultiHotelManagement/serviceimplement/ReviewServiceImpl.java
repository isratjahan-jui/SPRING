package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.ReviewMapperDTO;
import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.ReviewRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReviewResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Review;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.ReviewRepository;
import com.MHM.MultiHotelManagement.service.NotificationService;
import com.MHM.MultiHotelManagement.service.ReviewService;
import com.MHM.MultiHotelManagement.util.OwnershipGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final HotelRepository hotelRepository;
    private final CustomerRepository customerRepository;
    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final OwnershipGuard ownershipGuard;

    public ReviewServiceImpl(ReviewRepository reviewRepository,
                             HotelRepository hotelRepository,
                             CustomerRepository customerRepository,
                             BookingRepository bookingRepository,
                             NotificationService notificationService,
                             OwnershipGuard ownershipGuard) {
        this.reviewRepository = reviewRepository;
        this.hotelRepository = hotelRepository;
        this.customerRepository = customerRepository;
        this.bookingRepository = bookingRepository;
        this.notificationService = notificationService;
        this.ownershipGuard = ownershipGuard;
    }

    @Override
    @Transactional
    public ReviewResponseDTO createReview(ReviewRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        Customer customer = customerRepository.findById(dto.getCustomerId())
                .orElseThrow(() -> new EntityNotFoundException("Customer not found"));

        if (dto.getBookingId() == null) {
            throw new IllegalStateException("Booking ID is required to submit a review");
        }

        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));

        // Verify booking belongs to this customer
        if (booking.getCustomer().getId() != dto.getCustomerId()) {
            throw new IllegalStateException("This booking does not belong to you");
        }

        // Verify booking is for this hotel
        if (booking.getHotel().getId() != dto.getHotelId()) {
            throw new IllegalStateException("This booking is not for this hotel");
        }

        // Only completed stays can be reviewed — a CONFIRMED (future/ongoing) booking
        // hasn't been experienced yet.
        BookingStatus bookingStatus = booking.getStatus();
        if (bookingStatus != BookingStatus.CHECKED_OUT) {
            throw new IllegalStateException(
                    "You can only review after your stay is completed (current status: " + bookingStatus + ")");
        }

        // Prevent duplicate review per booking
        if (reviewRepository.existsByCustomer_IdAndBooking_Id(dto.getCustomerId(), dto.getBookingId())) {
            throw new IllegalStateException("You have already reviewed this booking");
        }

        Review review = ReviewMapperDTO.toEntity(dto);
        review.setHotel(hotel);
        review.setCustomer(customer);
        review.setBooking(booking);
        // Reviews go live only after admin moderation — otherwise the
        // /approve and /reject endpoints are dead code.
        review.setStatus("PENDING");

        Review saved = reviewRepository.save(review);

        // Notify hotel owner
        try {
            NotificationRequestDTO ownerNotification = new NotificationRequestDTO();
            ownerNotification.setUserId(hotel.getOwner().getUser().getId());
            ownerNotification.setType(NotificationType.REVIEW_RECEIVED);
            ownerNotification.setChannel(NotificationChannel.WEB);
            ownerNotification.setMessage("New review received for " + hotel.getHotelName()
                    + " from " + customer.getCustomerName() + ". Rating: " + dto.getRating() + "/5");
            notificationService.createNotification(ownerNotification);
        } catch (Exception ignored) {}

        return ReviewMapperDTO.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ReviewResponseDTO updateReview(Long id, ReviewRequestDTO dto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        // Only the review's own author (or an admin) may edit it
        if (!ownershipGuard.isAdmin()) {
            Long currentUserId = ownershipGuard.getCurrentUser().getId();
            if (review.getCustomer() == null || review.getCustomer().getUser() == null
                    || !review.getCustomer().getUser().getId().equals(currentUserId)) {
                throw new AccessDeniedException("You can only edit your own review");
            }
        }

        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        review.setEditCount(review.getEditCount() + 1);
        review.setEditedAt(LocalDateTime.now());
        // Edited content must pass moderation again before showing publicly
        review.setStatus("PENDING");

        Review updated = reviewRepository.save(review);
        return ReviewMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ReviewResponseDTO getReviewById(Long id) {
        Review review = reviewRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        return ReviewMapperDTO.toResponseDTO(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByHotel(Long hotelId) {
        return reviewRepository.findByHotelIdWithDetailsOrderAll(hotelId)
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getApprovedReviewsByHotel(Long hotelId) {
        return reviewRepository.findByHotelIdWithDetails(hotelId, "APPROVED")
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByCustomer(Long customerId) {
        return reviewRepository.findByCustomerIdWithDetails(customerId)
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getAllReviews() {
        return reviewRepository.findAllWithDetails()
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponseDTO> getReviewsByStatus(String status) {
        return reviewRepository.findByStatusWithDetails(status)
                .stream()
                .map(ReviewMapperDTO::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ReviewResponseDTO approveReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        review.setStatus("APPROVED");
        Review updated = reviewRepository.save(review);
        return ReviewMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public ReviewResponseDTO rejectReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        review.setStatus("REJECTED");
        Review updated = reviewRepository.save(review);
        return ReviewMapperDTO.toResponseDTO(updated);
    }

    @Override
    @Transactional
    public void deleteReview(Long id) {
        if (!reviewRepository.existsById(id)) {
            throw new EntityNotFoundException("Review not found");
        }
        reviewRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasCustomerReviewedBooking(Long customerId, Long bookingId) {
        return reviewRepository.existsByCustomer_IdAndBooking_Id(customerId, bookingId);
    }

    @Override
    @Transactional
    public ReviewResponseDTO replyToReview(Long id, String reply) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        review.setOwnerReply(reply);
        review.setReplyAt(LocalDateTime.now());
        Review updated = reviewRepository.save(review);

        // Notify customer about owner reply
        try {
            NotificationRequestDTO customerNotification = new NotificationRequestDTO();
            customerNotification.setUserId(review.getCustomer().getUser().getId());
            customerNotification.setType(NotificationType.SUPPORT_REPLIED);
            customerNotification.setChannel(NotificationChannel.WEB);
            customerNotification.setMessage("The owner of " + review.getHotel().getHotelName()
                    + " has replied to your review.");
            notificationService.createNotification(customerNotification);
        } catch (Exception ignored) {}

        return ReviewMapperDTO.toResponseDTO(updated);
    }
}
