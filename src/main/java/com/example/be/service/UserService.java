package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.be.model.Users;
import com.example.be.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Users createUser(Users user) {
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAllNotDeleted();
    }

    public Users getUserById(Integer userId) {
        Users user = userRepository.findByIdNotDeleted(userId);
        if (user == null) {
            throw new RuntimeException("User not found or has been deleted");
        }
        return user;
    }

    public Users updateUser(Integer userId, Users userDetails) {
        Users user = getUserById(userId);

        user.setFullName(userDetails.getFullName());
        user.setPhoneNumber(userDetails.getPhoneNumber());
        user.setEmail(userDetails.getEmail());
        user.setGender(userDetails.getGender());
        user.setAddress(userDetails.getAddress());
        user.setDateOfBirth(userDetails.getDateOfBirth());
        user.setUserRole(userDetails.getUserRole());
        user.setUpdatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public void deleteUser(Integer userId) {
        Users user = getUserById(userId);
        user.markAsDeleted();
        userRepository.save(user);
    }
}