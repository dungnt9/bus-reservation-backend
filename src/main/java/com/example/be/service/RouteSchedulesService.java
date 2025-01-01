package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.model.RouteSchedules;
import com.example.be.model.Routes;
import com.example.be.dto.RouteScheduleDTO;
import com.example.be.repository.RouteSchedulesRepository;

@Service
public class RouteSchedulesService {

    private final RouteSchedulesRepository routeSchedulesRepository;
    private final RoutesService routesService;

    // Constructor injection
    public RouteSchedulesService(
            RouteSchedulesRepository routeSchedulesRepository,
            RoutesService routesService
    ) {
        this.routeSchedulesRepository = routeSchedulesRepository;
        this.routesService = routesService;
    }

    @Transactional
    public RouteScheduleDTO createRouteSchedule(RouteSchedules routeSchedule) {
        routeSchedule.setCreatedAt(LocalDateTime.now());
        RouteSchedules savedSchedule = routeSchedulesRepository.save(routeSchedule);
        return convertToDTO(savedSchedule);
    }

    public List<RouteScheduleDTO> getAllRouteSchedules() {
        return routeSchedulesRepository.findAllNotDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public RouteScheduleDTO getRouteScheduleById(Integer scheduleId) {
        RouteSchedules routeSchedule = routeSchedulesRepository.findByIdNotDeleted(scheduleId);
        if (routeSchedule == null) {
            throw new RuntimeException("Route Schedule not found or has been deleted");
        }
        return convertToDTO(routeSchedule);
    }

    @Transactional
    public RouteScheduleDTO updateRouteSchedule(Integer scheduleId, RouteSchedules routeScheduleDetails) {
        RouteSchedules routeSchedule = routeSchedulesRepository.findByIdNotDeleted(scheduleId);
        if (routeSchedule == null) {
            throw new RuntimeException("Route Schedule not found or has been deleted");
        }

        routeSchedule.setRoute(routeScheduleDetails.getRoute());
        routeSchedule.setDepartureTime(routeScheduleDetails.getDepartureTime());
        routeSchedule.setDaysOfWeek(routeScheduleDetails.getDaysOfWeek());
        routeSchedule.setUpdatedAt(LocalDateTime.now());

        RouteSchedules updatedSchedule = routeSchedulesRepository.save(routeSchedule);
        return convertToDTO(updatedSchedule);
    }

    // Conversion method to DTO
    private RouteScheduleDTO convertToDTO(RouteSchedules routeSchedule) {
        RouteScheduleDTO dto = new RouteScheduleDTO();
        dto.setScheduleId(routeSchedule.getScheduleId());

        Routes route = routeSchedule.getRoute();
        if (route != null) {
            dto.setRouteId(route.getRouteId());
            dto.setRouteName(route.getRouteName());
        }

        dto.setDepartureTime(routeSchedule.getDepartureTime());  // LocalTime được chuyển trực tiếp
        dto.setDaysOfWeek(routeSchedule.getDaysOfWeek());

        return dto;
    }
}