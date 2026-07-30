package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.RoomMapper;
import com.MHM.MultiHotelManagement.dto.request.RoomRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.RoomResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.service.RoomService;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepo;
    private final HotelRepository hotelRepo;
    private final BookingRepository bookingRepo;
    private final OwnershipGuard ownershipGuard;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    // ── Create ───────────────────────────────────────────────────
    @Override
    @Transactional
    public RoomResponseDTO create(
            RoomRequestDTO dto,
            MultipartFile image
    ) {
        // Hotel আছে কিনা check
        Hotel hotel = hotelRepo.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with id: "
                                + dto.getHotelId()
                ));
        ownershipGuard.verifyHotelOwnership(hotel);

        // Entity তৈরি করো
        Room room = RoomMapper.toEntity(dto);
        room.setHotel(hotel);

        // Image upload
        if (image != null && !image.isEmpty()) {
            room.setImage(uploadImage(image, dto.getRoomType()));
        }

        Room saved = roomRepo.save(room);

        return RoomMapper.toDTO(
                roomRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
    }

    // ── Get All ──────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getAll() {
        return roomRepo.findAllWithDetails()
                .stream()
                .map(RoomMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get by ID ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public RoomResponseDTO getById(Long id) {
        Room room = roomRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + id
                ));
        return RoomMapper.toDTO(room);
    }

    // ── Get by Hotel ID ──────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getByHotelId(Long hotelId) {
        return roomRepo.findByHotelIdWithDetails(hotelId)
                .stream()
                .map(RoomMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get Available Rooms ──────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<RoomResponseDTO> getAvailableRooms(Long hotelId) {
        return roomRepo.findAvailableRoomsByHotel(hotelId)
                .stream()
                .map(RoomMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Update ───────────────────────────────────────────────────
    @Override
    @Transactional
    public RoomResponseDTO update(
            Long id,
            RoomRequestDTO dto,
            MultipartFile image
    ) {
        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + id
                ));
        ownershipGuard.verifyHotelOwnership(room.getHotel());

        // শুধু null না হলে update করো
        if (dto.getRoomType() != null)
            room.setRoomType(dto.getRoomType());

        if (dto.getDescription() != null)
            room.setDescription(dto.getDescription());

        if (dto.getAmenities() != null)
            room.setAmenities(dto.getAmenities());

        if (dto.getPrice() != null)
            room.setPricePerNight(dto.getPrice());

        if (dto.getTotalRooms() != null)
            room.setTotalRooms(dto.getTotalRooms());

        if (dto.getAvailableRooms() != null)
            room.setAvailableRooms(dto.getAvailableRooms());

        if (dto.getBookedRooms() != null)
            room.setBookedRooms(dto.getBookedRooms());

        if (dto.getAdults() != null)
            room.setAdults(dto.getAdults());

        if (dto.getChildren() != null)
            room.setChildren(dto.getChildren());

        if (dto.getIsAvailable() != null)
            room.setIsAvailable(dto.getIsAvailable());

        // Image update
        if (image != null && !image.isEmpty()) {
            room.setImage(uploadImage(image, room.getRoomType()));
        }

        Room saved = roomRepo.save(room);

        return RoomMapper.toDTO(
                roomRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
    }

    // ── Delete ───────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {
        Room room = roomRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + id
                ));
        ownershipGuard.verifyHotelOwnership(room.getHotel());
        roomRepo.deleteById(id);
    }

    // ── Booking এর সময় Availability Update ──────────────────────
    @Override
    @Transactional
    public void updateAvailability(Long roomId, int bookedCount) {
        // Locked so a concurrent booking can't read the same stale count and both succeed.
        Room room = roomRepo.findByIdForUpdate(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + roomId
                ));

        int newAvailable = room.getAvailableRooms() - bookedCount;

        if (newAvailable < 0) {
            throw new BadRequestException(
                    "Not enough rooms available. "
                            + "Available: " + room.getAvailableRooms()
                            + ", Requested: " + bookedCount
            );
        }

        room.setAvailableRooms(newAvailable);
        room.setBookedRooms(room.getBookedRooms() + bookedCount);

        // Available rooms শেষ হলে isAvailable false করো
        if (newAvailable == 0) {
            room.setIsAvailable(false);
        }

        roomRepo.save(room);
    }

    // ── Date-Specific Availability Check ─────────────────────────
    @Override
    @Transactional(readOnly = true)
    public int getAvailableRoomsForDates(Long roomId, Date checkIn, Date checkOut) {
        Room room = roomRepo.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: " + roomId
                ));

        int bookedForDates = bookingRepo.countBookedRoomsForDates(roomId, checkIn, checkOut);
        int remaining = room.getTotalRooms() - bookedForDates;
        return Math.max(0, remaining);
    }

    // ── Image Upload Helper ──────────────────────────────────────
    private String uploadImage(MultipartFile file, String name) {
        // Validated before the try block so a rejection surfaces as its own
        // BadRequestException instead of being rewrapped below.
        String ext = FileUploadUtil.safeExtension(file.getOriginalFilename());
        String safeName = FileUploadUtil.sanitizeBaseName(name, "room");
        try {
            Path path = Paths.get(uploadDir, "room");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String fileName = safeName + "_" + UUID.randomUUID() + ext;

            Files.copy(
                    file.getInputStream(),
                    path.resolve(fileName)
            );

            return fileName;
        } catch (Exception e) {
            throw new BadRequestException(
                    "Image upload failed: " + e.getMessage()
            );
        }
    }
}