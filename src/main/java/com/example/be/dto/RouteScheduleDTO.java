package com.example.be.dto;

import java.time.LocalTime;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteScheduleDTO {
    private Integer scheduleId;
    private Integer routeId;
    private String routeName;
    private LocalTime departureTime;
    private List<String> daysOfWeek;
}