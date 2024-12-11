package com.example.be.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.RouteSchedules;
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
    public ResponseEntity<List<RouteSchedules>> getAllRouteSchedules() {
        return ResponseEntity.ok(routeSchedulesService.getAllRouteSchedules());
    }

    @GetMapping("/{scheduleId}")
    public ResponseEntity<RouteSchedules> getRouteScheduleById(@PathVariable Integer scheduleId) {
        return ResponseEntity.ok(routeSchedulesService.getRouteScheduleById(scheduleId));
    }

    @PostMapping
    public ResponseEntity<RouteSchedules> createRouteSchedule(@Valid @RequestBody RouteSchedules routeSchedule) {
        return ResponseEntity.ok(routeSchedulesService.createRouteSchedule(routeSchedule));
    }

    @PutMapping("/{scheduleId}")
    public ResponseEntity<RouteSchedules> updateRouteSchedule(@PathVariable Integer scheduleId, @Valid @RequestBody RouteSchedules routeSchedule) {
        return ResponseEntity.ok(routeSchedulesService.updateRouteSchedule(scheduleId, routeSchedule));
    }

    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Void> deleteRouteSchedule(@PathVariable Integer scheduleId) {
        routeSchedulesService.deleteRouteSchedule(scheduleId);
        return ResponseEntity.noContent().build();
    }
}
