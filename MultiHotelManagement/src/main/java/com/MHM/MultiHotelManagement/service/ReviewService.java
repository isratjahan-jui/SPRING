package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.ReviewRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO createReview(ReviewRequestDTO dto);
    ReviewResponseDTO updateReview(Long id, ReviewRequestDTO dto);
    ReviewResponseDTO getReviewById(Long id);
    List<ReviewResponseDTO> getReviewsByHotel(Long hotelId);
    List<ReviewResponseDTO> getApprovedReviewsByHotel(Long hotelId);
    List<ReviewResponseDTO> getReviewsByCustomer(Long customerId);
    List<ReviewResponseDTO> getAllReviews();
    List<ReviewResponseDTO> getReviewsByStatus(String status);
    ReviewResponseDTO approveReview(Long id);
    ReviewResponseDTO rejectReview(Long id);
    void deleteReview(Long id);
    boolean hasCustomerReviewedBooking(Long customerId, Long bookingId);
    ReviewResponseDTO replyToReview(Long id, String reply);
}
