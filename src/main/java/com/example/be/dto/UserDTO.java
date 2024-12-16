package com.example.be.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class UserDTO {
    private Integer userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String gender;
    private String address;
    private LocalDate dateOfBirth;
    private String userRole;
}