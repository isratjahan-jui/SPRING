package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ScheduledTasksService {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasksService.class);
    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 0 */6 * * *")
    @Transactional
    public void autoNoShow() {
        Date now = new Date();
        List<Booking> bookings = bookingRepository.findByStatus(BookingStatus.CONFIRMED);
        int marked = 0;
        for (Booking b : bookings) {
            if (b.getCheckInDate() != null && b.getCheckInDate().before(now)) {
                long diffMs = now.getTime() - b.getCheckInDate().getTime();
                long diffHours = TimeUnit.MILLISECONDS.toHours(diffMs);
                if (diffHours >= 24) {
                    b.setStatus(BookingStatus.NO_SHOW);
                    bookingRepository.save(b);
                    marked++;
                }
            }
        }
        if (marked > 0) {
            log.info("Auto no-show: {} bookings marked as NO_SHOW", marked);
        }
    }
}
