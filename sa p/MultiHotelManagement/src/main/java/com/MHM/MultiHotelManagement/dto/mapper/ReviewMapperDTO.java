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
        response.setStatus(review.getStatus());
        response.setEditCount(review.getEditCount());
        response.setEditedAt(review.getEditedAt());

        try {
            response.setCustomerId(review.getCustomer() != null ? review.getCustomer().getId() : null);
            response.setCustomerName(review.getCustomer() != null ? review.getCustomer().getCustomerName() : null);
        } catch (Exception ignored) {}

        try {
            if (review.getHotel() != null) {
                response.setHotelId(review.getHotel().getId());
                response.setHotelName(review.getHotel().getHotelName());
                response.setHotelAddress(review.getHotel().getAddress());
            }
        } catch (Exception ignored) {}

        try {
            if (review.getBooking() != null) {
                response.setBookingId(review.getBooking().getId());
                response.setBookingStatus(review.getBooking().getStatus() != null
                        ? review.getBooking().getStatus().name() : null);
                if (review.getBooking().getRoom() != null) {
                    response.setRoomType(review.getBooking().getRoom().getRoomType());
                }
            }
        } catch (Exception ignored) {}

        response.setCreatedAt(review.getCreatedAt());
        response.setUpdatedAt(review.getUpdatedAt());
        response.setOwnerReply(review.getOwnerReply());
        response.setReplyAt(review.getReplyAt());
        return response;
    }
}
