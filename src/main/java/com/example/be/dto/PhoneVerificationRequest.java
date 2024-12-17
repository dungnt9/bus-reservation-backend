package com.example.be.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class PhoneVerificationRequest {
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^0\\d{9}$", message = "Phone number must be 10 digits and start with 0")
    private String phoneNumber;
}