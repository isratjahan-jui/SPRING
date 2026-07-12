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
import com.MHM.MultiHotelManagement.repository.CustomerRepository;
import com.MHM.MultiHotelManagement.repository.HotelOwnerRepository;
import com.MHM.MultiHotelManagement.repository.UserRepository;
import com.MHM.MultiHotelManagement.security.JwtUtil;
import com.MHM.MultiHotelManagement.util.EmailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
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

    /**
     * Spring Security authentication manager.
     * Responsible for validating username/password.
     */
    private final AuthenticationManager authenticationManager;

    /**
     * Repository used to retrieve user information.
     */
    private final UserRepository userRepository;

    /**
     * Repository used to retrieve agent information.
     */
    private final HotelOwnerRepository hotelOwnerRepository;

    private final CustomerRepository customerRepository;

    private final JwtUtil jwtUtil;

    private final EmailService emailService;



    private final PasswordEncoder encoder;

    /**
     * Authenticates a user and returns login information
     * along with a JWT token.
     *
     * @param dto Login request containing email and password
     * @return LoginResponseDTO containing token and user details
     */

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO dto){
        // =====================================================
        // STEP 1: Authenticate user credentials
        //
        // Spring Security checks:
        // - User exists
        // - Password matches
        // - Account status (if configured)
        //
        // If authentication fails,
        // AuthenticationException is thrown.
        // =====================================================


        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.getEmail(),
                            dto.getPassword()
                    )
            );
        }
        catch (Exception e) {

            System.out.println("Exception Class = " + e.getClass().getName());
            System.out.println("Exception Message = " + e.getMessage());

            e.printStackTrace();

            throw e;
        }

        // =====================================================
        // STEP 2: Load user from database
        //
        // Since authentication succeeded,
        // retrieve the full user entity.
        // =====================================================
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // =====================================================
        // STEP 3: Generate JWT token
        //
        // Token contains:
        // - User email
        // - User role
        //
        // Example payload:
        // {
        //   "sub": "admin@gmail.com",
        //   "role": "ADMIN",
        //   "iat": ...
        //   "exp": ...
        // }
        // =====================================================
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        // =====================================================
        // STEP 4: Create response DTO
        //
        // This data is returned to frontend after login.
        // =====================================================

        LoginResponseDTO response = new LoginResponseDTO();

        response.setToken(token);
        // Token prefix used in API calls
        response.setTokenType("Bearer");

        // User basic information
        response.setUserId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());

        // User role
        response.setRole(user.getRole().name());

        // =====================================================
        // STEP 5: Special handling for HOTEL_OWNER users



        // =====================================================

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






        // =====================================================
        // STEP 6: Return login response
        //
        // Frontend receives:
        // - JWT Token
        // - User Information
        // - Role
        // - Hub Information (for agents)
        // =====================================================
        return response;



    }

    // ── Register new user ─────────────────────────────────────────
    @Transactional
    public void register(RegisterRequestDTO dto) {

        if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered: " + dto.getEmail());
        }

        Role role;
        try {
            role = Role.valueOf(dto.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid role: " + dto.getRole());
        }

        if (role == Role.ADMIN) {
            throw new RuntimeException("Admin registration is not allowed");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setPhone(dto.getPhone());
        user.setRole(role);
        user.setActive(true);
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
            System.out.println("Warning: Failed to send verification email: " + e.getMessage());
        }
    }

    // ── Send / resend verification email ─────────────────────────
    public void sendVerificationEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));

        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }

        String token = jwtUtil.generateVerificationToken(user.getEmail());

        try {
            emailService.sendVerificationEmail(user.getEmail(), user.getName(), token);
        } catch (MessagingException e) {
            System.out.println("Warning: Failed to send verification email: " + e.getMessage());
        }
    }

    // ── Confirm verification link ─────────────────────────────────
    @Transactional
    public void verifyEmail(String token) {

        if (!jwtUtil.isValidForPurpose(token, "EMAIL_VERIFICATION")) {
            throw new RuntimeException("Invalid or expired verification link");
        }

        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }

        user.setActive(true);
        userRepository.save(user);
    }

    // ── Forgot password — send reset link ────────────────────────
    public void forgotPassword(ForgotPasswordRequestDTO dto) {

        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException(
                        "No account found with email: " + dto.getEmail()));

        String token = jwtUtil.generateResetToken(user.getEmail());

        try {
            emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), token);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send reset email: " + e.getMessage());
        }
    }

    // ── Reset password using token ────────────────────────────────
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {

        if (!jwtUtil.isValidForPurpose(dto.getToken(), "PASSWORD_RESET")) {
            throw new RuntimeException("Invalid or expired reset link");
        }

        if (dto.getNewPassword() == null || dto.getNewPassword().length() < 4) {
            throw new RuntimeException("Password must be at least 4 characters");
        }

        String email = jwtUtil.extractEmail(dto.getToken());

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(encoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }





}
