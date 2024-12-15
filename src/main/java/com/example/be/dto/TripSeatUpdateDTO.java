package com.example.be.dto;
import lombok.Data;

@Data
public class TripSeatUpdateDTO {
    private Integer tripSeatId;
    private String status; // "available" or "booked"
}
