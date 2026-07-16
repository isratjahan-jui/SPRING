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

    @Override
    @Transactional
    public void run(String... args) {
        dropUniqueConstraintsOnColumn("payments", "booking_id");
        dropUniqueConstraintsOnColumn("invoices", "booking_id");
        dropUniqueConstraintsOnColumn("invoices", "payment_id");

        generateMissingInvoices();
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

            invoice.setTotalAmount(total);
            invoice.setDiscountAmount(discount);
            invoice.setTaxAmount(tax);
            invoice.setNetAmount(BigDecimal.valueOf(total + tax - discount)
                    .setScale(2, RoundingMode.HALF_UP).doubleValue());
            invoice.setStatus(InvoiceStatus.ISSUED);
            invoice.setIssuedAt(LocalDateTime.now());

            invoiceRepository.save(invoice);
            created++;
        }

        if (created > 0) {
            log.info("Generated {} missing invoices for existing payments", created);
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
}
