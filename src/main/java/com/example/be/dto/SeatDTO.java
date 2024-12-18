package com.example.be.dto;

import lombok.Data;

@Data
public class SeatDTO {
    private Integer seatId;
    private String seatNumber;
    private String status; // "available" or "booked"
}