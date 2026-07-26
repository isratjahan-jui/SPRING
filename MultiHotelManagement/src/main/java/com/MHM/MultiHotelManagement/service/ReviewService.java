package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.ReviewRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReviewResponseDTO;

import java.util.List;

public interface ReviewService {
    ReviewResponseDTO createReview(ReviewRequestDTO dto);
    ReviewResponseDTO updateReview(Long id, ReviewRequestDTO dto);
    ReviewResponseDTO getReviewById(Long id);
    List<ReviewResponseDTO> getReviewsByHotel(Long hotelId);
    List<ReviewResponseDTO> getReviewsByCustomer(Long customerId);
    void deleteReview(Long id);
}
