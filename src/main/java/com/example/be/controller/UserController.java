package com.example.be.controller;

import com.example.be.dto.ChangePasswordRequest;
import com.example.be.dto.ErrorResponse;
import com.example.be.dto.MessageResponse;
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

    @PostMapping("/{userId}/change-password")
    public ResponseEntity<?> changePassword(
            @PathVariable Integer userId,
            @RequestBody ChangePasswordRequest request) {
        try {
            usersService.changePassword(userId, request.getCurrentPassword(),
                    request.getNewPassword());
            return ResponseEntity.ok().body(new MessageResponse("Đổi mật khẩu thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse(e.getMessage()));
        }
    }
}