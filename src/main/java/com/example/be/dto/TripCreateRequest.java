package com.example.be.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TripCreateRequest {
    private Integer scheduleId;
    private Integer driverId;
    private Integer assistantId;
    private Integer vehicleId;
    private LocalDateTime scheduledDeparture;
    private LocalDateTime scheduledArrival;
    private LocalDateTime actualDeparture;
    private LocalDateTime actualArrival;
    private String tripStatus;
    private List<TripSeatDTO> tripSeats;
}