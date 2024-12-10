package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;
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
    public void softDeleteUserForDriver(Integer userId) {
        Users user = getUserById(userId);
        user.markAsDeleted();
        usersRepository.save(user);
    }

    @Transactional
    public void softDeleteUserForCustomer(Integer userId) {
        Users user = getUserById(userId);
        user.markAsDeleted();
        usersRepository.save(user);
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
}