package com.MHM.MultiHotelManagement.repository;

import com.MHM.MultiHotelManagement.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByHotel_Id(Long hotelId);
    List<Room> findByHotel_IdAndIsAvailable(
            Long hotelId, Boolean isAvailable
    );
}
