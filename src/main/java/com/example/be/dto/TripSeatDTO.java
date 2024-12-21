package com.example.be.dto;

import lombok.Data;

@Data
public class TripSeatDTO {
    private Integer tripSeatId;
    private String seatNumber;
    private String vehiclePlateNumber;
    private String status; // Thay vì TripSeats.TripSeatStatus
}