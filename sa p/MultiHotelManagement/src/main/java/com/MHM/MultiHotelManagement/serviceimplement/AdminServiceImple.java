package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.AdminDTO;
import com.MHM.MultiHotelManagement.dto.request.AdminRequestDTO;
import com.MHM.MultiHotelManagement.entity.Admin;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.repository.AdminRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.service.AdminService;
import com.MHM.MultiHotelManagement.service.AuditTrailService;
import com.MHM.MultiHotelManagement.util.OwnershipGuard;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImple implements AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;
    private final AuditTrailService auditTrailService;
    private final OwnershipGuard ownershipGuard;

    public AdminServiceImple(AdminRepository adminRepository, UserRepository userRepository,
                             AuditTrailService auditTrailService, OwnershipGuard ownershipGuard) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.auditTrailService = auditTrailService;
        this.ownershipGuard = ownershipGuard;
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDTO getProfile(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));
        return mapToDTO(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDTO getAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + id));
        return mapToDTO(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminDTO findAdminByUserId(Long userId) {
        Admin admin = adminRepository.findAdminByUser_Id(userId);
        if (admin == null) {
            throw new EntityNotFoundException("Admin not found for user ID: " + userId);
        }
        return mapToDTO(admin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminDTO> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream().map(this::mapToDTO).toList();
    }

    @Override
    @Transactional
    public AdminDTO saveAdmin(AdminRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));

        // The Admin profile row alone doesn't grant admin access — RBAC runs off
        // User.role, so the linked account must be promoted too or they drift apart.
        if (user.getRole() != com.MHM.MultiHotelManagement.enums.Role.ADMIN) {
            user.setRole(com.MHM.MultiHotelManagement.enums.Role.ADMIN);
            userRepository.save(user);
        }

        Admin admin = new Admin();
        admin.setUser(user);
        admin.setName(dto.getName());
        admin.setEmail(dto.getEmail());
        admin.setPhone(dto.getPhone());
        admin.setAddress(dto.getAddress());
        admin.setGender(dto.getGender());
        admin.setDateOfBirth(dto.getDateOfBirth());
        admin.setImage(dto.getImage());

        Admin savedAdmin = adminRepository.save(admin);
        return mapToDTO(savedAdmin);
    }

    @Override
    @Transactional
    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + id));

        // Deleting the sole remaining admin would permanently lock everyone
        // out of every ADMIN-gated endpoint with no recovery path.
        if (adminRepository.count() <= 1) {
            throw new IllegalStateException("Cannot delete the last remaining admin account");
        }

        adminRepository.deleteById(id);

        try {
            auditTrailService.logAction("ADMIN_DELETED", "Admin", id,
                    "Admin account '" + admin.getEmail() + "' deleted",
                    ownershipGuard.getCurrentUser().getEmail());
        } catch (Exception e) {
            // audit failure must not roll back the delete itself
        }
    }

    // Helper method: Entity → DTO
    private AdminDTO mapToDTO(Admin admin) {
        return new AdminDTO(
                admin.getId(),
                admin.getName(),
                admin.getEmail(),
                admin.getPhone(),
                admin.getAddress(),
                admin.getGender(),
                admin.getDateOfBirth(),
                admin.getImage()
        );
    }

}
