package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.AdminDTO;
import com.MHM.MultiHotelManagement.dto.request.AdminRequestDTO;
import com.MHM.MultiHotelManagement.entity.Admin;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.repository.AdminRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.service.AdminService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImple implements AdminService {
    private final AdminRepository adminRepository;
    private final UserRepository userRepository;

    public AdminServiceImple(AdminRepository adminRepository, UserRepository userRepository) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
    }

    @Override
    public AdminDTO getProfile(String email) {
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found with email: " + email));
        return mapToDTO(admin);
    }

    @Override
    public AdminDTO getAdminById(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Admin not found with id: " + id));
        return mapToDTO(admin);
    }

    @Override
    public AdminDTO findAdminByUserId(Long userId) {
        Admin admin = adminRepository.findAdminByUser_Id(userId);
        if (admin == null) {
            throw new EntityNotFoundException("Admin not found for user ID: " + userId);
        }
        return mapToDTO(admin);
    }

    @Override
    public List<AdminDTO> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        return admins.stream().map(this::mapToDTO).toList();
    }

    @Override
    public AdminDTO saveAdmin(AdminRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + dto.getUserId()));

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
    public void deleteAdmin(Long id) {
        if (!adminRepository.existsById(id)) {
            throw new EntityNotFoundException("Admin not found with id: " + id);
        }
        adminRepository.deleteById(id);
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
