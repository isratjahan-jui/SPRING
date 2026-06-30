package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Commission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommissionRepository extends JpaRepository<Commission, Long> {

    // ── Booking দিয়ে খোঁজো ──────────────────────────────────────
    Optional<Commission> findByBooking_Id(Long bookingId);

    Boolean existsByBooking_Id(Long bookingId);

    // ── Hotel Owner দিয়ে খোঁজো ──────────────────────────────────
    @Query("""
        SELECT c FROM Commission c
        LEFT JOIN FETCH c.booking b
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH h.owner o
        LEFT JOIN FETCH o.user
        WHERE o.id = :ownerId
    """)
    List<Commission> findByOwnerId(@Param("ownerId") Long ownerId);

    // ── Hotel দিয়ে খোঁজো ─────────────────────────────────────────
    @Query("""
        SELECT c FROM Commission c
        LEFT JOIN FETCH c.booking b
        LEFT JOIN FETCH b.hotel h
        WHERE h.id = :hotelId
    """)
    List<Commission> findByHotelId(@Param("hotelId") Long hotelId);

    // ── ID দিয়ে details সহ খোঁজো ─────────────────────────────────
    @Query("""
        SELECT c FROM Commission c
        LEFT JOIN FETCH c.booking b
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH h.owner o
        LEFT JOIN FETCH o.user
        LEFT JOIN FETCH b.customer cu
        LEFT JOIN FETCH cu.user
        WHERE c.id = :id
    """)
    Optional<Commission> findByIdWithDetails(@Param("id") Long id);

    // ── সব Commission details সহ ─────────────────────────────────
    @Query("""
        SELECT c FROM Commission c
        LEFT JOIN FETCH c.booking b
        LEFT JOIN FETCH b.hotel h
        LEFT JOIN FETCH h.owner o
        LEFT JOIN FETCH o.user
        ORDER BY c.createdAt DESC
    """)
    List<Commission> findAllWithDetails();

    // ── Admin এর মোট আয় ──────────────────────────────────────────
    @Query("""
        SELECT COALESCE(SUM(c.adminEarnings), 0)
        FROM Commission c
    """)
    Double getTotalAdminEarnings();

    // ── নির্দিষ্ট Owner এর মোট আয় ───────────────────────────────
    @Query("""
        SELECT COALESCE(SUM(c.hotelOwnerEarnings), 0)
        FROM Commission c
        WHERE c.booking.hotel.owner.id = :ownerId
    """)
    Double getTotalOwnerEarnings(@Param("ownerId") Long ownerId);

    // ── নির্দিষ্ট Hotel এর মোট Commission ───────────────────────
    @Query("""
        SELECT COALESCE(SUM(c.adminEarnings), 0)
        FROM Commission c
        WHERE c.booking.hotel.id = :hotelId
    """)
    Double getTotalCommissionByHotel(
            @Param("hotelId") Long hotelId
    );

    // ── Date Range দিয়ে খোঁজো ────────────────────────────────────
    @Query("""
        SELECT c FROM Commission c
        LEFT JOIN FETCH c.booking b
        LEFT JOIN FETCH b.hotel h
        WHERE c.createdAt BETWEEN :startDate AND :endDate
        ORDER BY c.createdAt DESC
    """)
    List<Commission> findByDateRange(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    // ── Commission Rate দিয়ে খোঁজো ───────────────────────────────
    List<Commission> findByCommissionRate(Double commissionRate);

    // ── Monthly Report এর জন্য ───────────────────────────────────
    @Query("""
        SELECT
            MONTH(c.createdAt) as month,
            YEAR(c.createdAt) as year,
            COALESCE(SUM(c.adminEarnings), 0) as totalAdmin,
            COALESCE(SUM(c.hotelOwnerEarnings), 0) as totalOwner
        FROM Commission c
        WHERE YEAR(c.createdAt) = :year
        GROUP BY YEAR(c.createdAt), MONTH(c.createdAt)
        ORDER BY MONTH(c.createdAt)
    """)
    List<Object[]> getMonthlyReport(@Param("year") int year);
}
