package com.MHM.MultiHotelManagement.enums;

public enum Role {


    ADMIN,
    HOTEL_OWNER,
    CUSTOMER;

    // Returns Spring Security compatible authority string
    public String getAuthority() {
        return "ROLE_" + this.name();
    }

    }
