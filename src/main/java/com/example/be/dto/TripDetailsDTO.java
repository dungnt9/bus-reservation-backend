package com.example.be.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TripDetailsDTO {
    private Integer tripId;
    private String routeName;
    private LocalDateTime scheduledDeparture;
    private LocalDateTime scheduledArrival;
    private BigDecimal ticketPrice;
    private Integer estimatedDuration;
    private String vehiclePlateNumber;
    private List<SeatDTO> seats;
}
