package com.example.be.dto;

import java.time.LocalTime;
import java.util.List;
import lombok.Data;

@Data
public class RouteScheduleDTO {
    private Integer scheduleId;
    private Integer routeId;
    private String routeName;
    private LocalTime departureTime;  // Thay đổi kiểu dữ liệu thành LocalTime
    private List<String> daysOfWeek;
}