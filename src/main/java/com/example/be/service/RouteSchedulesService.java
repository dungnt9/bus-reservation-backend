package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.be.model.RouteSchedules;
import com.example.be.repository.RouteSchedulesRepository;

@Service
public class RouteSchedulesService {

    @Autowired
    private RouteSchedulesRepository routeSchedulesRepository;

    public RouteSchedules createRouteSchedule(RouteSchedules routeSchedule) {
        routeSchedule.setCreatedAt(LocalDateTime.now());
        return routeSchedulesRepository.save(routeSchedule);
    }

    public List<RouteSchedules> getAllRouteSchedules() {
        return routeSchedulesRepository.findAll();
    }

    public RouteSchedules getRouteScheduleById(Integer scheduleId) {
        return routeSchedulesRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Route Schedule not found"));
    }

    public RouteSchedules updateRouteSchedule(Integer scheduleId, RouteSchedules routeScheduleDetails) {
        RouteSchedules routeSchedule = getRouteScheduleById(scheduleId);
        
        routeSchedule.setRoute(routeScheduleDetails.getRoute());
        routeSchedule.setDepartureTime(routeScheduleDetails.getDepartureTime());
        routeSchedule.setIsDaily(routeScheduleDetails.getIsDaily());
        routeSchedule.setDayOfWeek(routeScheduleDetails.getDayOfWeek());
        routeSchedule.setUpdatedAt(LocalDateTime.now());

        return routeSchedulesRepository.save(routeSchedule);
    }

    public void deleteRouteSchedule(Integer scheduleId) {
        RouteSchedules routeSchedule = getRouteScheduleById(scheduleId);
        routeSchedule.setDeletedAt(LocalDateTime.now());
        routeSchedulesRepository.save(routeSchedule);
    }
}