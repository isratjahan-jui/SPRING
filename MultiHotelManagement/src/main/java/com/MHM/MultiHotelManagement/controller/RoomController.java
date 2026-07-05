package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.RoomRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.RoomResponseDTO;
import com.MHM.MultiHotelManagement.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor

public class RoomController {

    private final RoomService roomService;

        //                   Create
    // POST /api/rooms
    @PostMapping(consumes = {"multipart/form-data"})
    public ResponseEntity<RoomResponseDTO> create(
            @RequestPart("data") RoomRequestDTO dto,
            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(roomService.create(dto, image));
    }

          //                 Get All
    // GET /api/rooms
    @GetMapping
    public ResponseEntity<List<RoomResponseDTO>> getAll() {
        return ResponseEntity.ok(roomService.getAll());
    }

         //                       Get by ID
    // GET /api/rooms/1
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponseDTO> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(roomService.getById(id));
    }

    //                     Get by Hotel ID
    // GET /api/rooms/hotel/1
    @GetMapping("/hotel/{hotelId}")
    public ResponseEntity<List<RoomResponseDTO>> getByHotelId(
            @PathVariable Long hotelId
    ) {
        return ResponseEntity.ok(
                roomService.getByHotelId(hotelId)
        );
    }

    //                       Get Available Rooms
    // GET /api/rooms/hotel/1/available
    @GetMapping("/hotel/{hotelId}/available")
    public ResponseEntity<List<RoomResponseDTO>> getAvailable(
            @PathVariable Long hotelId
    ) {
        return ResponseEntity.ok(
                roomService.getAvailableRooms(hotelId)
        );
    }

    //                            Update
    // PUT /api/rooms/1
    @PutMapping(
            value = "/{id}",
            consumes = {"multipart/form-data"}
    )
    public ResponseEntity<RoomResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("data") RoomRequestDTO dto,
            @RequestPart(value = "image", required = false)
            MultipartFile image
    ) {
        return ResponseEntity.ok(
                roomService.update(id, dto, image)
        );
    }

    //                           Delete
    // DELETE /api/rooms/1
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roomService.delete(id);
        return ResponseEntity.noContent().build();
    }

    //                   Update Availability
    // PATCH /api/rooms/1/availability?count=2
    @PatchMapping("/{id}/availability")
    public ResponseEntity<Void> updateAvailability(
            @PathVariable Long id,
            @RequestParam int count
    ) {
        roomService.updateAvailability(id, count);
        return ResponseEntity.ok().build();
    }
}