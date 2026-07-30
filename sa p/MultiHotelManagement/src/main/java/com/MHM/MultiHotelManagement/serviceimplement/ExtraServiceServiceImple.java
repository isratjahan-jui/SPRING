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
import com.MHM.MultiHotelManagement.util.OwnershipGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExtraServiceServiceImple implements ExtraServiceService {

    private final ExtraServiceRepository extraServiceRepository;
    private final BookingRepository bookingRepository;
    private final OwnershipGuard ownershipGuard;

    public ExtraServiceServiceImple(ExtraServiceRepository extraServiceRepository,
                                    BookingRepository bookingRepository,
                                    OwnershipGuard ownershipGuard) {
        this.extraServiceRepository = extraServiceRepository;
        this.bookingRepository = bookingRepository;
        this.ownershipGuard = ownershipGuard;
    }

    @Override
    @Transactional
    public ExtraServiceResponseDTO createExtraService(ExtraServiceRequestDTO dto) {
        Booking booking = bookingRepository.findById(dto.getBookingId())
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        ownershipGuard.verifyBookingAccess(booking);
        ExtraService service = ExtraServiceMapper.toEntity(dto);
        service.setBooking(booking);
        ExtraService saved = extraServiceRepository.save(service);
        return ExtraServiceMapper.toResponseDTO(saved);
    }

    @Override
    @Transactional
    public ExtraServiceResponseDTO updateExtraService(Long id, ExtraServiceRequestDTO dto) {
        ExtraService existing = extraServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ExtraService not found"));
        ownershipGuard.verifyBookingAccess(existing.getBooking());
        existing.setServiceType(dto.getServiceType());
        existing.setPrice(dto.getPrice());
        if (dto.getServiceStatus() != null) {
            existing.setServiceStatus(ServiceStatus.valueOf(dto.getServiceStatus()));
        }
        ExtraService updated = extraServiceRepository.save(existing);
        return ExtraServiceMapper.toResponseDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public ExtraServiceResponseDTO getExtraServiceById(Long id) {
        ExtraService service = extraServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ExtraService not found"));
        ownershipGuard.verifyBookingAccess(service.getBooking());
        return ExtraServiceMapper.toResponseDTO(service);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExtraServiceResponseDTO> getExtraServicesByBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundException("Booking not found"));
        ownershipGuard.verifyBookingAccess(booking);
        return extraServiceRepository.findByBookingId(bookingId)
                .stream().map(ExtraServiceMapper::toResponseDTO).toList();
    }

    @Override
    @Transactional
    public void deleteExtraService(Long id) {
        ExtraService existing = extraServiceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ExtraService not found"));
        ownershipGuard.verifyBookingAccess(existing.getBooking());
        extraServiceRepository.deleteById(id);
    }
}
