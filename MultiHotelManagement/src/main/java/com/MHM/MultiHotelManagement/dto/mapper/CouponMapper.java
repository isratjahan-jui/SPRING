package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.response.CouponResponseDTO;
import com.MHM.MultiHotelManagement.entity.Coupon;

public class CouponMapper {
    public static CouponResponseDTO toDTO(Coupon coupon) {
        CouponResponseDTO dto = new CouponResponseDTO();
        dto.setId(coupon.getId());
        dto.setCode(coupon.getCode());
        dto.setDiscountPercent(coupon.getDiscountPercent());
        dto.setDiscountAmount(coupon.getDiscountAmount());
        dto.setValidFrom(coupon.getValidFrom());
        dto.setValidUntil(coupon.getValidUntil());
        dto.setHotelName(coupon.getHotel().getHotelName());
        dto.setActive(coupon.isActive());
        return dto;
    }
}
