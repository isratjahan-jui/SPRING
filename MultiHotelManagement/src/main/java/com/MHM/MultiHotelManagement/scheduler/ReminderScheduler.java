package com.MHM.MultiHotelManagement.scheduler;

import com.MHM.MultiHotelManagement.dto.request.NotificationRequestDTO;
import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.enums.BookingStatus;
import com.MHM.MultiHotelManagement.enums.NotificationChannel;
import com.MHM.MultiHotelManagement.enums.NotificationType;
import com.MHM.MultiHotelManagement.repository.BookingRepository;
import com.MHM.MultiHotelManagement.service.AuditTrailService;
import com.MHM.MultiHotelManagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    private final BookingRepository bookingRepository;
    private final NotificationService notificationService;
    private final AuditTrailService auditTrailService;

    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional
    public void sendCheckInReminders() {
        log.info("Running check-in reminder scheduler");
        try {
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 2);
            Date twoDaysFromNow = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, -2);
            Date startRange = cal.getTime();

            List<Booking> upcomingBookings = bookingRepository
                    .findBookingsByCheckInDateBetween(startRange, twoDaysFromNow);

            for (Booking booking : upcomingBookings) {
                try {
                    if (booking.getStatus() == BookingStatus.CANCELLED) {
                        continue;
                    }
                    String message = buildCheckInReminderMessage(booking);
                    sendNotification(booking, message, "CHECK_IN_REMINDER");
                    log.info("Check-in reminder sent for booking {}", booking.getId());
                    auditTrailService.logAction(
                            "CHECK_IN_REMINDER_SENT",
                            "Booking",
                            booking.getId(),
                            "Check-in reminder sent for booking " + booking.getId(),
                            "SYSTEM"
                    );
                } catch (Exception e) {
                    log.error("Failed to send check-in reminder for booking {}: {}", booking.getId(), e.getMessage());
                    auditTrailService.logAction(
                            "CHECK_IN_REMINDER_FAILED",
                            "Booking",
                            booking.getId(),
                            "Failed to send check-in reminder: " + e.getMessage(),
                            "SYSTEM"
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error in check-in reminder scheduler: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 30 8 * * ?")
    @Transactional
    public void sendCheckOutReminders() {
        log.info("Running check-out reminder scheduler");
        try {
            Date now = new Date();
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_YEAR, 2);
            Date twoDaysFromNow = cal.getTime();
            cal.add(Calendar.DAY_OF_YEAR, -2);
            Date startRange = cal.getTime();

            List<Booking> upcomingBookings = bookingRepository
                    .findBookingsByCheckOutDateBetween(startRange, twoDaysFromNow);

            for (Booking booking : upcomingBookings) {
                try {
                    if (booking.getStatus() == BookingStatus.CANCELLED) {
                        continue;
                    }
                    String message = buildCheckOutReminderMessage(booking);
                    sendNotification(booking, message, "CHECK_OUT_REMINDER");
                    log.info("Check-out reminder sent for booking {}", booking.getId());
                    auditTrailService.logAction(
                            "CHECK_OUT_REMINDER_SENT",
                            "Booking",
                            booking.getId(),
                            "Check-out reminder sent for booking " + booking.getId(),
                            "SYSTEM"
                    );
                } catch (Exception e) {
                    log.error("Failed to send check-out reminder for booking {}: {}", booking.getId(), e.getMessage());
                    auditTrailService.logAction(
                            "CHECK_OUT_REMINDER_FAILED",
                            "Booking",
                            booking.getId(),
                            "Failed to send check-out reminder: " + e.getMessage(),
                            "SYSTEM"
                    );
                }
            }
        } catch (Exception e) {
            log.error("Error in check-out reminder scheduler: {}", e.getMessage(), e);
        }
    }

    private String buildCheckInReminderMessage(Booking booking) {
        return String.format("Your stay at %s starts on %s for %s. Please prepare for your check-in.",
                booking.getHotel() != null ? booking.getHotel().getHotelName() : "the hotel",
                booking.getCheckInDate(),
                booking.getCustomer() != null ? booking.getCustomer().getCustomerName() : "Guest");
    }

    private String buildCheckOutReminderMessage(Booking booking) {
        return String.format("Your stay at %s ends on %s. Please prepare for check-out.",
                booking.getHotel() != null ? booking.getHotel().getHotelName() : "the hotel",
                booking.getCheckOutDate());
    }

    private void sendNotification(Booking booking, String message, String action) {
        Long customerId = booking.getCustomer() != null ? booking.getCustomer().getId() : null;
        if (customerId == null) return;

        NotificationRequestDTO dto = new NotificationRequestDTO();
        dto.setUserId(customerId);
        dto.setType(NotificationType.BOOKING_REMINDER);
        dto.setChannel(NotificationChannel.WEB);
        dto.setSubject("Upcoming " + (action.contains("CHECK_IN") ? "Check-in" : "Check-out") + " Reminder");
        dto.setMessage(message);
        notificationService.createNotification(dto);
    }
}
