package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.RoomRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.RoomResponseDTO;
import com.MHM.MultiHotelManagement.entity.Room;

public class RoomMapper {

    // Entity → ResponseDTO
    public static RoomResponseDTO toDTO(Room room) {
        RoomResponseDTO dto = new RoomResponseDTO();

        dto.setId(room.getId());
        dto.setRoomType(room.getRoomType());
        dto.setDescription(room.getDescription());
        dto.setAmenities(room.getAmenities());
        dto.setImage(room.getImage());
        dto.setPrice(room.getPrice());
        dto.setTotalRooms(room.getTotalRooms());
        dto.setAvailableRooms(room.getAvailableRooms());
        dto.setBookedRooms(room.getBookedRooms());
        dto.setAdults(room.getAdults());
        dto.setChildren(room.getChildren());
        dto.setIsAvailable(room.getIsAvailable());
        dto.setCreatedAt(room.getCreatedAt());
        dto.setUpdatedAt(room.getUpdatedAt());

        // Hotel info
        if (room.getHotel() != null) {
            dto.setHotelId(room.getHotel().getId());
            dto.setHotelName(room.getHotel().getHotelName());
        }

        return dto;
    }

    // RequestDTO → Entity
    public static Room toEntity(RoomRequestDTO dto) {
        Room room = new Room();
        room.setRoomType(dto.getRoomType());
        room.setDescription(dto.getDescription());
        room.setAmenities(dto.getAmenities());
        room.setPrice(dto.getPrice());

        room.setTotalRooms(
                dto.getTotalRooms() != null
                        ? dto.getTotalRooms() : 0
        );
        room.setAvailableRooms(
                dto.getAvailableRooms() != null
                        ? dto.getAvailableRooms() : 0
        );
        room.setBookedRooms(
                dto.getBookedRooms() != null
                        ? dto.getBookedRooms() : 0
        );
        room.setAdults(
                dto.getAdults() != null
                        ? dto.getAdults() : 0
        );
        room.setChildren(
                dto.getChildren() != null
                        ? dto.getChildren() : 0
        );
        room.setIsAvailable(
                dto.getIsAvailable() != null
                        ? dto.getIsAvailable() : true
        );

        return room;
    }
}