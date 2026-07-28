package com.MHM.MultiHotelManagement.serviceimplement;

import com.MHM.MultiHotelManagement.dto.mapper.CustomerMapperDTO;
import com.MHM.MultiHotelManagement.dto.request.CustomerRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.CustomerResponseDTO;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.enums.Role;
import com.MHM.MultiHotelManagement.exception.AlreadyExistsException;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;

    @Value("${image.upload.dir:uploads}")
    private String uploadDir;

    // ── Create ───────────────────────────────────────────────────
    @Override
    @Transactional
    public CustomerResponseDTO create(
            CustomerRequestDTO dto,
            MultipartFile image
    ) {
        // Email আগে থেকে আছে কিনা check (User table এ)
        if (userRepo.existsByEmail(dto.getEmail())) {
            throw new AlreadyExistsException(
                    "Email already registered: " + dto.getEmail()
            );
        }

        // Phone আগে থেকে আছে কিনা check (User table এ)
        if (dto.getPhone() != null && !dto.getPhone().isBlank()
                && userRepo.existsByPhone(dto.getPhone())) {
            throw new AlreadyExistsException(
                    "Phone number already registered: " + dto.getPhone()
            );
        }

        // 1. User account তৈরি করো (login credentials)
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setRole(Role.CUSTOMER);
        user.setActive(true);

        User savedUser = userRepo.save(user);

        // 2. Customer profile তৈরি করো
        Customer customer = new Customer();
        customer.setUser(savedUser);
        customer.setCustomerName(dto.getCustomerName());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());
        customer.setGender(dto.getGender());

        // Date of birth parse করো — LocalDate ব্যবহার করে
        if (dto.getDateOfBirth() != null
                && !dto.getDateOfBirth().isBlank()) {
            try {
                customer.setDateOfBirth(
                        LocalDate.parse(dto.getDateOfBirth())
                );
            } catch (DateTimeParseException e) {
                throw new BadRequestException(
                        "Invalid date format. Use yyyy-MM-dd"
                );
            }
        }

        // Image upload
        if (image != null && !image.isEmpty()) {
            customer.setImage(
                    uploadImage(image, dto.getCustomerName())
            );
        }

        Customer saved = customerRepo.save(customer);

        return CustomerMapperDTO.toDTO(
                customerRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
    }

    // ── Get All ──────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public List<CustomerResponseDTO> getAll() {
        return customerRepo.findAllWithDetails()
                .stream()
                .map(CustomerMapperDTO::toDTO)
                .collect(Collectors.toList());
    }

    // ── Get by ID ────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getById(Long id) {
        Customer customer = customerRepo.findByIdWithDetails(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + id
                ));
        return CustomerMapperDTO.toDTO(customer);
    }

    // ── Get by User ID ───────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public CustomerResponseDTO getByUserId(Long userId) {
        Customer customer = customerRepo
                .findCustomerByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found for user id: " + userId
                ));
        return CustomerMapperDTO.toDTO(customer);
    }

    // ── Update ───────────────────────────────────────────────────
    @Transactional
    @Override
    public CustomerResponseDTO update(
            Long id,
            CustomerRequestDTO dto,
            MultipartFile image
    ) {
        Customer customer = customerRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Customer not found with id: " + id
                ));

        // User fields update (শুধু null না হলে)
        User user = customer.getUser();
        if (dto.getName() != null) user.setName(dto.getName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        userRepo.save(user);

        // Customer profile fields update
        if (dto.getCustomerName() != null)
            customer.setCustomerName(dto.getCustomerName());

        if (dto.getAddress() != null)
            customer.setAddress(dto.getAddress());

        if (dto.getGender() != null)
            customer.setGender(dto.getGender());

        if (dto.getPhone() != null)
            customer.setPhone(dto.getPhone());

        if (dto.getDateOfBirth() != null
                && !dto.getDateOfBirth().isBlank()) {
            try {
                customer.setDateOfBirth(
                        LocalDate.parse(dto.getDateOfBirth())
                );
            } catch (DateTimeParseException e) {
                throw new BadRequestException(
                        "Invalid date format. Use yyyy-MM-dd"
                );
            }
        }

        if (image != null && !image.isEmpty()) {
            customer.setImage(
                    uploadImage(image, user.getName())
            );
        }

        Customer saved = customerRepo.save(customer);

        return CustomerMapperDTO.toDTO(
                customerRepo.findByIdWithDetails(saved.getId())
                        .orElse(saved)
        );
    }

    // ── Delete ───────────────────────────────────────────────────
    @Override
    @Transactional
    public void delete(Long id) {
        if (!customerRepo.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Customer not found with id: " + id
            );
        }
        customerRepo.deleteById(id);
    }

    // ── Image Upload Helper ──────────────────────────────────────
    private String uploadImage(MultipartFile file, String name) {
        try {
            Path path = Paths.get(uploadDir, "customer");
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }

            String ext = "";
            String original = file.getOriginalFilename();
            if (original != null && original.contains(".")) {
                ext = original.substring(
                        original.lastIndexOf(".")
                );
            }

            String fileName = name.trim()
                    .replaceAll("\\s+", "_")
                    + "_" + UUID.randomUUID() + ext;

            Files.copy(
                    file.getInputStream(),
                    path.resolve(fileName)
            );

            return fileName;
        } catch (Exception e) {
            throw new BadRequestException(
                    "Image upload failed: " + e.getMessage()
            );
        }
    }
}