package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.HotelMapper;
import com.MHM.MultiHotelManagement.dto.request.HotelRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.entity.Location;
import com.MHM.MultiHotelManagement.enums.HotelStatus;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.HotelOwnerRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.LocationRepository;
import com.MHM.MultiHotelManagement.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepo;
    private final HotelOwnerRepository ownerRepo;
    private final LocationRepository locationRepo;

    @Override
    @Transactional
    public HotelResponseDTO createHotel(HotelRequestDTO dto) {
        Hotel hotel = HotelMapper.toEntity(dto);

        HotelOwner owner = ownerRepo.findById(dto.getOwnerId())
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found with id: " + dto.getOwnerId()));
        hotel.setOwner(owner);

        Location location = locationRepo.findById(dto.getLocationId())
                .orElseThrow(() -> new ResourceNotFoundException("Location not found with id: " + dto.getLocationId()));
        hotel.setLocation(location);
        hotel.setStatus(HotelStatus.valueOf(dto.getStatus()));

        return HotelMapper.toDTO(hotelRepo.save(hotel));
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
    @Transactional
    public HotelResponseDTO updateHotel(Long id, HotelRequestDTO dto) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));

        hotel.setHotelName(dto.getHotelName());
        hotel.setAddress(dto.getAddress());
        hotel.setDescription(dto.getDescription());
        hotel.setRating(dto.getRating());
        hotel.setImage(dto.getImage());
        hotel.setStatus(HotelStatus.valueOf(dto.getStatus()));
        return HotelMapper.toDTO(hotelRepo.save(hotel));
    }

    @Override
    @Transactional
    public void deleteHotel(Long id) {
        Hotel hotel = hotelRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with id: " + id));
        hotelRepo.delete(hotel);
    }
}
