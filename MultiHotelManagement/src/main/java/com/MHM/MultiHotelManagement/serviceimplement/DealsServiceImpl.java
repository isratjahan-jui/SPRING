package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.DealsMapper;
import com.MHM.MultiHotelManagement.dto.request.DealsRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.DealsResponseDTO;
import com.MHM.MultiHotelManagement.entity.Deals;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.DealsRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.service.DealsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DealsServiceImpl implements DealsService {

    private final DealsRepository dealsRepository;
    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional
    public DealsResponseDTO createDeal(DealsRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        Room room = null;
        if (dto.getRoomId() != null) {
            room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        }

        Deals deals = new Deals();
        deals.setDealTitle(dto.getDealTitle());
        deals.setDescription(dto.getDescription());
        deals.setDiscountPercent(dto.getDiscountPercent());
        deals.setDiscountAmount(dto.getDiscountAmount());
        deals.setStartDate(dto.getStartDate());
        deals.setEndDate(dto.getEndDate());
        deals.setDealType(dto.getDealType());
        deals.setHotel(hotel);
        deals.setRoom(room);
        deals.setIsActive(true);

        Deals saved = dealsRepository.save(deals);
        return DealsMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public DealsResponseDTO updateDeal(Long id, DealsRequestDTO dto) {
        Deals deals = dealsRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deal not found"));

        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found"));

        Room room = null;
        if (dto.getRoomId() != null) {
            room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
        }

        deals.setDealTitle(dto.getDealTitle());
        deals.setDescription(dto.getDescription());
        deals.setDiscountPercent(dto.getDiscountPercent());
        deals.setDiscountAmount(dto.getDiscountAmount());
        deals.setStartDate(dto.getStartDate());
        deals.setEndDate(dto.getEndDate());
        deals.setDealType(dto.getDealType());
        deals.setHotel(hotel);
        deals.setRoom(room);

        Deals saved = dealsRepository.save(deals);
        return DealsMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteDeal(Long id) {
        dealsRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealsResponseDTO> getDealsByHotel(Long hotelId) {
        return dealsRepository.findByHotel_Id(hotelId)
                .stream()
                .map(DealsMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealsResponseDTO> getDealsByRoom(Long roomId) {
        return dealsRepository.findByRoom_Id(roomId)
                .stream()
                .map(DealsMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealsResponseDTO> searchDeals(String keyword) {
        return dealsRepository.findByDealTitleContainingIgnoreCase(keyword)
                .stream()
                .map(DealsMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DealsResponseDTO> getAllActiveDeals() {
        return dealsRepository.findByIsActive(true)
                .stream()
                .map(DealsMapper::toDTO)
                .collect(Collectors.toList());
    }
}
