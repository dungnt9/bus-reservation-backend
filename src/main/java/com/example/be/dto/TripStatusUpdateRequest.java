package com.example.be.dto;

import lombok.Data;
import java.time.LocalDateTime;
import jakarta.validation.constraints.Pattern;

@Data
public class TripStatusUpdateRequest {
    private LocalDateTime actualDeparture;
    private LocalDateTime actualArrival;

    @Pattern(regexp = "^(in_progress|completed|cancelled)$",
            message = "Status must be either 'in_progress', 'completed', or 'cancelled'")
    private String tripStatus;
}