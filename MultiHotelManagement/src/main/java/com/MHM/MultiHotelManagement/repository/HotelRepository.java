package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.enums.HotelStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {

    List<Hotel> findByStatus(HotelStatus status);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE h.status = :status
    """)
    List<Hotel> findByStatusWithDetails(@Param("status") HotelStatus status);

    Optional<Hotel> findByHotelName(String hotelname);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE h.id = :id
    """)
    Optional<Hotel> findByIdWithDetails(@Param("id") Long id);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE h.status = 'APPROVED'
    """)
    List<Hotel> findAllApprovedWithDetails();

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE o.id = :ownerId
    """)
    List<Hotel> findByOwner_IdWithDetails(@Param("ownerId") Long ownerId);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE h.location.city = :city
    """)
    List<Hotel> findByCityWithDetails(@Param("city") String city);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE h.status = 'APPROVED'
        AND (LOWER(l.city) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(l.locationName) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(h.hotelName) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(l.district) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(l.division) LIKE LOWER(CONCAT('%', :keyword, '%'))
             OR LOWER(l.upazila) LIKE LOWER(CONCAT('%', :keyword, '%')))
    """)
    List<Hotel> searchApprovedHotels(@Param("keyword") String keyword);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE h.location.id = :locationId
    """)
    List<Hotel> findHotelsByLocationId(@Param("locationId") Long locationId);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE h.location.id = :locationId
        AND h.status = 'APPROVED'
    """)
    List<Hotel> findApprovedHotelsByLocation(@Param("locationId") Long locationId);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
    """)
    List<Hotel> findAllWithDetails();

    @Query("""
        SELECT COUNT(h) FROM Hotel h
        JOIN h.location l
        WHERE LOWER(l.district) = LOWER(:districtName)
        AND h.status = 'APPROVED'
    """)
    Long countApprovedHotelsByDistrict(@Param("districtName") String districtName);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE LOWER(l.division) = LOWER(:divisionName)
        AND h.status = 'APPROVED'
    """)
    List<Hotel> findApprovedHotelsByDivision(@Param("divisionName") String divisionName);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE LOWER(l.district) = LOWER(:districtName)
        AND h.status = 'APPROVED'
    """)
    List<Hotel> findApprovedHotelsByDistrict(@Param("districtName") String districtName);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE LOWER(l.upazila) = LOWER(:upazilaName)
        AND h.status = 'APPROVED'
    """)
    List<Hotel> findApprovedHotelsByUpazila(@Param("upazilaName") String upazilaName);

    @Query("""
        SELECT h FROM Hotel h
        LEFT JOIN FETCH h.location l
        LEFT JOIN FETCH h.hotelDetails hd
        LEFT JOIN FETCH h.owner o
        WHERE LOWER(l.locationName) = LOWER(:placeName)
        AND h.status = 'APPROVED'
    """)
    List<Hotel> findApprovedHotelsByPlace(@Param("placeName") String placeName);
}
