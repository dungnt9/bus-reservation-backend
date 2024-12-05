package com.example.be.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.be.model.Routes;
import com.example.be.repository.RoutesRepository;

@Service
public class RoutesService {

    @Autowired
    private RoutesRepository routesRepository;

    public Routes createRoute(Routes route) {
        return routesRepository.save(route);
    }

    public List<Routes> getAllRoutes() {
        return routesRepository.findAll();
    }

    public Routes getRouteById(Integer routeId) {
        return routesRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
    }

    public Routes updateRoute(Integer routeId, Routes routeDetails) {
        Routes route = getRouteById(routeId);
        
        route.setRouteName(routeDetails.getRouteName());
        route.setTicketPrice(routeDetails.getTicketPrice());
        route.setDistance(routeDetails.getDistance());
        route.setEstimatedDuration(routeDetails.getEstimatedDuration());
        route.setRouteStatus(routeDetails.getRouteStatus());

        return routesRepository.save(route);
    }

    public void deleteRoute(Integer routeId) {
        Routes route = getRouteById(routeId);
        routesRepository.delete(route);
    }
}