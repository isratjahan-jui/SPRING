package com.MHM.MultiHotelManagement.dto.response;

import lombok.Data;
import java.util.Date;
import java.time.LocalDateTime;

@Data
public class HotelOwnerResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String gender;
    private Date dateOfBirth;
    private String image;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
