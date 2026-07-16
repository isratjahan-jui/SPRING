package com.MHM.MultiHotelManagement.controller;

import com.MHM.MultiHotelManagement.dto.request.ForgotPasswordRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.LoginRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.RegisterRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.ResetPasswordRequestDTO;
import com.MHM.MultiHotelManagement.dto.request.ChangePasswordRequestDTO;
import com.MHM.MultiHotelManagement.dto.response.LoginResponseDTO;
import com.MHM.MultiHotelManagement.serviceimplement.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@Valid @RequestBody RegisterRequestDTO dto) {
        authService.register(dto);
        return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email to verify your account."));
    }

    @GetMapping("/verify")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok(Map.of("message", "Email verified successfully"));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequestDTO dto) {
        authService.forgotPassword(dto);
        return ResponseEntity.ok(Map.of("message", "Password reset email sent successfully"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@Valid @RequestBody ResetPasswordRequestDTO dto) {
        authService.resetPassword(dto);
        return ResponseEntity.ok(Map.of("message", "Password reset successfully"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@Valid @RequestBody ChangePasswordRequestDTO dto) {
        authService.changePassword(dto);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }
}
