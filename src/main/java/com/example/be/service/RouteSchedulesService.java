package com.example.be.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<RouteScheduleDTO> getAllRouteSchedules(
            Pageable pageable,
            Integer routeId,
            String routeName,
            String departureTime,
            List<String> daysOfWeek
    ) {
        Specification<RouteSchedules> spec = (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            if (routeId != null) {
                predicates.add(criteriaBuilder.equal(root.get("route").get("routeId"), routeId));
            }

            if (routeName != null && !routeName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("route").get("routeName")),
                        "%" + routeName.toLowerCase() + "%"
                ));
            }

            if (departureTime != null && !departureTime.isEmpty()) {
                try {
                    LocalTime time = LocalTime.parse(departureTime);
                    predicates.add(criteriaBuilder.equal(root.get("departureTime"), time));
                } catch (Exception e) {
                    System.err.println("Invalid time format: " + departureTime);
                }
            }

            if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
                List<Predicate> dayPredicates = daysOfWeek.stream()
                        .map(day -> criteriaBuilder.like(root.get("dayOfWeek"), "%" + day + "%"))
                        .collect(Collectors.toList());

                // All selected days must be present (AND condition)
                predicates.addAll(dayPredicates);
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<RouteSchedules> schedulesPage = routeSchedulesRepository.findAll(spec, pageable);
        return schedulesPage.map(this::convertToDTO);
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