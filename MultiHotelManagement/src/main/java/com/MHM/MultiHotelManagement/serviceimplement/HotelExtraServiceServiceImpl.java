package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.HotelExtraServiceMapper;
import com.MHM.MultiHotelManagement.dto.request.HotelExtraServiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.HotelExtraServiceResponseDTO;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.HotelExtraService;
import com.MHM.MultiHotelManagement.repository.HotelExtraServiceRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.service.HotelExtraServiceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class HotelExtraServiceServiceImpl implements HotelExtraServiceService {

    private final HotelExtraServiceRepository repository;
    private final HotelRepository hotelRepository;

    public HotelExtraServiceServiceImpl(HotelExtraServiceRepository repository,
                                         HotelRepository hotelRepository) {
        this.repository = repository;
        this.hotelRepository = hotelRepository;
    }

    @Override
    @Transactional
    public HotelExtraServiceResponseDTO create(HotelExtraServiceRequestDTO dto) {
        Hotel hotel = hotelRepository.findById(dto.getHotelId())
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
        HotelExtraService entity = HotelExtraServiceMapper.toEntity(dto);
        entity.setHotel(hotel);
        HotelExtraService saved = repository.save(entity);
        return HotelExtraServiceMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public HotelExtraServiceResponseDTO update(Long id, HotelExtraServiceRequestDTO dto) {
        HotelExtraService entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("HotelExtraService not found"));
        entity.setServiceName(dto.getServiceName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        if (dto.getHotelId() != null) {
            Hotel hotel = hotelRepository.findById(dto.getHotelId())
                    .orElseThrow(() -> new EntityNotFoundException("Hotel not found"));
            entity.setHotel(hotel);
        }
        HotelExtraService updated = repository.save(entity);
        return HotelExtraServiceMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public HotelExtraServiceResponseDTO getById(Long id) {
        HotelExtraService entity = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("HotelExtraService not found"));
        return HotelExtraServiceMapper.toResponseDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelExtraServiceResponseDTO> getByHotel(Long hotelId) {
        return repository.findByHotel_Id(hotelId)
                .stream().map(HotelExtraServiceMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelExtraServiceResponseDTO> getActiveByHotel(Long hotelId) {
        return repository.findByHotel_IdAndIsActiveTrue(hotelId)
                .stream().map(HotelExtraServiceMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("HotelExtraService not found");
        }
        repository.deleteById(id);
    }
}
