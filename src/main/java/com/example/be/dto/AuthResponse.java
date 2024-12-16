package com.example.be.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private String message;
    private UserDTO user;
}