package com.example.be.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.be.dto.RouteDTO;
import com.example.be.service.RoutesService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/routes")
public class RoutesController {
    private final RoutesService routesService;

    public RoutesController(RoutesService routesService) {
        this.routesService = routesService;
    }

    @GetMapping
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        return ResponseEntity.ok(routesService.getAllRoutes());
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<RouteDTO> getRouteById(@PathVariable Integer routeId) {
        return ResponseEntity.ok(routesService.getRouteById(routeId));
    }

    @PostMapping
    public ResponseEntity<RouteDTO> createRoute(@Valid @RequestBody RouteDTO routeDTO) {
        return ResponseEntity.ok(routesService.createRoute(routeDTO));
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<RouteDTO> updateRoute(@PathVariable Integer routeId, @Valid @RequestBody RouteDTO routeDTO) {
        return ResponseEntity.ok(routesService.updateRoute(routeId, routeDTO));
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Integer routeId) {
        routesService.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }
}