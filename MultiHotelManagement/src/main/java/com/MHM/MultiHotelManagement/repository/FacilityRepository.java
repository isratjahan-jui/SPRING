package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityRepository
        extends JpaRepository<Facility, Long> {

    // Hotel ID diye sob facility khoja
    List<Facility> findByHotel_Id(Long hotelId);

    // specific Hotel a specific facility ache kina check
    Boolean existsByHotel_IdAndFacilityName(
            Long hotelId, String facilityName
    );

    // ID diye details soho khoja
    @Query("""
        SELECT f FROM Facility f
        LEFT JOIN FETCH f.hotel h
        WHERE f.id = :id
    """)
    Optional<Facility> findByIdWithDetails(@Param("id") Long id);

    // Hotel ID diye details soho sob khoja
    @Query("""
        SELECT f FROM Facility f
        LEFT JOIN FETCH f.hotel h
        WHERE f.hotel.id = :hotelId
        ORDER BY f.facilityName ASC
    """)
    List<Facility> findByHotelIdWithDetails(
            @Param("hotelId") Long hotelId
    );

    // sob facility details soho
    @Query("""
        SELECT f FROM Facility f
        LEFT JOIN FETCH f.hotel h
        ORDER BY f.createdAt DESC
    """)
    List<Facility> findAllWithDetails();

    // Hotel er sob facility muche felo
    @Modifying
    @Transactional
    @Query("""
        DELETE FROM Facility f
        WHERE f.hotel.id = :hotelId
    """)
    void deleteAllByHotelId(@Param("hotelId") Long hotelId);

    // Facility name diye khoja
    @Query("""
        SELECT f FROM Facility f
        LEFT JOIN FETCH f.hotel h
        WHERE LOWER(f.facilityName)
        LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    List<Facility> searchByName(@Param("name") String name);
}