package com.example.be.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String phoneNumber;
    private String password;
}