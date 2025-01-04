package com.example.be.service;

import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import com.example.be.model.*;
import com.example.be.repository.*;
import com.example.be.dto.TripDTO;

@Service
public class TripSchedulerService {
    private final RouteSchedulesRepository routeSchedulesRepository;
    private final DriversRepository driversRepository;
    private final AssistantsRepository assistantsRepository;
    private final VehiclesRepository vehiclesRepository;
    private final TripsService tripsService;
    private final TripsRepository tripsRepository;

    // Lưu trữ lịch làm việc của từng tài nguyên
    private Map<Integer, Map<LocalDate, List<TimeSlot>>> driverSchedules = new HashMap<>();
    private Map<Integer, Map<LocalDate, List<TimeSlot>>> assistantSchedules = new HashMap<>();
    private Map<Integer, Map<LocalDate, List<TimeSlot>>> vehicleSchedules = new HashMap<>();

    public TripSchedulerService(
            RouteSchedulesRepository routeSchedulesRepository,
            DriversRepository driversRepository,
            AssistantsRepository assistantsRepository,
            VehiclesRepository vehiclesRepository,
            TripsService tripsService,
            TripsRepository tripsRepository
    ) {
        this.routeSchedulesRepository = routeSchedulesRepository;
        this.driversRepository = driversRepository;
        this.assistantsRepository = assistantsRepository;
        this.vehiclesRepository = vehiclesRepository;
        this.tripsService = tripsService;
        this.tripsRepository = tripsRepository;
    }

