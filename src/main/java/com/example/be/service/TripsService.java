package com.example.be.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.dto.*;
import com.example.be.model.*;
import com.example.be.repository.*;

@Service
public class TripsService {
    private final TripsRepository tripsRepository;
    private final DriversRepository driversRepository;
    private final AssistantsRepository assistantsRepository;
    private final RouteSchedulesRepository routeSchedulesRepository;
    private final TripSeatsRepository tripSeatsRepository;
    private final VehicleSeatsRepository vehicleSeatsRepository;
    private final VehiclesRepository vehiclesRepository;

    public TripsService(
            TripsRepository tripsRepository,
            DriversRepository driversRepository,
            AssistantsRepository assistantsRepository,
            RouteSchedulesRepository routeSchedulesRepository,
            TripSeatsRepository tripSeatsRepository,
            VehicleSeatsRepository vehicleSeatsRepository,
            VehiclesRepository vehiclesRepository) {
        this.tripsRepository = tripsRepository;
        this.driversRepository = driversRepository;
        this.assistantsRepository = assistantsRepository;
        this.routeSchedulesRepository = routeSchedulesRepository;
        this.tripSeatsRepository = tripSeatsRepository;
        this.vehicleSeatsRepository = vehicleSeatsRepository;
        this.vehiclesRepository = vehiclesRepository;
    }

