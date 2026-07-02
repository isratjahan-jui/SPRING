package com.MHM.MultiHotelManagement.serviceImpl;

import com.MHM.MultiHotelManagement.dto.mapper.GalleryMapper;
import com.MHM.MultiHotelManagement.dto.request.GalleryRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.GalleryResponseDTO;
import com.MHM.MultiHotelManagement.entity.Gallery;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.GalleryRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GalleryServiceImpl implements GalleryService {

    private final GalleryRepository galleryRepo;
    private final HotelRepository hotelRepo;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    // ── Single Image Upload ──────────────────────────────────────
    @Override
    @Transactional
    public GalleryResponseDTO upload(
            GalleryRequestDTO dto,
            MultipartFile image
    ) {
        // Image আছে কিনা check
        if (image == null || image.isEmpty()) {
            throw new BadRequestException(
                    "Image file is required"
            );
        }

        // Hotel আছে কিনা check
        Hotel hotel = hotelRepo.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with id: "
                                + dto.getHotelId()
                ));

        // Entity তৈরি করো
        Gallery gallery = GalleryMapper.toEntity(dto);
        gallery.setHotel(hotel);
        gallery.setImageUrl(
                uploadImage(image, hotel.getHotelName(),
                        dto.getCategory())
        );

        Gallery saved = galleryRepo.save(gallery);

        return GalleryMapper.toDTO(
                galleryRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
    }

    // ── Multiple Images Upload ───────────────────────────────────
    @Override
    @Transactional
    public List<GalleryResponseDTO> uploadMultiple(
            Long hotelId,
            String category,
            List<MultipartFile> images
    ) {
        // Images আছে কিনা check
        if (images == null || images.isEmpty()) {
            throw new BadRequestException(
                    "At least one image is required"
            );
        }

        // Hotel আছে কিনা check
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with id: " + hotelId
                ));

        List<GalleryResponseDTO> results = new ArrayList<>();

        for (MultipartFile image : images) {
            if (image == null || image.isEmpty()) continue;

            Gallery gallery = new Gallery();
            gallery.setHotel(hotel);
            gallery.setCategory(category);
            gallery.setImageUrl(
                    uploadImage(image, hotel.getHotelName(), category)
            );

            Gallery saved = galleryRepo.save(gallery);
            results.add(GalleryMapper.toDTO(saved));
        }

        return results;
    }

    // ── Get All ──────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<GalleryResponseDTO> getAll() {
        return galleryRepo.findAllWithDetails()
                .stream()
                .map(GalleryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get by ID ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public GalleryResponseDTO getById(Long id) {
        Gallery gallery = galleryRepo
                .findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gallery not found with id: " + id
                ));
        return GalleryMapper.toDTO(gallery);
    }

    // ── Get by Hotel ID ──────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<GalleryResponseDTO> getByHotelId(Long hotelId) {
        return galleryRepo.findByHotelIdWithDetails(hotelId)
                .stream()
                .map(GalleryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get by Hotel ID + Category ───────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<GalleryResponseDTO> getByHotelIdAndCategory(
            Long hotelId,
            String category
    ) {
        return galleryRepo
                .findByHotelIdAndCategoryWithDetails(
                        hotelId, category
                )
                .stream()
                .map(GalleryMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Update Caption বা Category ───────────────────────────────
    @Override
    @Transactional
    public GalleryResponseDTO update(
            Long id,
            GalleryRequestDTO dto
    ) {
        Gallery gallery = galleryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gallery not found with id: " + id
                ));

        // শুধু null না হলে update করো
        if (dto.getCaption() != null)
            gallery.setCaption(dto.getCaption());

        if (dto.getCategory() != null)
            gallery.setCategory(dto.getCategory());

        Gallery saved = galleryRepo.save(gallery);

        return GalleryMapper.toDTO(
                galleryRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
    }

    // ── Delete একটা Image ────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {
        Gallery gallery = galleryRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Gallery not found with id: " + id
                ));

        // Server থেকে physical file মুছে ফেলো
        deletePhysicalFile(gallery.getImageUrl());

        galleryRepo.deleteById(id);
    }

    // ── Delete Hotel এর সব Images ────────────────────────────────
    @Override
    @Transactional
    public void deleteAllByHotelId(Long hotelId) {
        if (!hotelRepo.existsById(hotelId)) {
            throw new ResourceNotFoundException(
                    "Hotel not found with id: " + hotelId
            );
        }

        // Physical files মুছে ফেলো
        List<Gallery> galleries =
                galleryRepo.findByHotel_Id(hotelId);
        galleries.forEach(g -> deletePhysicalFile(g.getImageUrl()));

        galleryRepo.deleteAllByHotelId(hotelId);
    }

    // ── Delete Hotel এর নির্দিষ্ট Category ──────────────────────
    @Override
    @Transactional
    public void deleteByHotelIdAndCategory(
            Long hotelId,
            String category
    ) {
        // Physical files মুছে ফেলো
        List<Gallery> galleries =
                galleryRepo.findByHotel_IdAndCategory(
                        hotelId, category
                );
        galleries.forEach(g -> deletePhysicalFile(g.getImageUrl()));

        galleryRepo.deleteByHotelIdAndCategory(hotelId, category);
    }

    // ── Image Upload Helper ──────────────────────────────────────
    private String uploadImage(
            MultipartFile file,
            String hotelName,
            String category
    ) {
        try {
            String folder = category != null
                    ? category.toLowerCase()
                    : "general";

            Path path = Paths.get(uploadDir, "gallery", folder);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(
                        original.lastIndexOf(".")
                );
            }

            String fileName = (hotelName != null
                    ? hotelName.trim().replaceAll("\\s+", "_")
                    : "hotel")
                    + "_" + UUID.randomUUID() + ext;

            Path filePath = path.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Relative path return করো
            return "gallery/" + folder + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException(
                    "Image upload failed: " + e.getMessage()
            );
        }
    }

    // ── Physical File Delete Helper ──────────────────────────────
    private void deletePhysicalFile(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isBlank()) {
                Path filePath = Paths.get(uploadDir, imageUrl);
                Files.deleteIfExists(filePath);
            }
        } catch (Exception e) {
            // File delete fail হলেও DB operation চলবে
            System.err.println(
                    "Could not delete file: " + imageUrl
            );
        }
    }
}