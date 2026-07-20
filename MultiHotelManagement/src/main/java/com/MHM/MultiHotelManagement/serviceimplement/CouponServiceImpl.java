package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.CouponMapper;
import com.MHM.MultiHotelManagement.dto.request.CouponRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CouponResponseDTO;
import com.MHM.MultiHotelManagement.entity.Coupon;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.CouponRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.service.CouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

    private final CouponRepository couponRepository;
    private final HotelRepository hotelRepository;

    @Override
    @Transactional
    public CouponResponseDTO createCoupon(CouponRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        Coupon coupon = new Coupon();
        coupon.setCode(dto.getCode());
        coupon.setDiscountPercent(dto.getDiscountPercent());
        coupon.setDiscountAmount(dto.getDiscountAmount());
        coupon.setValidFrom(dto.getValidFrom());
        coupon.setValidUntil(dto.getValidUntil());
        coupon.setHotel(hotel);
        coupon.setActive(true);

        Coupon saved = couponRepository.save(coupon);
        return CouponMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void deactivateCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Coupon not found"));
        coupon.setActive(false);
        couponRepository.save(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDTO getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCodeAndActiveTrue(code);
        if (coupon == null) throw new ResourceNotFoundException("Coupon not found or inactive");
        return CouponMapper.toDTO(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public CouponResponseDTO getCouponByCodeAndHotel(String code, Long hotelId) {
        Coupon coupon = couponRepository.findByCodeAndHotel_IdAndActiveTrue(code, hotelId);
        if (coupon == null) throw new ResourceNotFoundException("Coupon not found, inactive, or not valid for this hotel");
        return CouponMapper.toDTO(coupon);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponseDTO> getAllActiveCoupons() {
        return couponRepository.findAllActiveWithHotel()
                .stream()
                .map(CouponMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CouponResponseDTO> getCouponsByHotel(Long hotelId) {
        return couponRepository.findByHotel_Id(hotelId)
                .stream()
                .map(CouponMapper::toDTO)
                .collect(Collectors.toList());
    }
}
