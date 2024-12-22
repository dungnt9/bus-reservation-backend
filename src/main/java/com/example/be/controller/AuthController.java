package com.example.be.controller;

import com.example.be.dto.*;
import com.example.be.model.Customers;
import com.example.be.model.Users;
import com.example.be.repository.CustomersRepository;
import com.example.be.repository.UsersRepository;
import com.example.be.security.JwtUtil;
import com.example.be.service.AuthService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final UsersRepository usersRepository;
    private final CustomersRepository customersRepository;
    private final JwtUtil jwtUtil;

    // In-memory OTP storage
    private final Map<String, OTPData> otpStorage = new ConcurrentHashMap<>();

    public AuthController(
            AuthService authService,
            UsersRepository usersRepository,
            CustomersRepository customersRepository,
            JwtUtil jwtUtil) {
        this.authService = authService;
        this.usersRepository = usersRepository;
        this.customersRepository = customersRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.adminLogin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/user-login")
    public ResponseEntity<?> userLogin(@RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.userLogin(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse(e.getMessage()));
        }
    }

    @PostMapping("/verify-phone")
    public ResponseEntity<?> verifyPhone(@Valid @RequestBody PhoneVerificationRequest request) {
        try {
            // Check if phone exists
            if (usersRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Phone number already registered"));
            }

            // Generate OTP
            String otp = String.format("%06d", new Random().nextInt(1000000));

            // Store OTP with timestamp
            otpStorage.put(request.getPhoneNumber(), new OTPData(otp, System.currentTimeMillis(), 0));

            // For development - print OTP to console
            System.out.println("OTP for " + request.getPhoneNumber() + ": " + otp);

            return ResponseEntity.ok(new MessageResponse("OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOTP(@Valid @RequestBody OTPVerificationRequest request) {
        OTPData otpData = otpStorage.get(request.getPhoneNumber());
        if (otpData == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("OTP expired or not found"));
        }

        // Check expiration (5 minutes)
        if (System.currentTimeMillis() - otpData.timestamp > 300000) {
            otpStorage.remove(request.getPhoneNumber());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("OTP expired"));
        }

        // Check attempts
        if (otpData.attempts >= 3) {
            otpStorage.remove(request.getPhoneNumber());
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Too many failed attempts"));
        }

        if (!otpData.code.equals(request.getOtp())) {
            otpData.attempts++;
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid OTP"));
        }

        // Clear OTP after successful verification
        otpStorage.remove(request.getPhoneNumber());
        return ResponseEntity.ok(new MessageResponse("OTP verified successfully"));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody CustomerDTO request) {
        try {
            // Create user
            Users user = new Users();
            user.setFullName(request.getFullName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setEmail(request.getEmail());
            user.setPassword_hash(request.getPassword_hash());
            user.setGender(Users.Gender.valueOf(request.getGender().toLowerCase()));
            user.setAddress(request.getAddress());
            user.setDateOfBirth(request.getDateOfBirth());
            user.setUserRole(Users.UserRole.customer);
            user.setCreatedAt(LocalDateTime.now());

            Users savedUser = usersRepository.save(user);

            // Create customer record
            Customers customer = new Customers();
            customer.setUser(savedUser);
            customer.setCreatedAt(LocalDateTime.now());
            customersRepository.save(customer);

            return ResponseEntity.ok(new MessageResponse("Registration successful"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Registration failed: " + e.getMessage()));
        }
    }

    // Inner class for OTP data
    @Data
    @AllArgsConstructor
    private static class OTPData {
        private String code;
        private long timestamp;
        private int attempts;
    }

    // Quên mật khẩu
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody PhoneVerificationRequest request) {
        try {
            // Check if phone exists
            Optional<Users> userOpt = usersRepository.findByPhoneNumber(request.getPhoneNumber());
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new MessageResponse("Phone number not found"));
            }

            // Generate OTP
            String otp = String.format("%06d", new Random().nextInt(1000000));
            otpStorage.put(request.getPhoneNumber(), new OTPData(otp, System.currentTimeMillis(), 0));

            // For development - print OTP to console
            System.out.println("Password Reset OTP for " + request.getPhoneNumber() + ": " + otp);

            return ResponseEntity.ok(new MessageResponse("OTP sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        OTPData otpData = otpStorage.get(request.getPhoneNumber());
        if (otpData == null) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("OTP expired or not found"));
        }

        // Validate OTP
        if (!otpData.getCode().equals(request.getOtp())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Invalid OTP"));
        }

        // Update password
        Optional<Users> userOpt = usersRepository.findByPhoneNumber(request.getPhoneNumber());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("User not found"));
        }

        Users user = userOpt.get();
        user.setPassword_hash(request.getNewPassword());
        user.setUpdatedAt(LocalDateTime.now());
        usersRepository.save(user);

        // Clear OTP after successful password reset
        otpStorage.remove(request.getPhoneNumber());

        return ResponseEntity.ok(new MessageResponse("Password reset successful"));
    }
}