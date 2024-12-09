package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.model.Users;
import com.example.be.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<Users> findAllNotDeleted() {
        return userRepository.findAllNotDeleted();
    }

    @Transactional
    public Users createUserForDriver(Users user) {
        user.setCreatedAt(LocalDateTime.now());
        user.setUserRole(Users.UserRole.driver); // Ensure user is set as driver
        return userRepository.save(user);
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

        return userRepository.save(existingUser);
    }

    @Transactional
    public void softDeleteUserForDriver(Integer userId) {
        Users user = getUserById(userId);
        user.markAsDeleted();
        userRepository.save(user);
    }

    public Users getUserById(Integer userId) {
        Users user = userRepository.findByIdNotDeleted(userId);
        if (user == null) {
            throw new RuntimeException("User not found or has been deleted");
        }
        return user;
    }
}