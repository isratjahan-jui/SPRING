package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    List<Coupon> findByHotel_Id(Long hotelId);
    List<Coupon> findByActiveTrue();
    Coupon findByCodeAndActiveTrue(String code);
}
