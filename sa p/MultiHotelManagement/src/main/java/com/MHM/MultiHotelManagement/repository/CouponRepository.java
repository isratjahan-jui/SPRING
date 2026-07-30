package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
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

    void deleteByHotel_Id(Long hotelId);

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
        AND (c.validFrom IS NULL OR c.validFrom <= :now)
        AND (c.validUntil IS NULL OR c.validUntil >= :now)
        AND (c.usageLimit IS NULL OR COALESCE(c.usedCount, 0) < c.usageLimit)
    """)
    Coupon findValidByCode(@Param("code") String code, @Param("now") LocalDateTime now);

    @Query("""
        SELECT c FROM Coupon c
        LEFT JOIN FETCH c.hotel h
        WHERE c.code = :code AND h.id = :hotelId AND c.active = true
        AND (c.validFrom IS NULL OR c.validFrom <= :now)
        AND (c.validUntil IS NULL OR c.validUntil >= :now)
        AND (c.usageLimit IS NULL OR COALESCE(c.usedCount, 0) < c.usageLimit)
    """)
    Coupon findValidByCodeAndHotel_Id(@Param("code") String code, @Param("hotelId") Long hotelId,
                                      @Param("now") LocalDateTime now);
}
