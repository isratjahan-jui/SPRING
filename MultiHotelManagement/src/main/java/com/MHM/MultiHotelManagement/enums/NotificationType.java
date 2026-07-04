package com.MHM.MultiHotelManagement.enums;

public enum NotificationType {

    BOOKING,     // নতুন booking হলে notification
    PAYMENT,     // Payment confirm হলে notification
    CANCEL,      // Booking cancel হলে notification
    INVOICE,     // Invoice issue হলে notification
    REVIEW,      // Customer review দিলে notification
    REPORT,      // Report generate হলে notification
    GENERAL     // সাধারণ notification (system message ইত্যাদি)


}
