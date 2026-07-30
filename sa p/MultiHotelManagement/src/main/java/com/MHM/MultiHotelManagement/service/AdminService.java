package com.MHM.MultiHotelManagement.service;

import com.MHM.MultiHotelManagement.dto.AdminDTO;
import com.MHM.MultiHotelManagement.dto.request.AdminRequestDTO;

import java.util.List;

public interface AdminService {
    AdminDTO getProfile(String email);

    AdminDTO getAdminById(Long id);

    AdminDTO findAdminByUserId(Long userId);

    List<AdminDTO> getAllAdmins();

    AdminDTO saveAdmin(AdminRequestDTO dto);

    void deleteAdmin(Long id);

}
