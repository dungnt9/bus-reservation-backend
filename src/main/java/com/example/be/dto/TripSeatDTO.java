package com.example.be.dto;

import java.time.LocalDateTime;
import lombok.Data;
import com.example.be.model.TripSeats;

@Data
public class TripSeatDTO {
    private Integer tripSeatId;
    private String seatNumber;
    private String vehiclePlateNumber;
    private TripSeats.TripSeatStatus status;
}