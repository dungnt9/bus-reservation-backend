package com.example.be.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RegisterRequest {
    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(regexp = "^0\\d{9}$")
    private String phoneNumber;

    private String email;

    @NotBlank
    @Size(min = 6)
    private String password;

    @NotBlank
    private String gender;

    private String address;

    @NotNull
    private LocalDate dateOfBirth;
}