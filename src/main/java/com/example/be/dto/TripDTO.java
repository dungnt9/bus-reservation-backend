// Trong TripDTO.java
package com.example.be.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class TripDTO {
    private Integer tripId;
    private Integer scheduleId;
    private String routeName;
    private String routeStatus;
    private Integer driverId;
    private String driverName;
    private String driverStatus;
    private Integer assistantId;
    private String assistantName;
    private String assistantStatus;
    private LocalDateTime scheduledDeparture;
    private LocalDateTime scheduledArrival;
    private LocalDateTime actualDeparture;
    private LocalDateTime actualArrival;
    private String tripStatus;
    private String vehiclePlateNumber;
    private Integer totalSeats;
    private Integer availableSeats;
    private List<TripSeatDTO> tripSeats;
    private BigDecimal ticketPrice;  // Thêm trường này
    private Integer estimatedDuration;
}