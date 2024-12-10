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

import com.example.be.model.Routes;
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
    public ResponseEntity<List<Routes>> getAllRoutes() {
        return ResponseEntity.ok(routesService.getAllRoutes());
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<Routes> getRouteById(@PathVariable Integer routeId) {
        return ResponseEntity.ok(routesService.getRouteById(routeId));
    }

    @PostMapping
    public ResponseEntity<Routes> createRoute(@Valid @RequestBody Routes route) {
        return ResponseEntity.ok(routesService.createRoute(route));
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<Routes> updateRoute(@PathVariable Integer routeId, @Valid @RequestBody Routes route) {
        return ResponseEntity.ok(routesService.updateRoute(routeId, route));
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Integer routeId) {
        routesService.deleteRoute(routeId);
        return ResponseEntity.noContent().build();
    }
}
