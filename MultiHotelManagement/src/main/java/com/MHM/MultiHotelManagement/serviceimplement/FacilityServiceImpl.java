package com.MHM.MultiHotelManagement.serviceImpl;

import com.MHM.MultiHotelManagement.dto.mapper.FacilityMapper;
import com.MHM.MultiHotelManagement.dto.request.FacilityRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.FacilityResponseDTO;
import com.MHM.MultiHotelManagement.entity.Facility;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.exception.AlreadyExistsException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.FacilityRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.service.FacilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FacilityServiceImpl implements FacilityService {

    private final FacilityRepository facilityRepo;
    private final HotelRepository hotelRepo;

    // ── Create একটা Facility ─────────────────────────────────────
    @Override
    @Transactional
    public FacilityResponseDTO create(FacilityRequestDTO dto) {

        // Hotel আছে কিনা check
        Hotel hotel = hotelRepo.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with id: "
                                + dto.getHotelId()
                ));

        // এই hotel এ এই facility already আছে কিনা check
        if (facilityRepo.existsByHotel_IdAndFacilityName(
                dto.getHotelId(), dto.getFacilityName())) {
            throw new AlreadyExistsException(
                    "Facility '" + dto.getFacilityName()
                            + "' already exists for this hotel"
            );
        }

        // Entity তৈরি করো
        Facility facility = FacilityMapper.toEntity(dto);
        facility.setHotel(hotel);

        Facility saved = facilityRepo.save(facility);

        return FacilityMapper.toDTO(
                facilityRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
    }

    // ── Create অনেকগুলো Facility একসাথে ─────────────────────────
    @Override
    @Transactional
    public List<FacilityResponseDTO> createBulk(
            Long hotelId,
            List<FacilityRequestDTO> dtoList
    ) {
        // Hotel আছে কিনা check
        Hotel hotel = hotelRepo.findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Hotel not found with id: " + hotelId
                ));

        List<FacilityResponseDTO> results = new ArrayList<>();

        for (FacilityRequestDTO dto : dtoList) {

            // Already exist করলে skip করো
            if (facilityRepo.existsByHotel_IdAndFacilityName(
                    hotelId, dto.getFacilityName())) {
                continue;
            }

            Facility facility = FacilityMapper.toEntity(dto);
            facility.setHotel(hotel);
            Facility saved = facilityRepo.save(facility);
            results.add(FacilityMapper.toDTO(saved));
        }

        return results;
    }

    // ── Get All ──────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<FacilityResponseDTO> getAll() {
        return facilityRepo.findAllWithDetails()
                .stream()
                .map(FacilityMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get by ID ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public FacilityResponseDTO getById(Long id) {
        Facility facility = facilityRepo
                .findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facility not found with id: " + id
                ));
        return FacilityMapper.toDTO(facility);
    }

    // ── Get by Hotel ID ──────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<FacilityResponseDTO> getByHotelId(Long hotelId) {
        return facilityRepo.findByHotelIdWithDetails(hotelId)
                .stream()
                .map(FacilityMapper::toDTO)
                .collect(Collectors.toList());
    }

    // ── Update ───────────────────────────────────────────────────
    @Override
    @Transactional
    public FacilityResponseDTO update(
            Long id,
            FacilityRequestDTO dto
    ) {
        Facility facility = facilityRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Facility not found with id: " + id
                ));

        // শুধু null না হলে update করো
        if (dto.getFacilityName() != null)
            facility.setFacilityName(dto.getFacilityName());

        if (dto.getDescription() != null)
            facility.setDescription(dto.getDescription());

        Facility saved = facilityRepo.save(facility);

        return FacilityMapper.toDTO(
                facilityRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
    }

    // ── Delete একটা ─────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {
        if (!facilityRepo.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Facility not found with id: " + id
            );
        }
        facilityRepo.deleteById(id);
    }

    // ── Delete Hotel এর সব Facility ─────────────────────────────
    @Override
    @Transactional
    public void deleteAllByHotelId(Long hotelId) {
        if (!hotelRepo.existsById(hotelId)) {
            throw new ResourceNotFoundException(
                    "Hotel not found with id: " + hotelId
            );
        }
        facilityRepo.deleteAllByHotelId(hotelId);
    }
}