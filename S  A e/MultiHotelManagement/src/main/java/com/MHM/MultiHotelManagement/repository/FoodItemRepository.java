package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {

    @Query("""
        SELECT f FROM FoodItem f
        LEFT JOIN FETCH f.hotel h
        WHERE h.id = :hotelId
    """)
    List<FoodItem> findByHotelId(@Param("hotelId") Long hotelId);
}
