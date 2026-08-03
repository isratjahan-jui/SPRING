package com.MHM.MultiHotelManagement.enums;

public enum NotificationType {


    BOOKING_CONFIRMED,     // Booking confirm হলে
    BOOKING_CANCELLED,     // Booking cancel হলে
    BOOKING_REMINDER,      // Check-in এর আগে reminder

    PAYMENT_SUCCESSFUL,    // Payment সফল হলে
    PAYMENT_FAILED,        // Payment fail হলে
    PAYMENT_REFUNDED,      // Refund হলে

    HOTEL_APPROVED,        // Admin hotel approve করলে
    HOTEL_REJECTED,        // Admin hotel reject করলে

    REVIEW_RECEIVED,       // Hotel এ নতুন review আসলে
    SUPPORT_REPLIED,       // Support ticket এ reply আসলে

    GENERAL,               // সাধারণ notification
    PROMOTIONAL            // Offer/Deal notification



}
