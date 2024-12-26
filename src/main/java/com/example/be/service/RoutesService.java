package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.be.model.Routes;
import com.example.be.dto.RouteDTO;
import com.example.be.repository.RoutesRepository;
import com.example.be.repository.RouteSchedulesRepository;

@Service
public class RoutesService {
    private final RoutesRepository routesRepository;
    private final RouteSchedulesRepository routeSchedulesRepository;

    public RoutesService(RoutesRepository routesRepository, RouteSchedulesRepository routeSchedulesRepository) {
        this.routesRepository = routesRepository;
        this.routeSchedulesRepository = routeSchedulesRepository;
    }

    public Page<RouteDTO> getAllRoutes(Pageable pageable) {
        Page<Routes> routePage = routesRepository.findAllNotDeleted(pageable);
        return routePage.map(this::convertToDTO);
    }

    public List<RouteDTO> getAllRoutesWithoutPagination() {
        List<Routes> routes = routesRepository.findAllNotDeleted();
        return routes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RouteDTO getRouteById(Integer routeId) {
        Routes route = routesRepository.findByIdNotDeleted(routeId);
        if (route == null) {
            throw new RuntimeException("Route not found or has been deleted");
        }
        return convertToDTO(route);
    }

    @Transactional
    public RouteDTO createRoute(RouteDTO routeDTO) {
        Routes route = convertToEntity(routeDTO);
        route.setCreatedAt(LocalDateTime.now());
        route.setUpdatedAt(LocalDateTime.now());
        return convertToDTO(routesRepository.save(route));
    }

    @Transactional
    public RouteDTO updateRoute(Integer routeId, RouteDTO routeDTO) {
        Routes route = routesRepository.findByIdNotDeleted(routeId);
        if (route == null) {
            throw new RuntimeException("Route not found or has been deleted");
        }

        route.setRouteName(routeDTO.getRouteName());
        route.setTicketPrice(routeDTO.getTicketPrice());
        route.setDistance(routeDTO.getDistance());
        route.setEstimatedDuration(routeDTO.getEstimatedDuration());
        route.setRouteStatus(routeDTO.getRouteStatus());
        route.setUpdatedAt(LocalDateTime.now());

        return convertToDTO(routesRepository.save(route));
    }

    @Transactional
    public void deleteRoute(Integer routeId) {
        Routes route = routesRepository.findByIdNotDeleted(routeId);
        if (route == null) {
            throw new RuntimeException("Route not found or has been deleted");
        }

        // Soft delete the route
        LocalDateTime now = LocalDateTime.now();
        route.setDeletedAt(now);
        route.setUpdatedAt(now);
        routesRepository.save(route);

        // Cascade soft delete to related route schedules
        routeSchedulesRepository.softDeleteByRouteId(routeId, now);
    }

    private RouteDTO convertToDTO(Routes route) {
        RouteDTO dto = new RouteDTO();
        dto.setRouteId(route.getRouteId());
        dto.setRouteName(route.getRouteName());
        dto.setTicketPrice(route.getTicketPrice());
        dto.setDistance(route.getDistance());
        dto.setEstimatedDuration(route.getEstimatedDuration());
        dto.setRouteStatus(route.getRouteStatus());
        return dto;
    }

    private Routes convertToEntity(RouteDTO dto) {
        Routes route = new Routes();
        route.setRouteName(dto.getRouteName());
        route.setTicketPrice(dto.getTicketPrice());
        route.setDistance(dto.getDistance());
        route.setEstimatedDuration(dto.getEstimatedDuration());
        route.setRouteStatus(dto.getRouteStatus());
        return route;
    }
}