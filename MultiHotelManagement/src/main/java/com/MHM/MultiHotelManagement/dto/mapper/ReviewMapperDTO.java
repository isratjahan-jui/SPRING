package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.ReviewRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ReviewResponseDTO;
import com.MHM.MultiHotelManagement.entity.Review;

public class ReviewMapperDTO {

    public static Review toEntity(ReviewRequestDTO dto) {
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setComment(dto.getComment());
        return review;
    }

    public static ReviewResponseDTO toResponseDTO(Review review) {
        ReviewResponseDTO response = new ReviewResponseDTO();
        response.setId(review.getId());
        response.setRating(review.getRating());
        response.setComment(review.getComment());
        response.setCustomerName(review.getCustomer() != null ? review.getCustomer().getCustomerName() : null);
        response.setHotelName(review.getHotel() != null ? review.getHotel().getHotelName() : null);
        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        return response;
    }
}
