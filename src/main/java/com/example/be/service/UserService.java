package com.example.be.service;

import com.example.be.model.Users;
import com.example.be.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
// import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public Users createUser(Users user) {
        return userRepository.save(user);
    }

    public List<Users> getAllUsers() {
        return userRepository.findAll();
    }

    public Users getUserById(Integer userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
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

        return userRepository.save(user);
    }

    public void deleteUser(Integer userId) {
        Users user = getUserById(userId);
        userRepository.delete(user);
    }
}