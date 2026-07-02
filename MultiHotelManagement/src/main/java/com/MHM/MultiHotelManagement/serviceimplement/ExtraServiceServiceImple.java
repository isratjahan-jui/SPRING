package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.ExtraServiceMapper;
import com.MHM.MultiHotelManagement.dto.request.ExtraServiceRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.ExtraServiceResponseDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.ExtraService;
import com.MHM.MultiHotelManagement.enums.ServiceStatus;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.ExtraServiceRepository;
import com.MHM.MultiHotelManagement.service.ExtraServiceService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtraServiceServiceImple implements ExtraServiceService {

    private final ExtraServiceRepository extraServiceRepository;
    private final BookingRepository bookingRepository;

    public ExtraServiceServiceImple(ExtraServiceRepository extraServiceRepository,
                                    BookingRepository bookingRepository) {
        this.extraServiceRepository = extraServiceRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public ExtraServiceResponseDTO createExtraService(ExtraServiceRequestDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        ExtraService service = ExtraServiceMapper.toEntity(dto);
        service.setBooking(booking);
        ExtraService saved = extraServiceRepository.save(service);
        return ExtraServiceMapper.toResponseDTO(saved);
    }

    @Override
    public ExtraServiceResponseDTO updateExtraService(Long id, ExtraServiceRequestDTO dto) {
        ExtraService existing = extraServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ExtraService not found"));
        existing.setServiceType(dto.getServiceType());
        existing.setPrice(dto.getPrice());
        if (dto.getServiceStatus() != null) {
            existing.setServiceStatus(ServiceStatus.valueOf(dto.getServiceStatus()));
        }
        ExtraService updated = extraServiceRepository.save(existing);
        return ExtraServiceMapper.toResponseDTO(updated);
    }

    @Override
    public ExtraServiceResponseDTO getExtraServiceById(Long id) {
        ExtraService service = extraServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ExtraService not found"));
        return ExtraServiceMapper.toResponseDTO(service);
    }

    @Override
    public List<ExtraServiceResponseDTO> getExtraServicesByBooking(Long bookingId) {
        return extraServiceRepository.findByBookingId(bookingId)
                .stream().map(ExtraServiceMapper::toResponseDTO).toList();
    }

    @Override
    public void deleteExtraService(Long id) {
        if (!extraServiceRepository.existsById(id)) {
            throw new EntityNotFoundException("ExtraService not found");
        }
        extraServiceRepository.deleteById(id);
    }
}
