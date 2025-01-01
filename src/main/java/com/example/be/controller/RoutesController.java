package com.example.be.controller;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    public ResponseEntity<Page<RouteDTO>> getAllRoutes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String routeName,
            @RequestParam(required = false) BigDecimal ticketPrice,
            @RequestParam(required = false) BigDecimal distance,
            @RequestParam(required = false) Integer estimatedDuration,
            @RequestParam(required = false) String routeStatus
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(routesService.getAllRoutes(
                pageable,
                routeName,
                ticketPrice,
                distance,
                estimatedDuration,
                routeStatus
        ));
    }

    @GetMapping("/all")
    public ResponseEntity<List<RouteDTO>> getAllRoutesWithoutPagination() {
        return ResponseEntity.ok(routesService.getAllRoutesWithoutPagination());
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
}