package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import com.example.be.dto.UserDTO;
import org.springframework.util.StringUtils;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.model.Users;
import com.example.be.repository.UsersRepository;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    // Constructor injection
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public List<Users> findAllNotDeleted() {
        return usersRepository.findAllNotDeleted();
    }

    @Transactional
    public Users createUserForDriver(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Validate required fields
        validateUserFields(user);

        user.setCreatedAt(LocalDateTime.now());
        user.setUserRole(Users.UserRole.driver); // Ensure user is set as driver
        return usersRepository.save(user);
    }

    @Transactional
    public Users createUserForCustomer(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Validate required fields
        validateUserFields(user);

        user.setCreatedAt(LocalDateTime.now());
        user.setUserRole(Users.UserRole.customer); // Ensure user is set as customer
        return usersRepository.save(user);
    }

    @Transactional
    public Users createUserForAssistant(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Validate required fields
        validateUserFields(user);

        user.setCreatedAt(LocalDateTime.now());
        user.setUserRole(Users.UserRole.assistant); // Ensure user is set as assistant
        return usersRepository.save(user);
    }

    @Transactional
    public Users createUserForAdmin(Users user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null");
        }

        // Validate required fields
        validateUserFields(user);

        user.setCreatedAt(LocalDateTime.now());
        user.setUserRole(Users.UserRole.admin); // Ensure user is set as admin
        return usersRepository.save(user);
    }

    @Transactional
    public Users updateUserForDriver(Integer userId, Users userDetails) {
        Users existingUser = getUserById(userId);

        // Update only specific fields for driver-related user
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setGender(userDetails.getGender());
        existingUser.setAddress(userDetails.getAddress());
        existingUser.setDateOfBirth(userDetails.getDateOfBirth());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return usersRepository.save(existingUser);
    }

    @Transactional
    public Users updateUserForCustomer(Integer userId, Users userDetails) {
        Users existingUser = getUserById(userId);

        // Update only specific fields for customer-related user
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setGender(userDetails.getGender());
        existingUser.setAddress(userDetails.getAddress());
        existingUser.setDateOfBirth(userDetails.getDateOfBirth());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return usersRepository.save(existingUser);
    }

    @Transactional
    public Users updateUserForAssistant(Integer userId, Users userDetails) {
        Users existingUser = getUserById(userId);

        // Update only specific fields for assistant-related user
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setGender(userDetails.getGender());
        existingUser.setAddress(userDetails.getAddress());
        existingUser.setDateOfBirth(userDetails.getDateOfBirth());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return usersRepository.save(existingUser);
    }

    @Transactional
    public Users updateUserForAdmin(Integer userId, Users userDetails) {
        Users existingUser = getUserById(userId);

        // Update only specific fields for admin-related user
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setGender(userDetails.getGender());
        existingUser.setAddress(userDetails.getAddress());
        existingUser.setDateOfBirth(userDetails.getDateOfBirth());
        existingUser.setUpdatedAt(LocalDateTime.now());

        return usersRepository.save(existingUser);
    }

    public Users getUserById(Integer userId) {
        Users user = usersRepository.findByIdNotDeleted(userId);
        if (user == null) {
            throw new RuntimeException("User not found or has been deleted");
        }
        return user;
    }

    // Added validation method
    private void validateUserFields(Users user) {
        if (!StringUtils.hasText(user.getFullName())) {
            throw new IllegalArgumentException("Full name is required");
        }
        if (!StringUtils.hasText(user.getPhoneNumber())) {
            throw new IllegalArgumentException("Phone number is required");
        }
    }

    public UserDTO getUserDTOById(Integer userId) {
        Users user = getUserById(userId);
        return convertToDTO(user);
    }

    private UserDTO convertToDTO(Users user) {
        UserDTO dto = new UserDTO();
        dto.setUserId(user.getUserId());
        dto.setFullName(user.getFullName());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setEmail(user.getEmail());
        dto.setGender(user.getGender() != null ? user.getGender().toString() : null);
        dto.setAddress(user.getAddress());
        dto.setDateOfBirth(user.getDateOfBirth());
        dto.setUserRole(user.getUserRole().toString());
        return dto;
    }

    public UserDTO updateUserProfile(Integer userId, UserDTO userDTO) {
        Users existingUser = getUserById(userId);

        // Update fields
        if (userDTO.getFullName() != null) {
            existingUser.setFullName(userDTO.getFullName());
        }
        if (userDTO.getEmail() != null) {
            existingUser.setEmail(userDTO.getEmail());
        }
        if (userDTO.getGender() != null) {
            existingUser.setGender(Users.Gender.valueOf(userDTO.getGender().toLowerCase()));
        }
        if (userDTO.getAddress() != null) {
            existingUser.setAddress(userDTO.getAddress());
        }
        if (userDTO.getDateOfBirth() != null) {
            existingUser.setDateOfBirth(userDTO.getDateOfBirth());
        }

        existingUser.setUpdatedAt(LocalDateTime.now());
        Users savedUser = usersRepository.save(existingUser);
        return convertToDTO(savedUser);
    }

    @Transactional
    public void changePassword(Integer userId, String currentPassword, String newPassword) {
        Users user = getUserById(userId);

        // Validate current password
        if (!user.getPassword_hash().equals(currentPassword)) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác");
        }

        // Update new password
        user.setPassword_hash(newPassword);
        user.setUpdatedAt(LocalDateTime.now());
        usersRepository.save(user);
    }
}