package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.AdminDTO;
import com.MHM.MultiHotelManagement.dto.request.AdminRequestDTO;
import com.MHM.MultiHotelManagement.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ✅ Get admin profile by email
    @GetMapping("/profile/{email}")
    public ResponseEntity<AdminDTO> getProfile(@PathVariable String email) {
        return ResponseEntity.ok(adminService.getProfile(email));
    }

    // ✅ Get admin by ID
    @GetMapping("/{id}")
    public ResponseEntity<AdminDTO> getAdminById(@PathVariable Long id) {
        return ResponseEntity.ok(adminService.getAdminById(id));
    }

    // ✅ Get admin by UserId
    @GetMapping("/user/{userId}")
    public ResponseEntity<AdminDTO> getAdminByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(adminService.findAdminByUserId(userId));
    }

    // ✅ Get all admins
    @GetMapping
    public ResponseEntity<List<AdminDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    // ✅ Save new admin
    @PostMapping
    public ResponseEntity<AdminDTO> saveAdmin(@RequestBody AdminRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.saveAdmin(dto));
    }

    // ✅ Delete admin by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
