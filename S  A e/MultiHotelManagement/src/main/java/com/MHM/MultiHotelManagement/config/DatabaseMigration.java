package com.MHM.MultiHotelManagement.config;

import com.MHM.MultiHotelManagement.entity.*;
import com.MHM.MultiHotelManagement.enums.InvoiceStatus;
import com.MHM.MultiHotelManagement.enums.PaymentStatus;
import com.MHM.MultiHotelManagement.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatabaseMigration implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseMigration.class);

    private final DataSource dataSource;
    private final PaymentRepository paymentRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingRepository bookingRepository;
    private final BookingRoomRepository bookingRoomRepository;

    @Override
    @Transactional
    public void run(String... args) {
        dropUniqueConstraintsOnColumn("payments", "booking_id");
        dropUniqueConstraintsOnColumn("invoices", "booking_id");
        dropUniqueConstraintsOnColumn("invoices", "payment_id");

        addColumnIfNotExists("support_tickets", "escalated", "BOOLEAN DEFAULT FALSE");
        addColumnIfNotExists("support_tickets", "first_response_at", "DATETIME NULL");
        addColumnIfNotExists("support_tickets", "resolved_at", "DATETIME NULL");

        addColumnIfNotExists("commissions", "commission_status", "VARCHAR(20) DEFAULT 'ACTIVE'");

        addColumnIfNotExists("hotels", "payment_type", "VARCHAR(20) DEFAULT 'FULL_ADVANCE'");
        addColumnIfNotExists("hotels", "advance_percentage", "DOUBLE DEFAULT 100.0");
        addColumnIfNotExists("invoices", "invoice_type", "VARCHAR(20) DEFAULT 'PROFORMA'");

        generateMissingInvoices();
        generateMissingBookingRooms();
    }

    private void generateMissingInvoices() {
        List<Payment> paidPayments = paymentRepository.findAll();
        int created = 0;

        for (Payment payment : paidPayments) {
            if (payment.getStatus() != PaymentStatus.PAID) continue;
            if (payment.getBooking() == null) continue;
            if (payment.getBooking().getCustomer() == null) continue;

            boolean alreadyExists = invoiceRepository.existsByBooking_IdAndPayment_Id(
                    payment.getBooking().getId(), payment.getId());
            if (alreadyExists) continue;

            Booking booking = payment.getBooking();

            Invoice invoice = new Invoice();
            invoice.setInvoiceNumber("INV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            invoice.setBooking(booking);
            invoice.setPayment(payment);
            invoice.setCustomer(booking.getCustomer());

            double total = booking.getTotalAmount() != null ? booking.getTotalAmount().doubleValue() : 0;
            double discount = booking.getDiscountRate() != null && booking.getDiscountRate().compareTo(BigDecimal.ZERO) > 0
                    ? BigDecimal.valueOf(total)
                        .multiply(booking.getDiscountRate())
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP)
                        .doubleValue() : 0;
            double tax = BigDecimal.valueOf(total - discount)
                    .multiply(BigDecimal.valueOf(0.15))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();

            invoice.setTotalAmount(BigDecimal.valueOf(total));
            invoice.setDiscountAmount(BigDecimal.valueOf(discount));
            invoice.setTaxAmount(BigDecimal.valueOf(tax));
            invoice.setNetAmount(BigDecimal.valueOf(total + tax - discount)
                    .setScale(2, RoundingMode.HALF_UP));
            invoice.setStatus(InvoiceStatus.ISSUED);
            invoice.setIssuedAt(LocalDateTime.now());

            invoiceRepository.save(invoice);
            created++;
        }

        if (created > 0) {
            log.info("Generated {} missing invoices for existing payments", created);
        }
    }

    private void generateMissingBookingRooms() {
        List<Booking> allBookings = bookingRepository.findAll();
        int created = 0;

        for (Booking booking : allBookings) {
            if (booking.getRoom() == null) continue;
            if (booking.getHotel() == null) continue;

            long existingCount = bookingRoomRepository.countByBookingId(booking.getId());
            if (existingCount > 0) continue;

            BookingRoom bookingRoom = new BookingRoom();
            bookingRoom.setBooking(booking);
            bookingRoom.setRoom(booking.getRoom());
            bookingRoom.setNumberOfRooms(booking.getNumberOfRooms());
            bookingRoom.setAdults(booking.getTotalGuests());
            bookingRoom.setChildren(0);

            double pricePerNight = booking.getRoom().getPricePerNight();
            int nights = 1;
            if (booking.getCheckInDate() != null && booking.getCheckOutDate() != null) {
                long diffMs = booking.getCheckOutDate().getTime() - booking.getCheckInDate().getTime();
                nights = Math.max(1, (int) Math.ceil(diffMs / (1000.0 * 60 * 60 * 24)));
            }
            bookingRoom.setPrice(pricePerNight * booking.getNumberOfRooms());

            bookingRoomRepository.save(bookingRoom);
            created++;
        }

        if (created > 0) {
            log.info("Generated {} missing BookingRoom entries for existing bookings", created);
        }
    }

    private void dropUniqueConstraintsOnColumn(String table, String column) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            List<String> constraints = new ArrayList<>();

            ResultSet rs = meta.getIndexInfo(null, null, table, true, false);
            while (rs.next()) {
                String indexName = rs.getString("INDEX_NAME");
                String colName = rs.getString("COLUMN_NAME");
                boolean nonUnique = rs.getBoolean("NON_UNIQUE");
                if (!nonUnique && column.equals(colName) && indexName != null) {
                    constraints.add(indexName);
                }
            }
            rs.close();

            try (Statement stmt = conn.createStatement()) {
                for (String constraint : constraints) {
                    stmt.execute("ALTER TABLE " + table + " DROP INDEX " + constraint);
                    log.info("Dropped unique constraint: {} on {}.{}", constraint, table, column);
                }
            }
        } catch (Exception e) {
            log.debug("No unique constraints to drop on {}.{}", table, column);
        }
    }

    private void addColumnIfNotExists(String table, String column, String definition) {
        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData meta = conn.getMetaData();
            ResultSet rs = meta.getColumns(null, null, table, column);
            if (!rs.next()) {
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
                    log.info("Added column {} to table {}", column, table);
                }
            }
            rs.close();
        } catch (Exception e) {
            log.debug("Column {} may already exist on {}", column, table);
        }
    }
}
