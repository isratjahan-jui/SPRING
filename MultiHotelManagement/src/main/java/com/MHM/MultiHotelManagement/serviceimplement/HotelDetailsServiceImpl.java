package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.HotelDetailsMapper;
import com.MHM.MultiHotelManagement.dto.request.HotelDetailsRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelDetailsResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.HotelDetails;
import com.MHM.MultiHotelManagement.exception.AlreadyExistsException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.HotelDetailsRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.service.HotelDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HotelDetailsServiceImpl implements HotelDetailsService {

    private final HotelDetailsRepository hotelDetailsRepo;
    private final HotelRepository hotelRepo;

    // ── Create ───────────────────────────────────────────────
    @Override
    @Transactional
    public HotelDetailsResponseDTO createHotelDetails(HotelDetailsRequestDTO dto) {
        Hotel hotel = hotelRepo.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + dto.getHotelId()));

        if (hotelDetailsRepo.existsByHotel_Id(dto.getHotelId())) {
            throw new AlreadyExistsException("HotelDetails already exists for hotel id: " + dto.getHotelId());
        }

        HotelDetails details = HotelDetailsMapper.toEntity(dto);
        details.setHotel(hotel);

        HotelDetails saved = hotelDetailsRepo.save(details);
        return HotelDetailsMapper.toDTO(saved);
    }

    // ── Get by Hotel ID ──────────────────────────────────────


    @Override
    @Transactional(readOnly = true)
    public HotelDetailsResponseDTO getHotelDetailsByHotelId(Long hotelId) {
        HotelDetails details = hotelDetailsRepo.findByHotel_Id(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "HotelDetails not found for hotel id: " + hotelId
                ));

        return HotelDetailsMapper.toDTO(details);
    }

    //    public HotelDetailsResponseDTO getHotelDetailsByHotelId(Long hotelId) {
//        HotelDetails details = hotelDetailsRepo.findByHotelIdWithDetails(hotelId)
//                .orElseThrow(() -> new ResourceNotFoundException("HotelDetails not found for hotel id: " + hotelId));
//        return HotelDetailsMapper.toDTO(details);
//
//
//
//    }



    // ── Get by ID ────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public HotelDetailsResponseDTO getHotelDetailsById(Long id) {
        HotelDetails details = hotelDetailsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HotelDetails not found with id: " + id));
        return HotelDetailsMapper.toDTO(details);
    }

    // ── Update (Partial Update) ──────────────────────────────
    @Override
    @Transactional
    public HotelDetailsResponseDTO updateHotelDetails(Long id, HotelDetailsRequestDTO dto) {
        HotelDetails details = hotelDetailsRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HotelDetails not found with id: " + id));

        if (dto.getOwnerSpeach() != null) details.setOwnerSpeach(dto.getOwnerSpeach());
        if (dto.getDescription() != null) details.setDescription(dto.getDescription());
        if (dto.getHotelPolicy() != null) details.setHotelPolicy(dto.getHotelPolicy());
        if (dto.getCheckInTime() != null) details.setCheckInTime(dto.getCheckInTime());
        if (dto.getCheckOutTime() != null) details.setCheckOutTime(dto.getCheckOutTime());
        if (dto.getContactEmail() != null) details.setContactEmail(dto.getContactEmail());
        if (dto.getContactPhone() != null) details.setContactPhone(dto.getContactPhone());
        if (dto.getCancellationPolicy() != null) details.setCancellationPolicy(dto.getCancellationPolicy());
        if (dto.getPetPolicy() != null) details.setPetPolicy(dto.getPetPolicy());
        if (dto.getSmokingPolicy() != null) details.setSmokingPolicy(dto.getSmokingPolicy());
        if (dto.getChildPolicy() != null) details.setChildPolicy(dto.getChildPolicy());
        if (dto.getLanguages() != null) details.setLanguages(dto.getLanguages());
        if (dto.getNearbyAttractions() != null) details.setNearbyAttractions(dto.getNearbyAttractions());

        HotelDetails updated = hotelDetailsRepo.save(details);
        return HotelDetailsMapper.toDTO(updated);
    }

    // ── Delete ───────────────────────────────────────────────
    @Override
    @Transactional
    public void deleteHotelDetails(Long id) {
        if (!hotelDetailsRepo.existsById(id)) {
            throw new ResourceNotFoundException("HotelDetails not found with id: " + id);
        }
        hotelDetailsRepo.deleteById(id);
    }
}
