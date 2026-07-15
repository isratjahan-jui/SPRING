package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository  extends JpaRepository<Location, Long> {

    // City দিয়ে খোঁজো
    List<Location> findByCity(String city);

    // LocationName দিয়ে খোঁজো
    Optional<Location> findByLocationName(String locationName);

    // City আছে কিনা check
    Boolean existsByCity(String city);

    // LocationName আছে কিনা check
    Boolean existsByLocationName(String locationName);

    // ID দিয়ে hotels সহ খোঁজো
    @Query("""
        SELECT DISTINCT l FROM Location l
        LEFT JOIN FETCH l.hotels h
        WHERE l.id = :id
    """)
    Optional<Location> findByIdWithHotels(@Param("id") Long id);

    // সব location hotels সহ
    @Query("""
        SELECT DISTINCT l FROM Location l
        LEFT JOIN FETCH l.hotels h
        ORDER BY l.locationName ASC
    """)
    List<Location> findAllWithHotels();

    // Keyword দিয়ে search করো
    @Query("""
        SELECT l FROM Location l
        WHERE LOWER(l.locationName)
        LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(l.city)
        LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Location> searchByKeyword(
            @Param("keyword") String keyword
    );

    // Hotel আছে এমন locations
    @Query("""
        SELECT DISTINCT l FROM Location l
        LEFT JOIN FETCH l.hotels h
        WHERE SIZE(l.hotels) > 0
        ORDER BY SIZE(l.hotels) DESC
    """)
    List<Location> findLocationsWithHotels();
}