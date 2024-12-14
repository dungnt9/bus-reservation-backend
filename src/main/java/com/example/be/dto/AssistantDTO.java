package com.example.be.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class AssistantDTO {
    private Integer assistantId;
    private Integer userId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private String password_hash;
    private String gender;
    private String address;
    private LocalDate dateOfBirth;
    private String assistantStatus;
}