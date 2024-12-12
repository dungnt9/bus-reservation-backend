package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.be.model.Routes;
import com.example.be.repository.RoutesRepository;

@Service
public class RoutesService {

    private final RoutesRepository routesRepository;

    // Constructor injection
    public RoutesService(RoutesRepository routesRepository) {
        this.routesRepository = routesRepository;
    }

    public Routes createRoute(Routes route) {
        route.setCreatedAt(LocalDateTime.now());
        return routesRepository.save(route);
    }

    public List<Routes> getAllRoutes() {
        return routesRepository.findAllNotDeleted();
    }

    public Routes getRouteById(Integer routeId) {
        Routes route = routesRepository.findByIdNotDeleted(routeId);
        if (route == null) {
            throw new RuntimeException("Route not found or has been deleted");
        }
        return route;
    }

    public Routes updateRoute(Integer routeId, Routes routeDetails) {
        Routes route = getRouteById(routeId);

        route.setRouteName(routeDetails.getRouteName());
        route.setTicketPrice(routeDetails.getTicketPrice());
        route.setDistance(routeDetails.getDistance());
        route.setEstimatedDuration(routeDetails.getEstimatedDuration());
        route.setRouteStatus(routeDetails.getRouteStatus());
        route.setUpdatedAt(LocalDateTime.now());

        return routesRepository.save(route);
    }

    public void deleteRoute(Integer routeId) {
        Routes route = getRouteById(routeId);
        route.markAsDeleted();
        routesRepository.save(route);
    }
}