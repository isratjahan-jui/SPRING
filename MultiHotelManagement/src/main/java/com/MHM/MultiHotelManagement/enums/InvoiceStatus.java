package com.MHM.MultiHotelManagement.enums;

public enum InvoiceStatus {
    ISSUED,     // Invoice তৈরি হয়েছে কিন্তু এখনো পরিশোধ হয়নি
    PAID,       // Invoice সম্পূর্ণ পরিশোধ হয়েছে
    CANCELLED   // Invoice বাতিল করা হয়েছে
}