    // Get available drivers for dropdown
    public List<DriverDTO> getAvailableDrivers() {
        return driversRepository.findAllNotDeleted().stream()
                .filter(driver -> driver.getDriverStatus() == Drivers.DriverStatus.available)
                .map(driver -> {
                    DriverDTO dto = new DriverDTO();
                    dto.setDriverId(driver.getDriverId());
                    dto.setFullName(driver.getUser().getFullName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Get available assistants for dropdown
    public List<AssistantDTO> getAvailableAssistants() {
        return assistantsRepository.findAllNotDeleted().stream()
                .filter(assistant -> assistant.getAssistantStatus() == Assistants.AssistantStatus.available)
                .map(assistant -> {
                    AssistantDTO dto = new AssistantDTO();
                    dto.setAssistantId(assistant.getAssistantId());
                    dto.setFullName(assistant.getUser().getFullName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Get active route schedules for dropdown
    public List<RouteScheduleDTO> getActiveRouteSchedules() {
        return routeSchedulesRepository.findAllNotDeleted().stream()
                .filter(schedule -> schedule.getRoute().getRouteStatus() == Routes.RouteStatus.active)
                .map(schedule -> {
                    RouteScheduleDTO dto = new RouteScheduleDTO();
                    dto.setScheduleId(schedule.getScheduleId());
                    dto.setRouteName(schedule.getRoute().getRouteName());
                    dto.setDepartureTime(schedule.getDepartureTime());
                    dto.setDaysOfWeek(schedule.getDaysOfWeek());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public TripDTO createTrip(TripCreateRequest request) {
        // Validate schedule exists and route is active
        RouteSchedules schedule = validateSchedule(request.getScheduleId());

        // Validate departure day matches schedule's day of week
        validateScheduleDay(schedule, request.getScheduledDeparture());

        // Validate driver and assistant availability
        Drivers driver = validateDriver(request.getDriverId());
        Assistants assistant = validateAssistant(request.getAssistantId());

        // Validate vehicle
        Vehicles vehicle = validateVehicle(request.getVehicleId());

        // Create trip
        Trips trip = new Trips();
        trip.setRouteSchedule(schedule);
        trip.setDriver(driver);
        trip.setAssistant(assistant);
        trip.setScheduledDeparture(request.getScheduledDeparture());
        trip.setScheduledArrival(request.getScheduledArrival());
        trip.setTripStatus(Trips.TripStatus.in_progress);
        trip.setCreatedAt(LocalDateTime.now());

        Trips savedTrip = tripsRepository.save(trip);

        // Create trip seats
        createTripSeats(savedTrip, vehicle);

        // Update statuses
        driver.setDriverStatus(Drivers.DriverStatus.on_trip);
        assistant.setAssistantStatus(Assistants.AssistantStatus.on_trip);
        driversRepository.save(driver);
        assistantsRepository.save(assistant);

        return convertToDTO(savedTrip);
    }

    public List<TripDTO> getAllTrips() {
        return tripsRepository.findAllNotDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public TripDTO getTripById(Integer tripId) {
        Trips trip = tripsRepository.findTripWithRouteById(tripId);
        if (trip == null) {
            throw new RuntimeException("Trip not found");
        }
        return convertToDTO(trip);
    }

    @Transactional
    public TripDTO updateTrip(Integer tripId, TripCreateRequest request) {
        Trips existingTrip = getTripEntity(tripId);

        // Update trip status if provided
        if (request.getTripStatus() != null) {
            existingTrip.setTripStatus(Trips.TripStatus.valueOf(request.getTripStatus()));
        }

        // Update departure and arrival times if provided
        if (request.getActualDeparture() != null) {
            existingTrip.setActualDeparture(request.getActualDeparture());
        }
        if (request.getActualArrival() != null) {
            existingTrip.setActualArrival(request.getActualArrival());
        }

        // Update driver if changed
        updateDriverIfChanged(existingTrip, request.getDriverId());

        // Update assistant if changed
        updateAssistantIfChanged(existingTrip, request.getAssistantId());

        // Update trip seats if provided
        updateTripSeatsIfProvided(request.getTripSeats());

        existingTrip.setUpdatedAt(LocalDateTime.now());
        return convertToDTO(tripsRepository.save(existingTrip));
    }

    @Transactional
    public void deleteTrip(Integer tripId) {
        Trips trip = getTripEntity(tripId);

        // Soft delete associated trip seats
        List<TripSeats> tripSeats = tripSeatsRepository.findByTripId(tripId);
        tripSeats.forEach(seat -> {
            seat.markAsDeleted();
            tripSeatsRepository.save(seat);
        });

        // Update driver and assistant status
        trip.getDriver().setDriverStatus(Drivers.DriverStatus.available);
        trip.getAssistant().setAssistantStatus(Assistants.AssistantStatus.available);
        driversRepository.save(trip.getDriver());
        assistantsRepository.save(trip.getAssistant());

        // Soft delete trip
        trip.markAsDeleted();
        tripsRepository.save(trip);
    }

    // Private helper methods
    private RouteSchedules validateSchedule(Integer scheduleId) {
        RouteSchedules schedule = routeSchedulesRepository.findByIdNotDeleted(scheduleId);
        if (schedule == null || schedule.getRoute().getRouteStatus() != Routes.RouteStatus.active) {
            throw new RuntimeException("Invalid or inactive route schedule");
        }
        return schedule;
    }

    private void validateScheduleDay(RouteSchedules schedule, LocalDateTime scheduledDeparture) {
        String tripDay = scheduledDeparture.getDayOfWeek().name();
        List<String> scheduleDays = schedule.getDaysOfWeek();

        if (!scheduleDays.contains(tripDay)) {
            String daysInVietnamese = scheduleDays.stream()
                    .map(this::formatDayToVietnamese)
                    .collect(Collectors.joining(", "));

            throw new RuntimeException("Thời gian khởi hành phải là một trong các ngày: " + daysInVietnamese);
        }
    }

    private Drivers validateDriver(Integer driverId) {
        Drivers driver = driversRepository.findByIdNotDeleted(driverId);
        if (driver == null) {
            throw new RuntimeException("Driver not found or has been deleted");
        }
        return driver;
    }

    private Assistants validateAssistant(Integer assistantId) {
        Assistants assistant = assistantsRepository.findByIdNotDeleted(assistantId);
        if (assistant == null) {
            throw new RuntimeException("Assistant not found or has been deleted");
        }
        return assistant;
    }

    private Vehicles validateVehicle(Integer vehicleId) {
        Vehicles vehicle = vehiclesRepository.findByIdNotDeleted(vehicleId);
        if (vehicle == null || vehicle.getVehicleStatus() != Vehicles.VehicleStatus.active) {
            throw new RuntimeException("Vehicle not found or not active");
        }
        return vehicle;
    }

    private void createTripSeats(Trips trip, Vehicles vehicle) {
        List<VehicleSeats> vehicleSeats = vehicleSeatsRepository.findByVehicle(vehicle);
        if (vehicleSeats.isEmpty()) {
            throw new RuntimeException("No seats found for vehicle");
        }

        List<TripSeats> tripSeats = vehicleSeats.stream().map(vehicleSeat -> {
            TripSeats tripSeat = new TripSeats();
            tripSeat.setTrip(trip);
            tripSeat.setVehicleSeat(vehicleSeat);
            tripSeat.setTripSeatStatus(TripSeats.TripSeatStatus.available);
            tripSeat.setCreatedAt(LocalDateTime.now());
            return tripSeat;
        }).collect(Collectors.toList());

        tripSeatsRepository.saveAll(tripSeats);
    }

    private void updateDriverIfChanged(Trips trip, Integer newDriverId) {
        if (newDriverId != null && !trip.getDriver().getDriverId().equals(newDriverId)) {
            trip.getDriver().setDriverStatus(Drivers.DriverStatus.available);
            driversRepository.save(trip.getDriver());

            Drivers newDriver = validateDriver(newDriverId);
            newDriver.setDriverStatus(Drivers.DriverStatus.on_trip);
            driversRepository.save(newDriver);
            trip.setDriver(newDriver);
        }
    }

    private void updateAssistantIfChanged(Trips trip, Integer newAssistantId) {
        if (newAssistantId != null && !trip.getAssistant().getAssistantId().equals(newAssistantId)) {
            trip.getAssistant().setAssistantStatus(Assistants.AssistantStatus.available);
            assistantsRepository.save(trip.getAssistant());

            Assistants newAssistant = validateAssistant(newAssistantId);
            newAssistant.setAssistantStatus(Assistants.AssistantStatus.on_trip);
            assistantsRepository.save(newAssistant);
            trip.setAssistant(newAssistant);
        }
    }

    private void updateTripSeatsIfProvided(List<TripSeatUpdateDTO> seatUpdates) {
        if (seatUpdates != null) {
            for (TripSeatUpdateDTO seatUpdate : seatUpdates) {
                TripSeats tripSeat = tripSeatsRepository.findByIdNotDeleted(seatUpdate.getTripSeatId());
                if (tripSeat != null) {
                    // Chuyển đổi từ String trong DTO sang enum TripSeatStatus
                    TripSeats.TripSeatStatus status = TripSeats.TripSeatStatus.valueOf(seatUpdate.getStatus());
                    tripSeat.setTripSeatStatus(status);
                    tripSeatsRepository.save(tripSeat);
                }
            }
        }
    }

    private Trips getTripEntity(Integer tripId) {
        Trips trip = tripsRepository.findByIdNotDeleted(tripId);
        if (trip == null) {
            throw new RuntimeException("Trip not found or has been deleted");
        }
        return trip;
    }

    private TripDTO convertToDTO(Trips trip) {
        TripDTO dto = new TripDTO();
        dto.setTripId(trip.getTripId());

        // Route Schedule và Route information
        RouteSchedules schedule = trip.getRouteSchedule();
        if (schedule != null && schedule.getDeletedAt() == null) {
            dto.setScheduleId(schedule.getScheduleId());
            Routes route = schedule.getRoute();
            if (route != null && route.getDeletedAt() == null) {
                dto.setRouteName(route.getRouteName());
                dto.setRouteStatus(route.getRouteStatus().toString());
                dto.setTicketPrice(route.getTicketPrice());
            } else {
                dto.setRouteName("Route no longer exists");
                dto.setRouteStatus("DELETED");
            }
        } else {
            dto.setRouteName("Schedule no longer exists");
        }

        // Driver information
        Drivers driver = trip.getDriver();
        if (driver != null && driver.getDeletedAt() == null) {
            dto.setDriverId(driver.getDriverId());
            dto.setDriverName(driver.getUser().getFullName());
            dto.setDriverStatus(driver.getDriverStatus().toString());
        } else {
            dto.setDriverName("Driver no longer exists");
            dto.setDriverStatus("DELETED");
        }

        // Assistant information
        Assistants assistant = trip.getAssistant();
        if (assistant != null && assistant.getDeletedAt() == null) {
            dto.setAssistantId(assistant.getAssistantId());
            dto.setAssistantName(assistant.getUser().getFullName());
            dto.setAssistantStatus(assistant.getAssistantStatus().toString());
        } else {
            dto.setAssistantName("Assistant no longer exists");
            dto.setAssistantStatus("DELETED");
        }

        // Trip details
        dto.setScheduledDeparture(trip.getScheduledDeparture());
        dto.setScheduledArrival(trip.getScheduledArrival());
        dto.setActualDeparture(trip.getActualDeparture());
        dto.setActualArrival(trip.getActualArrival());
        dto.setTripStatus(trip.getTripStatus().toString());

        // Vehicle và Seat information
        List<TripSeats> tripSeats = tripSeatsRepository.findByTripId(trip.getTripId());
        dto.setTotalSeats(tripSeats.size());
        dto.setAvailableSeats((int) tripSeats.stream()
                .filter(seat -> seat.getTripSeatStatus() == TripSeats.TripSeatStatus.available)
                .count());

        // Vehicle information từ trip seats
        if (!tripSeats.isEmpty()) {
            VehicleSeats firstSeat = tripSeats.get(0).getVehicleSeat();
            if (firstSeat != null && firstSeat.getVehicle() != null) {
                dto.setVehiclePlateNumber(firstSeat.getVehicle().getPlateNumber());
            }
        }

        // Convert trip seats to DTOs
        dto.setTripSeats(tripSeats.stream()
                .map(this::convertToTripSeatDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private TripSeatDTO convertToTripSeatDTO(TripSeats tripSeat) {
        TripSeatDTO dto = new TripSeatDTO();
        dto.setTripSeatId(tripSeat.getTripSeatId());
        dto.setSeatNumber(tripSeat.getVehicleSeat().getSeatNumber());
        dto.setVehiclePlateNumber(tripSeat.getVehicleSeat().getVehicle().getPlateNumber());
        dto.setStatus(tripSeat.getTripSeatStatus().toString()); // Convert enum to string
        return dto;
    }

    private String formatDayToVietnamese(String day) {
        switch (day) {
            case "MONDAY": return "Thứ 2";
            case "TUESDAY": return "Thứ 3";
            case "WEDNESDAY": return "Thứ 4";
            case "THURSDAY": return "Thứ 5";
            case "FRIDAY": return "Thứ 6";
            case "SATURDAY": return "Thứ 7";
            case "SUNDAY": return "Chủ nhật";
            default: return day;
        }
    }

    public List<VehicleSeatDTO> getAvailableVehicleSeats() {
        return vehicleSeatsRepository.findAllNotDeleted().stream()
                .map(seat -> {
                    VehicleSeatDTO dto = new VehicleSeatDTO();
                    dto.setVehicleSeatId(seat.getVehicleSeatId());
                    dto.setSeatNumber(seat.getSeatNumber());
                    dto.setVehicleId(seat.getVehicle().getVehicleId());
                    dto.setPlateNumber(seat.getVehicle().getPlateNumber());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TripDTO> searchAvailableTrips(Integer routeId, LocalDate departureDate) {
        List<Trips> trips = tripsRepository.searchAvailableTrips(routeId, departureDate);
        return trips.stream()
                .map(this::convertToSearchDTO)
                .collect(Collectors.toList());
    }

    private TripDTO convertToSearchDTO(Trips trip) {
        TripDTO dto = new TripDTO();
        dto.setTripId(trip.getTripId());
        dto.setRouteName(trip.getRouteSchedule().getRoute().getRouteName());
        dto.setScheduledDeparture(trip.getScheduledDeparture());
        dto.setScheduledArrival(trip.getScheduledArrival());
        dto.setTicketPrice(trip.getRouteSchedule().getRoute().getTicketPrice());
        dto.setEstimatedDuration(trip.getRouteSchedule().getRoute().getEstimatedDuration());

        // Get vehicle info from first available seat
        List<TripSeats> tripSeats = tripSeatsRepository.findByTripId(trip.getTripId());
        if (!tripSeats.isEmpty()) {
            VehicleSeats firstSeat = tripSeats.get(0).getVehicleSeat();
            dto.setVehiclePlateNumber(firstSeat.getVehicle().getPlateNumber());
        }

        // Calculate available seats
        Long availableSeats = tripsRepository.countAvailableSeats(trip.getTripId());
        dto.setAvailableSeats(availableSeats.intValue());

        return dto;
    }

    @Transactional(readOnly = true)
    public TripDetailsDTO getTripDetails(Integer tripId) {
        Trips trip = tripsRepository.findTripWithDetailsById(tripId);
        if (trip == null) {
            throw new RuntimeException("Trip not found");
        }

        TripDetailsDTO details = new TripDetailsDTO();
        details.setTripId(trip.getTripId());
        details.setRouteName(trip.getRouteSchedule().getRoute().getRouteName());
        details.setScheduledDeparture(trip.getScheduledDeparture());
        details.setScheduledArrival(trip.getScheduledArrival());
        details.setTicketPrice(trip.getRouteSchedule().getRoute().getTicketPrice());
        details.setEstimatedDuration(trip.getRouteSchedule().getRoute().getEstimatedDuration());

        // Get all seats with their status
        List<TripSeats> tripSeats = tripsRepository.findTripSeatsWithDetails(tripId);
        if (!tripSeats.isEmpty()) {
            details.setVehiclePlateNumber(tripSeats.get(0).getVehicleSeat().getVehicle().getPlateNumber());
            details.setSeats(tripSeats.stream()
                    .map(this::convertToSeatDTO)
                    .collect(Collectors.toList()));
        }

        return details;
    }

    private SeatDTO convertToSeatDTO(TripSeats tripSeat) {
        SeatDTO dto = new SeatDTO();
        dto.setSeatId(tripSeat.getTripSeatId());
        dto.setSeatNumber(tripSeat.getVehicleSeat().getSeatNumber());
        dto.setStatus(tripSeat.getTripSeatStatus().toString());
        return dto;
    }



//    Dành cho tài xế, phụ xe
    public List<TripDTO> getMyTrips(Integer userId, String userRole) {
        List<Trips> trips;
        if ("driver".equals(userRole.toLowerCase())) {
            // Get trips where user is driver
            trips = tripsRepository.findAllNotDeleted().stream()
                    .filter(trip -> trip.getDriver().getUser().getUserId().equals(userId))
                    .collect(Collectors.toList());
        } else if ("assistant".equals(userRole.toLowerCase())) {
            // Get trips where user is assistant
            trips = tripsRepository.findAllNotDeleted().stream()
                    .filter(trip -> trip.getAssistant().getUser().getUserId().equals(userId))
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }

        return trips.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public TripDTO updateTripStatusAndTimes(Integer tripId, TripStatusUpdateRequest request) {
        // Validate trip exists
        Trips trip = getTripEntity(tripId);
        if (trip == null) {
            throw new RuntimeException("Trip not found");
        }

        // Validate time sequence if both times are provided
        if (request.getActualDeparture() != null && request.getActualArrival() != null) {
            if (request.getActualArrival().isBefore(request.getActualDeparture())) {
                throw new RuntimeException("Actual arrival time cannot be before actual departure time");
            }
        }

        // Update actual times if provided
        if (request.getActualDeparture() != null) {
            trip.setActualDeparture(request.getActualDeparture());
        }
        if (request.getActualArrival() != null) {
            trip.setActualArrival(request.getActualArrival());
        }

        // Update status if provided
        if (request.getTripStatus() != null) {
            trip.setTripStatus(Trips.TripStatus.valueOf(request.getTripStatus()));
        }

        // Update timestamp
        trip.setUpdatedAt(LocalDateTime.now());

        // Save and return updated trip
        Trips updatedTrip = tripsRepository.save(trip);
        return convertToDTO(updatedTrip);
    }
}