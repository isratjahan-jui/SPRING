package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Location;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByCity(String city);

    Optional<Location> findByLocationName(String locationName);

    Boolean existsByCity(String city);

    Boolean existsByLocationName(String locationName);

    @Query("""
        SELECT DISTINCT l FROM Location l
        LEFT JOIN FETCH l.hotels h
        WHERE l.id = :id
    """)
    Optional<Location> findByIdWithHotels(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT l FROM Location l
        LEFT JOIN FETCH l.hotels h
        ORDER BY l.locationName ASC
    """)
    List<Location> findAllWithHotels();

    @Query("""
        SELECT l FROM Location l
        WHERE LOWER(l.locationName)
        LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(l.city)
        LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(l.district)
        LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(l.division)
        LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(l.upazila)
        LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    List<Location> searchByKeyword(
            @Param("keyword") String keyword
    );

    @Query("""
        SELECT DISTINCT l FROM Location l
        LEFT JOIN FETCH l.hotels h
        WHERE SIZE(l.hotels) > 0
        ORDER BY SIZE(l.hotels) DESC
    """)
    List<Location> findLocationsWithHotels();

    @Query(value = """
        SELECT DISTINCT l FROM Location l
        LEFT JOIN FETCH l.hotels
        ORDER BY l.locationName ASC
    """, countQuery = "SELECT COUNT(l) FROM Location l")
    Page<Location> findAllPaginated(Pageable pageable);
}
