package com.example.be.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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
        // Lấy tất cả tài xế có trạng thái available
        List<Drivers> availableDrivers = driversRepository.findAllNotDeleted().stream()
                .filter(driver -> driver.getDriverStatus() == Drivers.DriverStatus.available)
                .collect(Collectors.toList());

        return availableDrivers.stream()
                .map(driver -> {
                    DriverDTO dto = new DriverDTO();
                    dto.setDriverId(driver.getDriverId());
                    dto.setFullName(driver.getUser().getFullName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public Page<TripDTO> getAllTripsWithFilters(
            Pageable pageable,
            Integer tripId,
            String routeName,
            String driverName,
            String assistantName,
            String vehiclePlateNumber,
            String tripStatus,
            Integer totalSeats,
            Integer availableSeats,
            LocalDateTime scheduledDeparture,
            LocalDateTime scheduledArrival,
            LocalDateTime actualDeparture,
            LocalDateTime actualArrival
    ) {
        Specification<Trips> spec = (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            // Base condition: not deleted
            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            if (tripId != null) {
                predicates.add(criteriaBuilder.equal(root.get("tripId"), tripId));
            }

            if (routeName != null && !routeName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("routeSchedule").get("route").get("routeName")),
                        "%" + routeName.toLowerCase() + "%"
                ));
            }

            if (driverName != null && !driverName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("driver").get("user").get("fullName")),
                        "%" + driverName.toLowerCase() + "%"
                ));
            }

            if (assistantName != null && !assistantName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("assistant").get("user").get("fullName")),
                        "%" + assistantName.toLowerCase() + "%"
                ));
            }

            if (vehiclePlateNumber != null && !vehiclePlateNumber.isEmpty()) {
                // Create a subquery to get all trips that have any seats with the matching vehicle plate number
                Subquery<Integer> tripIdSubquery = query.subquery(Integer.class);
                Root<TripSeats> tripSeatsRoot = tripIdSubquery.from(TripSeats.class);

                tripIdSubquery.select(tripSeatsRoot.get("trip").get("tripId"))
                        .where(criteriaBuilder.like(
                                criteriaBuilder.lower(tripSeatsRoot
                                        .get("vehicleSeat")
                                        .get("vehicle")
                                        .get("plateNumber")),
                                "%" + vehiclePlateNumber.toLowerCase() + "%"
                        ));

                predicates.add(criteriaBuilder.in(root.get("tripId")).value(tripIdSubquery));
            }

            if (tripStatus != null && !tripStatus.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("tripStatus"),
                        Trips.TripStatus.valueOf(tripStatus.toLowerCase())
                ));
            }

            if (scheduledDeparture != null) {
                predicates.add(criteriaBuilder.equal(root.get("scheduledDeparture"), scheduledDeparture));
            }

            if (scheduledArrival != null) {
                predicates.add(criteriaBuilder.equal(root.get("scheduledArrival"), scheduledArrival));
            }

            if (actualDeparture != null) {
                predicates.add(criteriaBuilder.equal(root.get("actualDeparture"), actualDeparture));
            }

            if (actualArrival != null) {
                predicates.add(criteriaBuilder.equal(root.get("actualArrival"), actualArrival));
            }

            if (totalSeats != null) {
                Subquery<Long> totalSeatsSubquery = query.subquery(Long.class);
                Root<TripSeats> tripSeatsRoot = totalSeatsSubquery.from(TripSeats.class);
                totalSeatsSubquery.select(criteriaBuilder.count(tripSeatsRoot))
                        .where(criteriaBuilder.equal(tripSeatsRoot.get("trip"), root));

                predicates.add(criteriaBuilder.equal(totalSeatsSubquery, totalSeats));
            }

            if (availableSeats != null) {
                Subquery<Long> availableSeatsSubquery = query.subquery(Long.class);
                Root<TripSeats> tripSeatsRoot = availableSeatsSubquery.from(TripSeats.class);
                availableSeatsSubquery.select(criteriaBuilder.count(tripSeatsRoot))
                        .where(
                                criteriaBuilder.and(
                                        criteriaBuilder.equal(tripSeatsRoot.get("trip"), root),
                                        criteriaBuilder.equal(tripSeatsRoot.get("tripSeatStatus"), TripSeats.TripSeatStatus.available)
                                )
                        );

                predicates.add(criteriaBuilder.equal(availableSeatsSubquery, availableSeats));
            }

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<Trips> tripsPage = tripsRepository.findAll(spec, pageable);
        return tripsPage.map(this::convertToDTO);
    }

    public List<DriverDTO> getDriverForTrip(Integer tripId) {
        // Lấy danh sách gồm các tài xế available và tài xế của chuyến xe này
        List<Drivers> drivers = driversRepository.findAllNotDeleted().stream()
                .filter(driver ->
                        driver.getDriverStatus() == Drivers.DriverStatus.available ||
                                (driver.getDriverStatus() == Drivers.DriverStatus.on_trip && isDriverForTrip(driver, tripId))
                )
                .collect(Collectors.toList());

        return drivers.stream()
                .map(driver -> {
                    DriverDTO dto = new DriverDTO();
                    dto.setDriverId(driver.getDriverId());
                    dto.setFullName(driver.getUser().getFullName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private boolean isDriverForTrip(Drivers driver, Integer tripId) {
        return tripsRepository.findByIdNotDeleted(tripId) != null &&
                tripsRepository.findByIdNotDeleted(tripId).getDriver().getDriverId().equals(driver.getDriverId());
    }

    public List<AssistantDTO> getAssistantForTrip(Integer tripId) {
        // Tương tự như getDriverForTrip
        List<Assistants> assistants = assistantsRepository.findAllNotDeleted().stream()
                .filter(assistant ->
                        assistant.getAssistantStatus() == Assistants.AssistantStatus.available ||
                                (assistant.getAssistantStatus() == Assistants.AssistantStatus.on_trip && isAssistantForTrip(assistant, tripId))
                )
                .collect(Collectors.toList());

        return assistants.stream()
                .map(assistant -> {
                    AssistantDTO dto = new AssistantDTO();
                    dto.setAssistantId(assistant.getAssistantId());
                    dto.setFullName(assistant.getUser().getFullName());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private boolean isAssistantForTrip(Assistants assistant, Integer tripId) {
        return tripsRepository.findByIdNotDeleted(tripId) != null &&
                tripsRepository.findByIdNotDeleted(tripId).getAssistant().getAssistantId().equals(assistant.getAssistantId());
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
    public TripDTO createTrip(TripDTO request) {
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
    public TripDTO updateTrip(Integer tripId, TripDTO request) {
        Trips existingTrip = getTripEntity(tripId);

        // Update driver if changed
        if (request.getDriverId() != null && !request.getDriverId().equals(existingTrip.getDriver().getDriverId())) {
            // Set previous driver as available
            existingTrip.getDriver().setDriverStatus(Drivers.DriverStatus.available);
            driversRepository.save(existingTrip.getDriver());

            // Set new driver
            Drivers newDriver = validateDriver(request.getDriverId());
            newDriver.setDriverStatus(Drivers.DriverStatus.on_trip);
            driversRepository.save(newDriver);
            existingTrip.setDriver(newDriver);
        }

        // Update assistant if changed
        if (request.getAssistantId() != null && !request.getAssistantId().equals(existingTrip.getAssistant().getAssistantId())) {
            // Set previous assistant as available
            existingTrip.getAssistant().setAssistantStatus(Assistants.AssistantStatus.available);
            assistantsRepository.save(existingTrip.getAssistant());

            // Set new assistant
            Assistants newAssistant = validateAssistant(request.getAssistantId());
            newAssistant.setAssistantStatus(Assistants.AssistantStatus.on_trip);
            assistantsRepository.save(newAssistant);
            existingTrip.setAssistant(newAssistant);
        }

        // Update scheduled times if provided
        if (request.getScheduledDeparture() != null) {
            existingTrip.setScheduledDeparture(request.getScheduledDeparture());
        }
        if (request.getScheduledArrival() != null) {
            existingTrip.setScheduledArrival(request.getScheduledArrival());
        }

        // Update actual times if provided
        if (request.getActualDeparture() != null) {
            existingTrip.setActualDeparture(request.getActualDeparture());
        }
        if (request.getActualArrival() != null) {
            existingTrip.setActualArrival(request.getActualArrival());
        }

        // Update trip status if provided
        if (request.getTripStatus() != null) {
            existingTrip.setTripStatus(Trips.TripStatus.valueOf(request.getTripStatus()));

            // If trip is completed or cancelled, set driver and assistant as available
            if (request.getTripStatus().equals("completed") || request.getTripStatus().equals("cancelled")) {
                existingTrip.getDriver().setDriverStatus(Drivers.DriverStatus.available);
                existingTrip.getAssistant().setAssistantStatus(Assistants.AssistantStatus.available);
                driversRepository.save(existingTrip.getDriver());
                assistantsRepository.save(existingTrip.getAssistant());
            }
        }

        // Update trip seats if provided
        if (request.getTripSeats() != null && !request.getTripSeats().isEmpty()) {
            for (TripSeatDTO seatUpdate : request.getTripSeats()) {
                TripSeats tripSeat = tripSeatsRepository.findByIdNotDeleted(seatUpdate.getTripSeatId());
                if (tripSeat != null) {
                    tripSeat.setTripSeatStatus(TripSeats.TripSeatStatus.valueOf(seatUpdate.getStatus()));
                    tripSeatsRepository.save(tripSeat);
                }
            }
        }

        existingTrip.setUpdatedAt(LocalDateTime.now());
        Trips updatedTrip = tripsRepository.save(existingTrip);
        return convertToDTO(updatedTrip);
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
    public TripDTO updateTripStatusAndTimes(Integer tripId, TripDTO request) {
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