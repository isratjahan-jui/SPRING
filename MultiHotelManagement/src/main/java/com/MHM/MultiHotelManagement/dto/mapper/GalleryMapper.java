package com.MHM.MultiHotelManagement.dto.mapper;

import com.MHM.MultiHotelManagement.dto.request.GalleryRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.GalleryResponseDTO;
import com.MHM.MultiHotelManagement.entity.Gallery;

public class GalleryMapper {

    // Entity → ResponseDTO
    public static GalleryResponseDTO toDTO(Gallery gallery) {
        GalleryResponseDTO dto = new GalleryResponseDTO();

        dto.setId(gallery.getId());
        dto.setImageUrl(gallery.getImageUrl());
        dto.setCaption(gallery.getCaption());
        dto.setCategory(gallery.getCategory());
        dto.setCreatedAt(gallery.getCreatedAt());
        dto.setUpdatedAt(gallery.getUpdatedAt());

        // Hotel info
        if (gallery.getHotel() != null) {
            dto.setHotelId(gallery.getHotel().getId());
            dto.setHotelName(gallery.getHotel().getHotelName());
        }

        return dto;
    }

    // RequestDTO → Entity
    public static Gallery toEntity(GalleryRequestDTO dto) {
        Gallery gallery = new Gallery();
        gallery.setCaption(dto.getCaption());
        gallery.setCategory(dto.getCategory());
        return gallery;
    }
}