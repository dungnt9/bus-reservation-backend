package com.example.be.dto;

import lombok.Data;

@Data
public class VehicleSeatDTO {
    private Integer vehicleSeatId;
    private Integer vehicleId;
    private String plateNumber;
    private String seatNumber;
}