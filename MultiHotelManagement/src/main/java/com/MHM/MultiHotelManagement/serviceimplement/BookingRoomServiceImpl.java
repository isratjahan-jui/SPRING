package com.MHM.MultiHotelManagement.serviceimplement;


import com.MHM.MultiHotelManagement.dto.mapper.BookingRoomMapper;
import com.MHM.MultiHotelManagement.dto.request.BookingRoomRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.BookingRoomResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.BookingRoom;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.exception.AlreadyExistsException;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.BookingRoomRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.service.BookingRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingRoomServiceImpl implements BookingRoomService {

    private final BookingRoomRepository bookingRoomRepo;
    private final BookingRepository bookingRepo;
    private final RoomRepository roomRepo;

    // ── Create একটা BookingRoom ──────────────────────────────────
    @Override
    @Transactional
    public BookingRoomResponseDTO create(
            BookingRoomRequestDTO dto
    ) {
        // Booking আছে কিনা check
        Booking booking = bookingRepo
                .findById(dto.getBookingId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Booking not found with id: "
                                + dto.getBookingId()
                ));

        // Room আছে কিনা check
        Room room = roomRepo.findById(dto.getRoomId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Room not found with id: "
                                + dto.getRoomId()
                ));

        // এই booking এ এই room already আছে কিনা check
        if (bookingRoomRepo.existsByBooking_IdAndRoom_Id(
                dto.getBookingId(), dto.getRoomId())) {
            throw new AlreadyExistsException(
                    "Room already added to this booking"
            );
        }

        // Room available আছে কিনা check
        int requested = dto.getNumberOfRooms() != null
                ? dto.getNumberOfRooms() : 1;

        if (room.getAvailableRooms() < requested) {
            throw new BadRequestException(
                    "Not enough rooms available. "
                            + "Requested: " + requested
                            + ", Available: " + room.getAvailableRooms()
            );
        }

        // Price calculate করো
        Double totalPrice = room.getPricePerNight() * requested;

        // Entity তৈরি করো
        BookingRoom bookingRoom = BookingRoomMapper.toEntity(dto);
        bookingRoom.setBooking(booking);
        bookingRoom.setRoom(room);
        bookingRoom.setPrice(totalPrice);

        // Room availability update করো
        room.setAvailableRooms(
                room.getAvailableRooms() - requested
        );
        room.setBookedRooms(
                room.getBookedRooms() + requested
        );
        if (room.getAvailableRooms() == 0) {
            room.setIsAvailable(false);
        }
        roomRepo.save(room);

        BookingRoom saved = bookingRoomRepo.save(bookingRoom);

        return BookingRoomMapper.toDTO(
                bookingRoomRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
    }

    // ── Create Multiple BookingRooms ─────────────────────────────
    @Override
    @Transactional
    public List<BookingRoomResponseDTO> createMultiple(
            List<BookingRoomRequestDTO> dtoList
    ) {
        if (dtoList == null || dtoList.isEmpty()) {
            throw new BadRequestException(
                    "At least one room is required"
            );
        }

        List<BookingRoomResponseDTO> results = new ArrayList<>();
        for (BookingRoomRequestDTO dto : dtoList) {
            results.add(create(dto));
        }
        return results;
    }

    // ── Get by ID ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public BookingRoomResponseDTO getById(Long id) {
        BookingRoom bookingRoom = bookingRoomRepo
                .findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BookingRoom not found with id: " + id
                ));
        return BookingRoomMapper.toDTO(bookingRoom);
    }

    // ── Get by Booking ID ────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<BookingRoomResponseDTO> getByBookingId(
            Long bookingId
    ) {
        return bookingRoomRepo
                .findByBookingIdWithDetails(bookingId)
                .stream()
                .map(BookingRoomMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get by Room ID ───────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<BookingRoomResponseDTO> getByRoomId(Long roomId) {
        return bookingRoomRepo
                .findByRoomIdWithDetails(roomId)
                .stream()
                .map(BookingRoomMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get Total Price by Booking ID ────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public Double getTotalPriceByBookingId(Long bookingId) {
        return bookingRoomRepo
                .getTotalPriceByBookingId(bookingId);
    }

    // ── Update ───────────────────────────────────────────────────
    @Override
    @Transactional
    public BookingRoomResponseDTO update(
            Long id,
            BookingRoomRequestDTO dto
    ) {
        BookingRoom bookingRoom = bookingRoomRepo
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BookingRoom not found with id: " + id
                ));

        Room room = bookingRoom.getRoom();

        // numberOfRooms পরিবর্তন হলে availability recalculate
        if (dto.getNumberOfRooms() != null
                && dto.getNumberOfRooms()
                != bookingRoom.getNumberOfRooms()) {

            int oldCount = bookingRoom.getNumberOfRooms();
            int newCount = dto.getNumberOfRooms();
            int diff = newCount - oldCount;

            if (diff > 0 && room.getAvailableRooms() < diff) {
                throw new BadRequestException(
                        "Not enough rooms available"
                );
            }

            // Room availability update করো
            room.setAvailableRooms(
                    room.getAvailableRooms() - diff
            );
            room.setBookedRooms(
                    room.getBookedRooms() + diff
            );
            room.setIsAvailable(room.getAvailableRooms() > 0);
            roomRepo.save(room);

            bookingRoom.setNumberOfRooms(newCount);
            bookingRoom.setPrice(room.getPricePerNight() * newCount);
        }

        if (dto.getAdults() != null)
            bookingRoom.setAdults(dto.getAdults());

        if (dto.getChildren() != null)
            bookingRoom.setChildren(dto.getChildren());

        BookingRoom saved = bookingRoomRepo.save(bookingRoom);

        return BookingRoomMapper.toDTO(
                bookingRoomRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
    }

    // ── Delete একটা ─────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {
        BookingRoom bookingRoom = bookingRoomRepo
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "BookingRoom not found with id: " + id
                ));

        // Room availability restore করো
        Room room = bookingRoom.getRoom();
        room.setAvailableRooms(
                room.getAvailableRooms()
                        + bookingRoom.getNumberOfRooms()
        );
        room.setBookedRooms(Math.max(0,
                room.getBookedRooms()
                        - bookingRoom.getNumberOfRooms()
        ));
        room.setIsAvailable(true);
        roomRepo.save(room);

        bookingRoomRepo.deleteById(id);
    }

    // ── Delete Booking এর সব BookingRoom ────────────────────────
    @Override
    @Transactional
    public void deleteAllByBookingId(Long bookingId) {
        // সব BookingRoom খুঁজে Room availability restore করো
        List<BookingRoom> bookingRooms =
                bookingRoomRepo.findByBooking_Id(bookingId);

        bookingRooms.forEach(br -> {
            Room room = br.getRoom();
            if (room != null) {
                room.setAvailableRooms(
                        room.getAvailableRooms()
                                + br.getNumberOfRooms()
                );
                room.setBookedRooms(Math.max(0,
                        room.getBookedRooms()
                                - br.getNumberOfRooms()
                ));
                room.setIsAvailable(true);
                roomRepo.save(room);
            }
        });

        bookingRoomRepo.deleteAllByBookingId(bookingId);
    }
}
