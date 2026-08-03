package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.entity.Booking;
import com.MHM.MultiHotelManagement.entity.Hotel;
import com.MHM.MultiHotelManagement.entity.Invoice;
import com.MHM.MultiHotelManagement.entity.Room;
import com.MHM.MultiHotelManagement.enums.InvoiceType;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class InvoicePdfService {

    private static final Logger log = LoggerFactory.getLogger(InvoicePdfService.class);

    private static final Font TITLE_FONT = new Font(Font.HELVETICA, 22, Font.BOLD, new Color(37, 99, 235));
    private static final Font HEADER_FONT = new Font(Font.HELVETICA, 14, Font.BOLD, new Color(37, 99, 235));
    private static final Font SUBHEADER_FONT = new Font(Font.HELVETICA, 11, Font.BOLD, new Color(55, 65, 81));
    private static final Font LABEL_FONT = new Font(Font.HELVETICA, 9, Font.BOLD, new Color(107, 114, 128));
    private static final Font VALUE_FONT = new Font(Font.HELVETICA, 9, Font.NORMAL, new Color(31, 41, 55));
    private static final Font SMALL_FONT = new Font(Font.HELVETICA, 8, Font.NORMAL, new Color(156, 163, 175));
    private static final Font TOTAL_FONT = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(31, 41, 55));

    public byte[] generateInvoicePdf(Invoice invoice) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4, 40, 40, 40, 40);

        try {
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            addHeader(document, invoice);
            addInvoiceInfo(document, invoice);
            addBookingDetails(document, invoice);
            addPaymentTable(document, invoice);
            addFooter(document, invoice);

            document.close();
        } catch (Exception e) {
            log.error("Failed to generate PDF for invoice {}: {}", invoice.getInvoiceNumber(), e.getMessage(), e);
            throw new RuntimeException("PDF generation failed: " + e.getMessage());
        }

        return baos.toByteArray();
    }

    private void addHeader(Document document, Invoice invoice) throws DocumentException {
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{60, 40});

        PdfPCell leftCell = new PdfPCell();
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.addElement(new Paragraph("INVOICE", TITLE_FONT));
        Paragraph subtitle = new Paragraph("TripNest Hotel Management", new Font(Font.HELVETICA, 10, Font.NORMAL, new Color(107, 114, 128)));
        subtitle.setSpacingAfter(5);
        leftCell.addElement(subtitle);

        PdfPCell rightCell = new PdfPCell();
        rightCell.setBorder(Rectangle.NO_BORDER);
        rightCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        String invType = invoice.getInvoiceType() != null && invoice.getInvoiceType() == InvoiceType.FINAL
                ? "FINAL INVOICE" : "PROFORMA INVOICE";
        Paragraph typePara = new Paragraph(invType, new Font(Font.HELVETICA, 10, Font.BOLD,
                invoice.getInvoiceType() == InvoiceType.FINAL ? new Color(220, 38, 38) : new Color(59, 130, 246)));
        typePara.setAlignment(Element.ALIGN_RIGHT);
        typePara.setSpacingAfter(10);
        rightCell.addElement(typePara);

        String invNum = invoice.getInvoiceNumber() != null ? invoice.getInvoiceNumber() : "N/A";
        Paragraph numPara = new Paragraph(invNum, new Font(Font.HELVETICA, 12, Font.BOLD, new Color(37, 99, 235)));
        numPara.setAlignment(Element.ALIGN_RIGHT);
        numPara.setSpacingAfter(4);
        rightCell.addElement(numPara);

        if (invoice.getIssuedAt() != null) {
            String dateStr = invoice.getIssuedAt().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));
            Paragraph datePara = new Paragraph("Date: " + dateStr, VALUE_FONT);
            datePara.setAlignment(Element.ALIGN_RIGHT);
            datePara.setSpacingAfter(4);
            rightCell.addElement(datePara);
        }

        String status = invoice.getStatus() != null ? invoice.getStatus().name() : "UNKNOWN";
        Paragraph statusPara = new Paragraph("Status: " + status, new Font(Font.HELVETICA, 9, Font.BOLD,
                "PAID".equals(status) ? new Color(22, 163, 74) : new Color(234, 179, 8)));
        statusPara.setAlignment(Element.ALIGN_RIGHT);
        rightCell.addElement(statusPara);

        headerTable.addCell(leftCell);
        headerTable.addCell(rightCell);

        document.add(headerTable);

        PdfPTable lineTable = new PdfPTable(1);
        lineTable.setWidthPercentage(100);
        lineTable.setSpacingBefore(10);
        lineTable.setSpacingAfter(15);
        PdfPCell lineCell = new PdfPCell();
        lineCell.setBorder(Rectangle.BOTTOM);
        lineCell.setBorderColor(new Color(37, 99, 235));
        lineCell.setBorderWidth(2);
        lineCell.setFixedHeight(2);
        lineTable.addCell(lineCell);
        document.add(lineTable);
    }

    private void addInvoiceInfo(Document document, Invoice invoice) throws DocumentException {
        PdfPTable infoTable = new PdfPTable(2);
        infoTable.setWidthPercentage(100);
        infoTable.setWidths(new float[]{50, 50});
        infoTable.setSpacingAfter(15);

        Booking booking = invoice.getBooking();
        Hotel hotel = booking != null ? booking.getHotel() : null;
        Room room = booking != null ? booking.getRoom() : null;

        PdfPCell leftInfo = new PdfPCell();
        leftInfo.setBorder(Rectangle.NO_BORDER);
        leftInfo.addElement(new Paragraph("BILL TO", LABEL_FONT));
        String customerName = invoice.getCustomer() != null ? invoice.getCustomer().getCustomerName() : "N/A";
        Paragraph custName = new Paragraph(customerName, SUBHEADER_FONT);
        custName.setSpacingAfter(3);
        leftInfo.addElement(custName);

        if (invoice.getCustomer() != null && invoice.getCustomer().getUser() != null) {
            String email = invoice.getCustomer().getUser().getEmail();
            if (email != null) {
                leftInfo.addElement(new Paragraph(email, SMALL_FONT));
            }
        }
        if (invoice.getCustomer() != null && invoice.getCustomer().getPhone() != null) {
            leftInfo.addElement(new Paragraph(invoice.getCustomer().getPhone(), SMALL_FONT));
        }

        PdfPCell rightInfo = new PdfPCell();
        rightInfo.setBorder(Rectangle.NO_BORDER);
        rightInfo.addElement(new Paragraph("HOTEL", LABEL_FONT));
        String hotelName = hotel != null ? hotel.getHotelName() : "N/A";
        Paragraph hotelNamePara = new Paragraph(hotelName, SUBHEADER_FONT);
        hotelNamePara.setSpacingAfter(3);
        rightInfo.addElement(hotelNamePara);

        if (hotel != null && hotel.getAddress() != null) {
            rightInfo.addElement(new Paragraph(hotel.getAddress(), SMALL_FONT));
        }
        if (hotel != null && hotel.getLocation() != null) {
            rightInfo.addElement(new Paragraph(hotel.getLocation().getLocationName(), SMALL_FONT));
        }

        infoTable.addCell(leftInfo);
        infoTable.addCell(rightInfo);

        document.add(infoTable);
    }

    private void addBookingDetails(Document document, Invoice invoice) throws DocumentException {
        Booking booking = invoice.getBooking();
        if (booking == null) return;

        PdfPTable detailTable = new PdfPTable(4);
        detailTable.setWidthPercentage(100);
        detailTable.setWidths(new float[]{25, 25, 25, 25});
        detailTable.setSpacingBefore(5);
        detailTable.setSpacingAfter(15);

        addDetailCell(detailTable, "Booking ID", "#" + booking.getId());
        addDetailCell(detailTable, "Room Type", booking.getRoom() != null ? booking.getRoom().getRoomType() : "N/A");
        addDetailCell(detailTable, "Check-In", booking.getCheckInDate() != null
                ? booking.getCheckInDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A");
        addDetailCell(detailTable, "Check-Out", booking.getCheckOutDate() != null
                ? booking.getCheckOutDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "N/A");
        addDetailCell(detailTable, "Rooms", String.valueOf(booking.getNumberOfRooms()));
        addDetailCell(detailTable, "Guests", String.valueOf(booking.getTotalGuests()));
        addDetailCell(detailTable, "Booking Status", booking.getStatus() != null ? booking.getStatus().name() : "N/A");
        if (invoice.getInvoiceType() != null) {
            addDetailCell(detailTable, "Invoice Type", invoice.getInvoiceType().name());
        }

        document.add(detailTable);
    }

    private void addDetailCell(PdfPTable table, String label, String value) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, LABEL_FONT));
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setBorderColor(new Color(229, 231, 235));
        labelCell.setPaddingBottom(6);
        labelCell.setPaddingTop(2);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, VALUE_FONT));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(new Color(229, 231, 235));
        valueCell.setPaddingBottom(6);
        valueCell.setPaddingTop(2);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addPaymentTable(Document document, Invoice invoice) throws DocumentException {
        Paragraph sectionTitle = new Paragraph("PAYMENT SUMMARY", SUBHEADER_FONT);
        sectionTitle.setSpacingBefore(5);
        sectionTitle.setSpacingAfter(8);
        document.add(sectionTitle);

        PdfPTable payTable = new PdfPTable(2);
        payTable.setWidthPercentage(100);
        payTable.setWidths(new float[]{70, 30});

        addPaymentRow(payTable, "Room Charges", formatCurrency(invoice.getTotalAmount()), false);
        addPaymentRow(payTable, "Tax (15% VAT)", formatCurrency(invoice.getTaxAmount()), false);
        addPaymentRow(payTable, "Discount", "-" + formatCurrency(invoice.getDiscountAmount()), false);

        PdfPCell emptyLabel = new PdfPCell(new Phrase("", VALUE_FONT));
        emptyLabel.setBorder(Rectangle.BOTTOM);
        emptyLabel.setBorderColor(new Color(229, 231, 235));

        PdfPCell emptyValue = new PdfPCell(new Phrase("", VALUE_FONT));
        emptyValue.setBorder(Rectangle.BOTTOM);
        emptyValue.setBorderColor(new Color(229, 231, 235));

        payTable.addCell(emptyLabel);
        payTable.addCell(emptyValue);

        addPaymentRow(payTable, "NET AMOUNT", "BDT " + formatCurrency(invoice.getNetAmount()), true);

        document.add(payTable);
    }

    private void addPaymentRow(PdfPTable table, String label, String value, boolean isTotal) {
        Font labelF = isTotal ? TOTAL_FONT : VALUE_FONT;
        Font valueF = isTotal ? TOTAL_FONT : VALUE_FONT;
        Color bgColor = isTotal ? new Color(243, 244, 246) : null;

        PdfPCell labelCell = new PdfPCell(new Phrase(label, labelF));
        labelCell.setBorder(Rectangle.BOTTOM);
        labelCell.setBorderColor(new Color(229, 231, 235));
        labelCell.setPadding(8);
        labelCell.setPaddingTop(4);
        labelCell.setPaddingBottom(4);
        if (bgColor != null) labelCell.setBackgroundColor(bgColor);

        PdfPCell valueCell = new PdfPCell(new Phrase(value, valueF));
        valueCell.setBorder(Rectangle.BOTTOM);
        valueCell.setBorderColor(new Color(229, 231, 235));
        valueCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        valueCell.setPadding(8);
        valueCell.setPaddingTop(4);
        valueCell.setPaddingBottom(4);
        if (bgColor != null) valueCell.setBackgroundColor(bgColor);

        table.addCell(labelCell);
        table.addCell(valueCell);
    }

    private void addFooter(Document document, Invoice invoice) throws DocumentException {
        document.add(new Paragraph(" ", new Font(Font.HELVETICA, 1, Font.NORMAL)));

        PdfPTable footerTable = new PdfPTable(1);
        footerTable.setWidthPercentage(100);
        footerTable.setSpacingBefore(30);

        PdfPCell footerCell = new PdfPCell();
        footerCell.setBorder(Rectangle.TOP);
        footerCell.setBorderColor(new Color(229, 231, 235));
        footerCell.setPaddingTop(10);

        Paragraph thanks = new Paragraph("Thank you for your stay!", new Font(Font.HELVETICA, 10, Font.BOLD, new Color(37, 99, 235)));
        thanks.setAlignment(Element.ALIGN_CENTER);
        thanks.setSpacingAfter(5);
        footerCell.addElement(thanks);

        Paragraph contact = new Paragraph("For any queries, contact hotel support or visit our website.", SMALL_FONT);
        contact.setAlignment(Element.ALIGN_CENTER);
        contact.setSpacingAfter(3);
        footerCell.addElement(contact);

        Paragraph copyright = new Paragraph("TripNest Hotel Management System", SMALL_FONT);
        copyright.setAlignment(Element.ALIGN_CENTER);
        footerCell.addElement(copyright);

        footerTable.addCell(footerCell);
        document.add(footerTable);
    }

    private String formatCurrency(java.math.BigDecimal amount) {
        if (amount == null) return "0.00";
        return String.format("%,.2f", amount);
    }
}
