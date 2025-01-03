package com.example.be.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import com.example.be.model.RouteSchedules;
import com.example.be.dto.RouteScheduleDTO;
import com.example.be.service.RouteSchedulesService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/route-schedules")
public class RouteSchedulesController {

    private final RouteSchedulesService routeSchedulesService;

    public RouteSchedulesController(RouteSchedulesService routeSchedulesService) {
        this.routeSchedulesService = routeSchedulesService;
    }

    @GetMapping
    public ResponseEntity<Page<RouteScheduleDTO>> getAllRouteSchedules(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer routeId,
            @RequestParam(required = false) String routeName,
            @RequestParam(required = false) String departureTime,
            @RequestParam(required = false) String daysOfWeek
    ) {
        List<String> daysOfWeekList = daysOfWeek != null ?
                Arrays.asList(daysOfWeek.split(",")) :
                new ArrayList<>();

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(routeSchedulesService.getAllRouteSchedules(
                pageable,
                routeId,
                routeName,
                departureTime,
                daysOfWeekList
        ));
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<RouteScheduleDTO> getRouteScheduleById(@PathVariable Integer scheduleId) {
        return ResponseEntity.ok(routeSchedulesService.getRouteScheduleById(scheduleId));
    }

    @PostMapping
    public ResponseEntity<RouteScheduleDTO> createRouteSchedule(@Valid @RequestBody RouteSchedules routeSchedule) {
        return ResponseEntity.ok(routeSchedulesService.createRouteSchedule(routeSchedule));
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<RouteScheduleDTO> updateRouteSchedule(
            @PathVariable Integer scheduleId,
            @Valid @RequestBody RouteSchedules routeSchedule
    ) {
        return ResponseEntity.ok(routeSchedulesService.updateRouteSchedule(scheduleId, routeSchedule));
    }
}