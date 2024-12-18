package com.example.be.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TripSearchDTO {
    private Integer tripId;
    private String routeName;
    private LocalDateTime scheduledDeparture;
    private LocalDateTime scheduledArrival;
    private BigDecimal ticketPrice;
    private Integer estimatedDuration;
    private String vehiclePlateNumber;
    private Integer availableSeats;
}