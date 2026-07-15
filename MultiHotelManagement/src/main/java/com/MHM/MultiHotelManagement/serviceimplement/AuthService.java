package com.MHM.MultiHotelManagement.serviceimplement;


import com.MHM.MultiHotelManagement.dto.request.ForgotPasswordRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.LoginRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.RegisterRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.ResetPasswordRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.LoginResponseDTO;
import com.MHM.MultiHotelManagement.entity.Customer;
import com.MHM.MultiHotelManagement.entity.HotelOwner;
import com.MHM.MultiHotelManagement.entity.User;
import com.MHM.MultiHotelManagement.enums.Role;
import com.MHM.MultiHotelManagement.exception.AlreadyExistsException;
import com.MHM.MultiHotelManagement.exception.BadRequestException;
import com.MHM.MultiHotelManagement.exception.ResourceNotFoundException;
import com.MHM.MultiHotelManagement.exception.UnauthorizedException;
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.HotelOwnerRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.security.JwtUtil;
import com.MHM.MultiHotelManagement.util.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);
    private static final int MIN_PASSWORD_LENGTH = 8;

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final HotelOwnerRepository hotelOwnerRepository;
    private final CustomerRepository customerRepository;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );
        } catch (DisabledException e) {
            throw new UnauthorizedException("Your account is inactive. Please verify your email first.");
        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid email or password");
        }

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(token);
        response.setTokenType("Bearer");
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setRole(user.getRole().name());

        if (user.getRole() == Role.HOTEL_OWNER) {
            var existingOwner = hotelOwnerRepository.findByUser_IdWithUser(user.getId());

            if (existingOwner.isEmpty()) {
                HotelOwner newOwner = new HotelOwner();
                newOwner.setName(user.getName());
                newOwner.setEmail(user.getEmail());
                newOwner.setPhone(user.getPhone());
                newOwner.setUser(user);
                newOwner = hotelOwnerRepository.save(newOwner);
                response.setOwnerId(newOwner.getId());
                response.setOwnerName(newOwner.getName());
            } else {
                HotelOwner owner = existingOwner.get();

                if (owner.getHotels() != null && !owner.getHotels().isEmpty()) {
                    response.setHotelId(owner.getHotels().get(0).getId());
                    response.setHotelName(owner.getHotels().get(0).getHotelName());
                }

                response.setOwnerId(owner.getId());
                response.setOwnerName(owner.getName());
            }
        }

        return response;
    }

    // ── Register new user ─────────────────────────────────────────
    @Transactional
    public void register(RegisterRequestDTO dto) {
        // A.5: Prevent user enumeration - always return success
        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            // Silently return to prevent email enumeration
            return;
        }

        // A.10: Enforce password length
        if (dto.getPassword() == null || dto.getPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new BadRequestException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }

        Role role;
        try {
            role = Role.valueOf(dto.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + dto.getRole());
        }

        if (role == Role.ADMIN) {
            throw new BadRequestException("Admin registration is not allowed");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setRole(role);
        user.setActive(false); // A.6: Account inactive until email verification
        user = userRepository.save(user);

        if (role == Role.CUSTOMER) {
            Customer customer = new Customer();
            customer.setCustomerName(dto.getName());
            customer.setEmail(dto.getEmail());
            customer.setPhone(dto.getPhone());
            customer.setUser(user);
            customerRepository.save(customer);
        } else if (role == Role.HOTEL_OWNER) {
            HotelOwner owner = new HotelOwner();
            owner.setName(dto.getName());
            owner.setEmail(dto.getEmail());
            owner.setPhone(dto.getPhone());
            owner.setUser(user);
            hotelOwnerRepository.save(owner);
        }

        String token = jwtUtil.generateVerificationToken(user.getEmail());

        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getName(), token);
        } catch (MessagingException e) {
            log.warn("Failed to send verification email: {}", e.getMessage());
        }
    }

    // ── Send / resend verification email ─────────────────────────
    public void sendVerificationEmail(String email) {
        // A.5: Return success even if email not found
        userRepository.findByEmail(email).ifPresent(user -> {
            if (!user.isEnabled()) {
                String token = jwtUtil.generateVerificationToken(user.getEmail());
                try {
                    emailService.sendVerificationEmail(user.getEmail(), user.getName(), token);
                } catch (MessagingException e) {
                    log.warn("Failed to send verification email: {}", e.getMessage());
                }
            }
        });
    }

    // ── Confirm verification link ─────────────────────────────────
    @Transactional
    public void verifyEmail(String token) {
        if (!jwtUtil.isValidForPurpose(token, "EMAIL_VERIFICATION")) {
            throw new BadRequestException("Invalid or expired verification link");
        }

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new BadRequestException("Account is already verified");
        }

        user.setActive(true);
        userRepository.save(user);
    }

    // ── Forgot password — send reset link ────────────────────────
    public void forgotPassword(ForgotPasswordRequestDTO dto) {
        // A.5: Return success regardless of whether email exists
        userRepository.findByEmail(dto.getEmail()).ifPresent(user -> {
            String token = jwtUtil.generateResetToken(user.getEmail());
            try {
                emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), token);
            } catch (MessagingException e) {
                log.warn("Failed to send reset email: {}", e.getMessage());
            }
        });
    }

    // ── Reset password using token ────────────────────────────────
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {
        if (!jwtUtil.isValidForPurpose(dto.getToken(), "PASSWORD_RESET")) {
            throw new BadRequestException("Invalid or expired reset link");
        }

        // A.10: Enforce minimum password length
        if (dto.getNewPassword() == null || dto.getNewPassword().length() < MIN_PASSWORD_LENGTH) {
            throw new BadRequestException("Password must be at least " + MIN_PASSWORD_LENGTH + " characters");
        }

        String email = jwtUtil.extractEmail(dto.getToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }
}
