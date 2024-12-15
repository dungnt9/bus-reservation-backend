package com.example.be.dto;

import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
public class RouteScheduleDropdownDTO {
    private Integer scheduleId;
    private String routeName;
    private LocalTime departureTime;
    private List<String> daysOfWeek;
}