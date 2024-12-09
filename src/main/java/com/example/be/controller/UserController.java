package com.example.be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.Users;
import com.example.be.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<Users> createUser(@Valid @RequestBody Users user) {
        return ResponseEntity.ok(userService.createUserForDriver(user));
    }

    @GetMapping
    public ResponseEntity<List<Users>> getAllUsers() {
        return ResponseEntity.ok(userService.findAllNotDeleted());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Users> getUserById(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Users> updateUser(@PathVariable Integer userId, @Valid @RequestBody Users user) {
        return ResponseEntity.ok(userService.updateUserForDriver(userId, user));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer userId) {
        userService.softDeleteUserForDriver(userId);
        return ResponseEntity.noContent().build();
    }
}