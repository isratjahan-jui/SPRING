package com.MHM.MultiHotelManagement.service;



import com.MHM.MultiHotelManagement.dto.request.CouponRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CouponResponseDTO;

import java.util.List;

public interface CouponService {
    CouponResponseDTO createCoupon(CouponRequestDTO dto);
    void deactivateCoupon(Long id);
    CouponResponseDTO getCouponByCode(String code);
    CouponResponseDTO getCouponByCodeAndHotel(String code, Long hotelId);
    List<CouponResponseDTO> getAllActiveCoupons();
    List<CouponResponseDTO> getCouponsByHotel(Long hotelId);
}
