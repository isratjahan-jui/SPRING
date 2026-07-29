package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.HotelMapper;
import com.MHM.MultiHotelManagement.dto.request.HotelRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.entity.Location;
import com.MHM.MultiHotelManagement.enums.HotelStatus;
import com.MHM.MultiHotelManagement.enums.PaymentType;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.CouponRepository;
import com.MHM.MultiHotelManagement.repository.DealsRepository;
import com.MHM.MultiHotelManagement.repository.HotelExtraServiceRepository;
import com.MHM.MultiHotelManagement.repository.HotelOwnerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.LocationRepository;
import com.MHM.MultiHotelManagement.repository.ReviewRepository;
import com.MHM.MultiHotelManagement.repository.WishlistRepository;
import com.MHM.MultiHotelManagement.service.AuditTrailService;
import com.MHM.MultiHotelManagement.service.HotelService;
import com.MHM.MultiHotelManagement.util.FileUploadUtil;
import com.MHM.MultiHotelManagement.util.OwnershipGuard;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepo;
    private final HotelOwnerRepository ownerRepo;
    private final LocationRepository locationRepo;
    private final OwnershipGuard ownershipGuard;
    private final BookingRepository bookingRepo;
    private final CouponRepository couponRepo;
    private final DealsRepository dealsRepo;
    private final HotelExtraServiceRepository hotelExtraServiceRepo;
    private final WishlistRepository wishlistRepo;
    private final ReviewRepository reviewRepo;
    private final AuditTrailService auditTrailService;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    @Override
    @Transactional
    public HotelResponseDTO createHotel(HotelRequestDTO dto, MultipartFile image) {
        Hotel hotel = HotelMapper.toEntity(dto);

        if (dto.getOwnerId() == null || dto.getOwnerId() <= 0) {
            throw new BadRequestException("Valid owner ID is required");
        }

        HotelOwner owner = ownerRepo.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + dto.getOwnerId()));
        hotel.setOwner(owner);

        if (dto.getLocationId() == null || dto.getLocationId() <= 0) {
            throw new BadRequestException("Please select a valid location");
        }

        Location location = locationRepo.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + dto.getLocationId()));
        hotel.setLocation(location);

        if (dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            try {
                hotel.setStatus(HotelStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid hotel status: " + dto.getStatus());
            }
        } else {
            hotel.setStatus(HotelStatus.PENDING_APPROVAL);
        }

        if (image != null && !image.isEmpty()) {
            hotel.setImage(uploadImage(image, dto.getHotelName()));
        }

        if (dto.getPaymentType() != null && !dto.getPaymentType().isEmpty()) {
            try {
                hotel.setPaymentType(PaymentType.valueOf(dto.getPaymentType()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid payment type: " + dto.getPaymentType());
            }
        }
        if (dto.getAdvancePercentage() != null) {
            hotel.setAdvancePercentage(dto.getAdvancePercentage());
        }

        Hotel saved = hotelRepo.save(hotel);
        return HotelMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelResponseDTO getHotelById(Long id) {
        Hotel hotel = hotelRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        return HotelMapper.toDTO(hotel);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getAllApprovedHotels() {
        return hotelRepo.findAllApprovedWithDetails()
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getHotelsByOwner(Long ownerId) {
        return hotelRepo.findByOwner_IdWithDetails(ownerId)
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getHotelsByCity(String city) {
        return hotelRepo.findByCityWithDetails(city)
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> searchHotels(String keyword) {
        return hotelRepo.searchApprovedHotels(keyword)
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    // ✅ নতুন method এখানে add করো
    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getApprovedHotelsByLocation(Long locationId) {
        return hotelRepo.findApprovedHotelsByLocation(locationId)
                .stream()
                .map(HotelMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelResponseDTO updateHotel(Long id, HotelRequestDTO dto, MultipartFile image) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        ownershipGuard.verifyHotelOwnership(hotel);

        hotel.setHotelName(dto.getHotelName());
        hotel.setAddress(dto.getAddress());
        hotel.setDescription(dto.getDescription());
        hotel.setRating(dto.getRating());

        // Status transitions (approve/reject) are admin-only via the dedicated endpoints;
        // a hotel owner submitting this field back must not be able to self-approve.
        if (ownershipGuard.isAdmin() && dto.getStatus() != null && !dto.getStatus().isEmpty()) {
            try {
                hotel.setStatus(HotelStatus.valueOf(dto.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid hotel status: " + dto.getStatus());
            }
        }

        if (image != null && !image.isEmpty()) {
            hotel.setImage(uploadImage(image, dto.getHotelName()));
        } else if (dto.getImage() != null) {
            hotel.setImage(dto.getImage());
        }

        if (dto.getPaymentType() != null && !dto.getPaymentType().isEmpty()) {
            try {
                hotel.setPaymentType(PaymentType.valueOf(dto.getPaymentType()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid payment type: " + dto.getPaymentType());
            }
        }
        if (dto.getAdvancePercentage() != null) {
            hotel.setAdvancePercentage(dto.getAdvancePercentage());
        }

        Hotel saved = hotelRepo.save(hotel);
        return HotelMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteHotel(Long id) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        ownershipGuard.verifyHotelOwnership(hotel);

        // Bookings carry financial history (payments, invoices, commissions) —
        // deleting the hotel out from under them would either FK-fail or destroy records.
        if (bookingRepo.existsByHotel_Id(id)) {
            throw new BadRequestException(
                    "Cannot delete a hotel that has bookings. Reject or deactivate it instead.");
        }

        // These reference the hotel with NOT-NULL FKs but have no cascade mapping on
        // Hotel, so they must be removed first or the delete throws an FK violation.
        couponRepo.deleteByHotel_Id(id);
        dealsRepo.deleteByHotel_Id(id);
        hotelExtraServiceRepo.deleteByHotel_Id(id);
        wishlistRepo.deleteByHotel_Id(id);
        reviewRepo.deleteByHotel_Id(id);

        hotelRepo.delete(hotel);
    }
    // ── Image Upload Helper ──────────────────────────────────────
    private String uploadImage(MultipartFile file, String hotelName) {
        // Validated before the try block so a rejection surfaces as its own
        // BadRequestException instead of being rewrapped below.
        String ext = FileUploadUtil.safeExtension(file.getOriginalFilename());
        String safeName = FileUploadUtil.sanitizeBaseName(hotelName, "hotel");
        try {
            Path path = Paths.get(uploadDir, "hotel");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fileName = safeName + "_" + UUID.randomUUID() + ext;

            Files.copy(file.getInputStream(), path.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (Exception e) {
            throw new BadRequestException("Image upload failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getAllHotels() {
        return hotelRepo.findAllWithDetails()
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getPendingHotels() {
        return hotelRepo.findByStatusWithDetails(HotelStatus.PENDING_APPROVAL)
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public HotelResponseDTO approveHotel(Long id) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        hotel.setStatus(HotelStatus.APPROVED);
        Hotel saved = hotelRepo.save(hotel);

        try {
            auditTrailService.logAction("HOTEL_APPROVED", "Hotel", saved.getId(),
                    "Hotel '" + saved.getHotelName() + "' approved",
                    ownershipGuard.getCurrentUser().getEmail());
        } catch (Exception e) {
            // audit failure must not roll back the approval itself
        }

        return HotelMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public HotelResponseDTO rejectHotel(Long id, String reason) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));
        hotel.setStatus(HotelStatus.REJECTED);
        hotel.setRejectionReason(reason);
        Hotel saved = hotelRepo.save(hotel);

        try {
            auditTrailService.logAction("HOTEL_REJECTED", "Hotel", saved.getId(),
                    "Hotel '" + saved.getHotelName() + "' rejected. Reason: " + reason,
                    ownershipGuard.getCurrentUser().getEmail());
        } catch (Exception e) {
            // audit failure must not roll back the rejection itself
        }

        return HotelMapper.toDTO(saved);
    }


    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getApprovedHotelsByDivision(String divisionName) {
        return hotelRepo.findApprovedHotelsByDivision(divisionName)
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Long getHotelCountByDistrict(String districtName) {
        return hotelRepo.countApprovedHotelsByDistrict(districtName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getApprovedHotelsByUpazila(String upazilaName) {
        return hotelRepo.findApprovedHotelsByUpazila(upazilaName)
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> getApprovedHotelsByPlace(String placeName) {
        return hotelRepo.findApprovedHotelsByPlace(placeName)
                .stream().map(HotelMapper::toDTO).collect(Collectors.toList());
    }


    @Override
    @Transactional(readOnly = true)
    public List<HotelResponseDTO> unifiedSearch(String keyword) {
        keyword = keyword.trim().toLowerCase();

        // 1. Division wise
        List<Hotel> divisionHotels = hotelRepo.findApprovedHotelsByDivision(keyword);
        if (!divisionHotels.isEmpty()) {
            return divisionHotels.stream().map(HotelMapper::toDTO).toList();
        }

        // 2. District wise
        List<Hotel> districtHotels = hotelRepo.findApprovedHotelsByDistrict(keyword);
        if (!districtHotels.isEmpty()) {
            return districtHotels.stream().map(HotelMapper::toDTO).toList();
        }

        // 3. Upazila wise
        List<Hotel> upazilaHotels = hotelRepo.findApprovedHotelsByUpazila(keyword);
        if (!upazilaHotels.isEmpty()) {
            return upazilaHotels.stream().map(HotelMapper::toDTO).toList();
        }

        // 4. Place wise
        List<Hotel> placeHotels = hotelRepo.findApprovedHotelsByPlace(keyword);
        if (!placeHotels.isEmpty()) {
            return placeHotels.stream().map(HotelMapper::toDTO).toList();
        }

        // 5. Fallback → generic search
        return hotelRepo.searchApprovedHotels(keyword)
                .stream().map(HotelMapper::toDTO).toList();
    }


}
