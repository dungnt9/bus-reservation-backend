package com.example.be.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.be.dto.AuthRequest;
import com.example.be.dto.AuthResponse;
import com.example.be.model.Users;
import com.example.be.repository.AdminsRepository;
import com.example.be.repository.UsersRepository;
import com.example.be.security.JwtUtil;
import com.example.be.dto.UserDTO;

// AuthService.java
@Service
public class AuthService {
    private final UsersRepository usersRepository;
    private final AdminsRepository adminsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(
            UsersRepository usersRepository,
            AdminsRepository adminsRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.usersRepository = usersRepository;
        this.adminsRepository = adminsRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse adminLogin(AuthRequest request) {
        Users user = usersRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!request.getPassword().equals(user.getPassword_hash())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getUserRole().equals(Users.UserRole.admin)) {
            throw new RuntimeException("Access denied: Not an admin");
        }

        if (adminsRepository.findByUserId(user.getUserId()).isEmpty()) {
            throw new RuntimeException("Access denied: User is not an admin");
        }

        String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getUserId(), "ADMIN");

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setMessage("Login successful");
        response.setUser(convertToUserDTO(user));
        return response;
    }

    public AuthResponse userLogin(AuthRequest request) {
        Users user = usersRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!request.getPassword().equals(user.getPassword_hash())) {
            throw new RuntimeException("Invalid credentials");
        }

        // Check if user role is valid for customer website
        if (user.getUserRole().equals(Users.UserRole.admin)) {
            throw new RuntimeException("Invalid user type for this application");
        }

        String role = user.getUserRole().toString().toUpperCase();
        String token = jwtUtil.generateToken(user.getPhoneNumber(), user.getUserId(), role);

        AuthResponse response = new AuthResponse();
        response.setToken(token);
        response.setMessage("Login successful");
        response.setUser(convertToUserDTO(user));
        return response;
    }

    private UserDTO convertToUserDTO(Users user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getUserId());
        userDTO.setFullName(user.getFullName());
        userDTO.setPhoneNumber(user.getPhoneNumber());
        userDTO.setEmail(user.getEmail());
        userDTO.setGender(user.getGender() != null ? user.getGender().toString() : null);
        userDTO.setAddress(user.getAddress());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setUserRole(user.getUserRole().toString());
        return userDTO;
    }
}