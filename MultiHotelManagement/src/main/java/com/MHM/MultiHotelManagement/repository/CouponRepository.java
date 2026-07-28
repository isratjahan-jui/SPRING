package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    @Query("""
        SELECT c FROM Coupon c
        LEFT JOIN FETCH c.hotel h
        WHERE h.id = :hotelId
    """)
    List<Coupon> findByHotel_Id(@Param("hotelId") Long hotelId);

    List<Coupon> findByActiveTrue();

    @Query("""
        SELECT c FROM Coupon c
        LEFT JOIN FETCH c.hotel h
        WHERE c.active = true
    """)
    List<Coupon> findAllActiveWithHotel();

    @Query("""
        SELECT c FROM Coupon c
        LEFT JOIN FETCH c.hotel h
        WHERE c.code = :code AND c.active = true
    """)
    Coupon findByCodeAndActiveTrue(@Param("code") String code);

    @Query("""
        SELECT c FROM Coupon c
        LEFT JOIN FETCH c.hotel h
        WHERE c.code = :code AND h.id = :hotelId AND c.active = true
    """)
    Coupon findByCodeAndHotel_IdAndActiveTrue(@Param("code") String code, @Param("hotelId") Long hotelId);
}
