package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.response.OwnerDashboardStatsDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.repository.HotelRepository;
import com.MHM.MultiHotelManagement.repository.RoomRepository;
import com.MHM.MultiHotelManagement.service.HotelOwnerDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelOwnerDashboardServiceImpl implements HotelOwnerDashboardService {

    private final HotelRepository hotelRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    @Override
    @Transactional(readOnly = true)
    public OwnerDashboardStatsDTO getDashboardStats(Long ownerId) {
        OwnerDashboardStatsDTO stats = new OwnerDashboardStatsDTO();

        long totalHotels = hotelRepository.findByOwner_IdWithDetails(ownerId).size();
        stats.setTotalHotels(totalHotels);

        Integer totalRooms = roomRepository.countTotalRoomsByOwnerId(ownerId);
        stats.setTotalRooms(totalRooms != null ? totalRooms.longValue() : 0L);

        List<Booking> allBookings = bookingRepository.findAllBookingsByOwnerId(ownerId);

        stats.setTotalBookings((long) allBookings.size());
        stats.setPendingBookings(allBookings.stream().filter(b -> b.getStatus() != null && b.getStatus().name().equals("PENDING")).count());
        stats.setConfirmedBookings(allBookings.stream().filter(b -> b.getStatus() != null && b.getStatus().name().equals("CONFIRMED")).count());
        stats.setCheckedInBookings(allBookings.stream().filter(b -> b.getStatus() != null && b.getStatus().name().equals("CHECKED_IN")).count());
        stats.setCheckedOutBookings(allBookings.stream().filter(b -> b.getStatus() != null && b.getStatus().name().equals("CHECKED_OUT")).count());
        stats.setCancelledBookings(allBookings.stream().filter(b -> b.getStatus() != null && b.getStatus().name().equals("CANCELLED")).count());
        stats.setNoShowBookings(allBookings.stream().filter(b -> b.getStatus() != null && b.getStatus().name().equals("NO_SHOW")).count());

        double totalRevenue = allBookings.stream()
                .filter(b -> b.getStatus() != null && !b.getStatus().name().equals("CANCELLED"))
                .mapToDouble(Booking::getTotalAmount)
                .sum();
        stats.setTotalRevenue(totalRevenue);

        double totalDue = allBookings.stream()
                .filter(b -> b.getStatus() != null && !b.getStatus().name().equals("CANCELLED"))
                .mapToDouble(Booking::getDueAmount)
                .sum();
        stats.setTotalDue(totalDue);

        return stats;
    }
}
