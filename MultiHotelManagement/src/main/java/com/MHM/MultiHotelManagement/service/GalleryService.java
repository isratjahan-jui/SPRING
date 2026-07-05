package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.request.GalleryRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.GalleryResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GalleryService {

    // Single image upload
    GalleryResponseDTO upload(
            GalleryRequestDTO dto,
            MultipartFile image
    );

    // Multiple images upload একসাথে
    List<GalleryResponseDTO> uploadMultiple(
            Long hotelId,
            String category,
            List<MultipartFile> images
    );

    // সব gallery
    List<GalleryResponseDTO> getAll();

    // ID দিয়ে খোঁজো
    GalleryResponseDTO getById(Long id);

    // Hotel ID দিয়ে সব image খোঁজো
    List<GalleryResponseDTO> getByHotelId(Long hotelId);

    // Hotel ID + Category দিয়ে খোঁজো
    List<GalleryResponseDTO> getByHotelIdAndCategory(
            Long hotelId,
            String category
    );

    // Caption বা Category update করো
    GalleryResponseDTO update(
            Long id,
            GalleryRequestDTO dto
    );

    // একটা image delete করো
    void delete(Long id);

    // Hotel এর সব image delete করো
    void deleteAllByHotelId(Long hotelId);

    // Hotel এর নির্দিষ্ট Category র সব image delete করো
    void deleteByHotelIdAndCategory(
            Long hotelId,
            String category
    );
}