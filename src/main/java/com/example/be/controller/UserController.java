package com.example.be.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.be.service.UsersService;
import com.example.be.dto.UserDTO;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UsersService usersService;

    public UserController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserProfile(@PathVariable Integer userId) {
        UserDTO userDTO = usersService.getUserDTOById(userId);
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUserProfile(
            @PathVariable Integer userId,
            @Valid @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = usersService.updateUserProfile(userId, userDTO);
        return ResponseEntity.ok(updatedUser);
    }
}