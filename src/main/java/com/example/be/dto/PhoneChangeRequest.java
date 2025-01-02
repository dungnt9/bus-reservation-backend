package com.example.be.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class PhoneChangeRequest {
    @NotNull(message = "User ID is required")
    private Integer userId;

    @NotBlank(message = "Current phone number is required")
    @Pattern(regexp = "^0\\d{9}$", message = "Invalid phone number format")
    private String currentPhone;

    @NotBlank(message = "New phone number is required")
    @Pattern(regexp = "^0\\d{9}$", message = "Invalid phone number format")
    private String newPhone;

    @NotBlank(message = "Password is required")
    private String password;
}