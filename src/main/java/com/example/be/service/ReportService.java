package com.example.be.service;

import com.example.be.model.Trips;
import org.springframework.stereotype.Service;
import com.example.be.repository.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {
    private final InvoicesRepository invoicesRepository;
    private final TripsRepository tripsRepository;
    private final CustomersRepository customersRepository;
    private final RoutesRepository routesRepository;

    public ReportService(
            InvoicesRepository invoicesRepository,
            TripsRepository tripsRepository,
            CustomersRepository customersRepository,
            RoutesRepository routesRepository
    ) {
        this.invoicesRepository = invoicesRepository;
        this.tripsRepository = tripsRepository;
        this.customersRepository = customersRepository;
        this.routesRepository = routesRepository;
    }

    public Map<String, Object> getDashboardData(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(23, 59, 59);

        Map<String, Object> data = new HashMap<>();

        // Revenue metrics
        data.put("totalRevenue", getTotalRevenue(startDateTime, endDateTime));
        data.put("revenueByRoute", getRevenueByRoute(startDateTime, endDateTime));
        data.put("revenueByPaymentMethod", getRevenueByPaymentMethod(startDateTime, endDateTime));
        data.put("revenueOverTime", getRevenueOverTime(startDateTime, endDateTime));

        // Trip metrics
        data.put("tripStats", getTripStats(startDateTime, endDateTime));
        data.put("tripStatusDistribution", getTripStatusDistribution(startDateTime, endDateTime));

        // Customer metrics
        data.put("newCustomers", getNewCustomers(startDateTime, endDateTime));
        data.put("newCustomersOverTime", getNewCustomersOverTime(startDateTime, endDateTime));

        // Time and distance metrics
        data.put("timeComparison", getTimeComparison(startDateTime, endDateTime));

        // Route performance
        data.put("tripsByRoute", getTripsByRoute(startDateTime, endDateTime));

        return data;
    }

    private BigDecimal getTotalRevenue(LocalDateTime start, LocalDateTime end) {
        return invoicesRepository.findAll().stream()
                .filter(i -> i.getInvoiceDate().isAfter(start) && i.getInvoiceDate().isBefore(end))
                .map(i -> i.getTotalPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Map<String, BigDecimal> getRevenueByRoute(LocalDateTime start, LocalDateTime end) {
        // Implementation logic for revenue by route
        // You'll need to join invoices with trips and routes
        return new HashMap<>(); // Placeholder
    }

    private Map<String, BigDecimal> getRevenueByPaymentMethod(LocalDateTime start, LocalDateTime end) {
        return invoicesRepository.findAll().stream()
                .filter(i -> i.getInvoiceDate().isAfter(start) && i.getInvoiceDate().isBefore(end))
                .collect(Collectors.groupingBy(
                        i -> i.getPaymentMethod().toString(),
                        Collectors.mapping(
                                i -> i.getTotalPrice(),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }

    private Map<LocalDate, BigDecimal> getRevenueOverTime(LocalDateTime start, LocalDateTime end) {
        return invoicesRepository.findAll().stream()
                .filter(i -> i.getInvoiceDate().isAfter(start) && i.getInvoiceDate().isBefore(end))
                .collect(Collectors.groupingBy(
                        i -> i.getInvoiceDate().toLocalDate(),
                        Collectors.mapping(
                                i -> i.getTotalPrice(),
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )
                ));
    }

    private Map<String, Long> getTripStats(LocalDateTime start, LocalDateTime end) {
        Map<String, Long> stats = new HashMap<>();
        List<Trips> trips = tripsRepository.findAll().stream()
                .filter(t -> t.getScheduledDeparture().isAfter(start) && t.getScheduledDeparture().isBefore(end))
                .collect(Collectors.toList());

        stats.put("total", (long) trips.size());
        stats.put("completed", trips.stream().filter(t -> t.getTripStatus() == Trips.TripStatus.completed).count());
        stats.put("cancelled", trips.stream().filter(t -> t.getTripStatus() == Trips.TripStatus.cancelled).count());

        return stats;
    }

    private Map<String, Long> getTripStatusDistribution(LocalDateTime start, LocalDateTime end) {
        return tripsRepository.findAll().stream()
                .filter(t -> t.getScheduledDeparture().isAfter(start) && t.getScheduledDeparture().isBefore(end))
                .collect(Collectors.groupingBy(
                        t -> t.getTripStatus().toString(),
                        Collectors.counting()
                ));
    }

    private long getNewCustomers(LocalDateTime start, LocalDateTime end) {
        return customersRepository.findAll().stream()
                .filter(c -> c.getCreatedAt().isAfter(start) && c.getCreatedAt().isBefore(end))
                .count();
    }

    private Map<LocalDate, Long> getNewCustomersOverTime(LocalDateTime start, LocalDateTime end) {
        return customersRepository.findAll().stream()
                .filter(c -> c.getCreatedAt().isAfter(start) && c.getCreatedAt().isBefore(end))
                .collect(Collectors.groupingBy(
                        c -> c.getCreatedAt().toLocalDate(),
                        Collectors.counting()
                ));
    }

    private Map<String, Map<String, Double>> getTimeComparison(LocalDateTime start, LocalDateTime end) {
        return tripsRepository.findAll().stream()
                .filter(t -> t.getScheduledDeparture().isAfter(start) && t.getScheduledDeparture().isBefore(end))
                .collect(Collectors.groupingBy(
                        t -> t.getRouteSchedule().getRoute().getRouteName(),
                        Collectors.collectingAndThen(
                                Collectors.toList(),
                                trips -> {
                                    Map<String, Double> times = new HashMap<>();
                                    double scheduledAvg = trips.stream()
                                            .mapToLong(t -> java.time.Duration.between(
                                                    t.getScheduledDeparture(),
                                                    t.getScheduledArrival()
                                            ).toMinutes())
                                            .average()
                                            .orElse(0.0);
                                    times.put("scheduled", scheduledAvg);

                                    double actualAvg = trips.stream()
                                            .filter(t -> t.getActualDeparture() != null && t.getActualArrival() != null)
                                            .mapToLong(t -> java.time.Duration.between(
                                                    t.getActualDeparture(),
                                                    t.getActualArrival()
                                            ).toMinutes())
                                            .average()
                                            .orElse(0.0);
                                    times.put("actual", actualAvg);

                                    return times;
                                }
                        )
                ));
    }

    private Map<String, Long> getTripsByRoute(LocalDateTime start, LocalDateTime end) {
        return tripsRepository.findAll().stream()
                .filter(t -> t.getScheduledDeparture().isAfter(start) && t.getScheduledDeparture().isBefore(end))
                .collect(Collectors.groupingBy(
                        t -> t.getRouteSchedule().getRoute().getRouteName(),
                        Collectors.counting()
                ));
    }
}