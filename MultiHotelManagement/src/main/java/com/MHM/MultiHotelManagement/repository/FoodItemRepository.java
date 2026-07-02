package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.FoodItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long> {
    List<FoodItem> findByHotelId(Long hotelId);
}