    @Transactional
    public List<TripDTO> createTripsForDateRange(LocalDate startDate, LocalDate endDate) {
        List<TripDTO> createdTrips = new ArrayList<>();

        // Reset lịch làm việc khi bắt đầu tạo mới
        driverSchedules.clear();
        assistantSchedules.clear();
        vehicleSchedules.clear();

        // Lấy danh sách lịch trình hoạt động
        List<RouteSchedules> activeSchedules = routeSchedulesRepository.findAllNotDeleted().stream()
                .filter(schedule -> schedule.getRoute().getRouteStatus() == Routes.RouteStatus.active)
                .collect(Collectors.toList());

        System.out.println("Tổng số lịch trình hoạt động: " + activeSchedules.size());

        // Lấy danh sách tài nguyên sẵn có
        List<Drivers> availableDrivers = driversRepository.findAllNotDeleted().stream()
                .filter(driver -> driver.getDriverStatus() == Drivers.DriverStatus.available)
                .collect(Collectors.toList());

        List<Assistants> availableAssistants = assistantsRepository.findAllNotDeleted().stream()
                .filter(assistant -> assistant.getAssistantStatus() == Assistants.AssistantStatus.available)
                .collect(Collectors.toList());

        List<Vehicles> availableVehicles = vehiclesRepository.findAllNotDeleted().stream()
                .filter(vehicle -> vehicle.getVehicleStatus() == Vehicles.VehicleStatus.active)
                .collect(Collectors.toList());

        System.out.println("Số lượng tài xế: " + availableDrivers.size());
        System.out.println("Số lượng phụ xe: " + availableAssistants.size());
        System.out.println("Số lượng xe: " + availableVehicles.size());

        // Duyệt từng ngày
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            final LocalDate processDate = currentDate;
            System.out.println("\nĐang xử lý ngày: " + processDate);

            // Lọc lịch trình cho ngày hiện tại
            List<RouteSchedules> schedulesForDay = activeSchedules.stream()
                    .filter(schedule -> {
                        boolean containsDay = schedule.getDaysOfWeek().contains(processDate.getDayOfWeek().name());
                        System.out.println("Lịch trình " + schedule.getScheduleId() +
                                " cho " + processDate.getDayOfWeek().name() + ": " + containsDay);
                        return containsDay;
                    })
                    .sorted(Comparator.comparing(RouteSchedules::getDepartureTime))
                    .collect(Collectors.toList());

            System.out.println("Số lịch trình cho ngày " + processDate + ": " + schedulesForDay.size());

            // Xử lý từng lịch trình
            for (RouteSchedules schedule : schedulesForDay) {
                try {
                    LocalDateTime departureTime = LocalDateTime.of(processDate, schedule.getDepartureTime());
                    LocalDateTime arrivalTime = departureTime.plusMinutes(schedule.getRoute().getEstimatedDuration());

                    System.out.println("\nĐang xử lý lịch trình ID " + schedule.getScheduleId() +
                            " khởi hành lúc " + departureTime);

                    // Tìm tài xế phù hợp
                    Optional<Drivers> selectedDriver = findAvailableDriver(
                            availableDrivers, processDate, departureTime, arrivalTime);

                    // Tìm phụ xe phù hợp
                    Optional<Assistants> selectedAssistant = findAvailableAssistant(
                            availableAssistants, processDate, departureTime, arrivalTime);

                    // Tìm xe phù hợp
                    Optional<Vehicles> selectedVehicle = findAvailableVehicle(
                            availableVehicles, processDate, departureTime, arrivalTime);

                    if (selectedDriver.isPresent() && selectedAssistant.isPresent() && selectedVehicle.isPresent()) {
                        // Tạo chuyến mới
                        TripDTO newTrip = new TripDTO();
                        newTrip.setScheduleId(schedule.getScheduleId());
                        newTrip.setDriverId(selectedDriver.get().getDriverId());
                        newTrip.setAssistantId(selectedAssistant.get().getAssistantId());
                        newTrip.setVehicleId(selectedVehicle.get().getVehicleId());
                        newTrip.setScheduledDeparture(departureTime);
                        newTrip.setScheduledArrival(arrivalTime);

                        // Lưu chuyến
                        TripDTO createdTrip = tripsService.createTrip(newTrip);
                        createdTrips.add(createdTrip);

                        // Cập nhật lịch làm việc
                        updateSchedule(driverSchedules, selectedDriver.get().getDriverId(),
                                processDate, departureTime, arrivalTime);

                        updateSchedule(assistantSchedules, selectedAssistant.get().getAssistantId(),
                                processDate, departureTime, arrivalTime);

                        updateSchedule(vehicleSchedules, selectedVehicle.get().getVehicleId(),
                                processDate, departureTime, arrivalTime);

                        System.out.println("Đã tạo chuyến thành công cho lịch trình " + schedule.getScheduleId() +
                                " với tài xế " + selectedDriver.get().getDriverId());
                    } else {
                        System.out.println("Không tìm thấy đủ tài nguyên cho lịch trình " + schedule.getScheduleId());
                    }
                } catch (Exception e) {
                    System.err.println("Lỗi khi tạo chuyến cho lịch trình " + schedule.getScheduleId() +
                            " ngày " + processDate + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }

            currentDate = currentDate.plusDays(1);
        }

        System.out.println("\nTổng số chuyến đã tạo: " + createdTrips.size());
        return createdTrips;
    }

    private Optional<Drivers> findAvailableDriver(
            List<Drivers> drivers,
            LocalDate date,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        return drivers.stream()
                .filter(driver -> isTimeSlotAvailable(driverSchedules, driver.getDriverId(),
                        date, startTime, endTime))
                .findFirst();
    }

    private Optional<Assistants> findAvailableAssistant(
            List<Assistants> assistants,
            LocalDate date,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        return assistants.stream()
                .filter(assistant -> isTimeSlotAvailable(assistantSchedules, assistant.getAssistantId(),
                        date, startTime, endTime))
                .findFirst();
    }

    private Optional<Vehicles> findAvailableVehicle(
            List<Vehicles> vehicles,
            LocalDate date,
            LocalDateTime startTime,
            LocalDateTime endTime) {
        return vehicles.stream()
                .filter(vehicle -> isTimeSlotAvailable(vehicleSchedules, vehicle.getVehicleId(),
                        date, startTime, endTime))
                .findFirst();
    }

    private boolean isTimeSlotAvailable(
            Map<Integer, Map<LocalDate, List<TimeSlot>>> schedules,
            Integer resourceId,
            LocalDate date,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        // Lấy lịch làm việc của tài nguyên trong ngày
        Map<LocalDate, List<TimeSlot>> resourceSchedule = schedules.computeIfAbsent(resourceId,
                k -> new HashMap<>());

        List<TimeSlot> daySchedule = resourceSchedule.computeIfAbsent(date,
                k -> new ArrayList<>());

        // Kiểm tra xem có slot nào chồng chéo không
        return daySchedule.stream().noneMatch(slot ->
                // Kiểm tra chồng chéo thời gian
                !(endTime.isBefore(slot.startTime) || startTime.isAfter(slot.endTime)) ||
                        // Kiểm tra khoảng cách 6 tiếng
                        Duration.between(slot.endTime, startTime).toHours() < 6
        );
    }

    private void updateSchedule(
            Map<Integer, Map<LocalDate, List<TimeSlot>>> schedules,
            Integer resourceId,
            LocalDate date,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        Map<LocalDate, List<TimeSlot>> resourceSchedule = schedules.computeIfAbsent(resourceId,
                k -> new HashMap<>());

        List<TimeSlot> daySchedule = resourceSchedule.computeIfAbsent(date,
                k -> new ArrayList<>());

        daySchedule.add(new TimeSlot(startTime, endTime));
        daySchedule.sort(Comparator.comparing(slot -> slot.startTime));
    }

    private static class TimeSlot {
        LocalDateTime startTime;
        LocalDateTime endTime;

        TimeSlot(LocalDateTime startTime, LocalDateTime endTime) {
            this.startTime = startTime;
            this.endTime = endTime;
        }
    }

    @Transactional
    public String deleteTripsInRange(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Trips> tripsToDelete = tripsRepository.findAll()
                .stream()
                .filter(trip -> {
                    LocalDateTime tripTime = trip.getScheduledDeparture();
                    return tripTime.isAfter(startDateTime) &&
                            tripTime.isBefore(endDateTime);
                })
                .collect(Collectors.toList());

        tripsToDelete.forEach(trip -> {
            Drivers driver = trip.getDriver();
            Assistants assistant = trip.getAssistant();

            driver.setDriverStatus(Drivers.DriverStatus.available);
            assistant.setAssistantStatus(Assistants.AssistantStatus.available);

            driversRepository.save(driver);
            assistantsRepository.save(assistant);

            trip.markAsDeleted();
            tripsRepository.save(trip);
        });

        System.out.println("Đã xóa " + tripsToDelete.size() + " chuyến xe");
        return String.format("Đã xóa %d chuyến xe trong khoảng thời gian từ %s đến %s",
                tripsToDelete.size(), startDate, endDate);
    }
}