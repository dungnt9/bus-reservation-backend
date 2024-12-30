package com.example.be.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.example.be.dto.TicketDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.model.*;
import com.example.be.dto.InvoiceDTO;
import com.example.be.repository.*;

@Service
public class InvoicesService {

    private final InvoicesRepository invoicesRepository;
    private final CustomersRepository customersRepository;
    private final InvoiceDetailsRepository invoiceDetailsRepository;
    private final TripSeatsRepository tripSeatsRepository;
    private final TripsRepository tripsRepository;

    public InvoicesService(
            InvoicesRepository invoicesRepository,
            CustomersRepository customersRepository,
            InvoiceDetailsRepository invoiceDetailsRepository,
            TripSeatsRepository tripSeatsRepository,
            TripsRepository tripsRepository) {
        this.invoicesRepository = invoicesRepository;
        this.customersRepository = customersRepository;
        this.invoiceDetailsRepository = invoiceDetailsRepository;
        this.tripSeatsRepository = tripSeatsRepository;
        this.tripsRepository = tripsRepository;
    }

    @Transactional
    public InvoiceDTO createInvoice(Integer customerId, Integer tripId,
                                    List<Integer> selectedSeatNumbers, String paymentStatus, String paymentMethod) {

        // Validate customer
        Customers customer = customersRepository.findByIdNotDeleted(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found");
        }

        // Get trip with route info
        Trips trip = tripsRepository.findTripWithRouteById(tripId);
        if (trip == null) {
            throw new RuntimeException("Trip not found");
        }

        // Calculate total price
        BigDecimal ticketPrice = trip.getRouteSchedule().getRoute().getTicketPrice();
        BigDecimal totalPrice = ticketPrice.multiply(new BigDecimal(selectedSeatNumbers.size()));

        // Create invoice
        Invoices invoice = new Invoices();
        invoice.setCustomer(customer);
        invoice.setTotalPrice(totalPrice);
        invoice.setPaymentStatus(Invoices.PaymentStatus.valueOf(paymentStatus));
        invoice.setPaymentMethod(Invoices.PaymentMethod.valueOf(paymentMethod));
        invoice.setInvoiceDate(LocalDateTime.now());
        invoice.setCreatedAt(LocalDateTime.now());

        Invoices savedInvoice = invoicesRepository.save(invoice);

        // Create invoice details and update trip seats
        for (Integer seatNumber : selectedSeatNumbers) {
            TripSeats tripSeat = tripSeatsRepository.findByTripAndSeatNumber(tripId, seatNumber);
            if (tripSeat == null) {
                throw new RuntimeException("Trip seat not found: " + seatNumber);
            }
            if (tripSeat.getTripSeatStatus() != TripSeats.TripSeatStatus.available) {
                throw new RuntimeException("Seat " + seatNumber + " is not available");
            }

            // Create invoice detail
            InvoiceDetails detail = new InvoiceDetails();
            detail.setInvoice(savedInvoice);
            detail.setTripSeat(tripSeat);
            detail.setCreatedAt(LocalDateTime.now());
            invoiceDetailsRepository.save(detail);

            // Update seat status
            tripSeat.setTripSeatStatus(TripSeats.TripSeatStatus.booked);
            tripSeatsRepository.save(tripSeat);
        }

        return convertToDTO(savedInvoice);
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoicesRepository.findAllNotDeleted().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Page<InvoiceDTO> getAllInvoicesDTO(Pageable pageable) {
        Page<Invoices> invoicePage = invoicesRepository.findAllNotDeleted(pageable);
        List<InvoiceDTO> invoiceDTOs = invoicePage.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(
                invoiceDTOs,
                pageable,
                invoicePage.getTotalElements()
        );
    }

    public InvoiceDTO getInvoiceById(Integer invoiceId) {
        return convertToDTO(findInvoiceById(invoiceId));
    }

    @Transactional
    public InvoiceDTO updateInvoice(Integer invoiceId, String paymentStatus, String paymentMethod) {
        Invoices invoice = findInvoiceById(invoiceId);

        invoice.setPaymentStatus(Invoices.PaymentStatus.valueOf(paymentStatus));
        invoice.setPaymentMethod(Invoices.PaymentMethod.valueOf(paymentMethod));
        invoice.setUpdatedAt(LocalDateTime.now());

        return convertToDTO(invoicesRepository.save(invoice));
    }

    @Transactional
    public void deleteInvoice(Integer invoiceId) {
        Invoices invoice = findInvoiceById(invoiceId);

        // Soft delete invoice details and update trip seats
        List<InvoiceDetails> details = invoiceDetailsRepository.findByInvoiceId(invoiceId);
        for (InvoiceDetails detail : details) {
            // Update trip seat status back to available
            TripSeats tripSeat = detail.getTripSeat();
            tripSeat.setTripSeatStatus(TripSeats.TripSeatStatus.available);
            tripSeatsRepository.save(tripSeat);

            detail.markAsDeleted();
            invoiceDetailsRepository.save(detail);
        }

        // Soft delete invoice
        invoice.markAsDeleted();
        invoicesRepository.save(invoice);
    }

    private Invoices findInvoiceById(Integer invoiceId) {
        Invoices invoice = invoicesRepository.findByIdNotDeleted(invoiceId);
        if (invoice == null) {
            throw new RuntimeException("Invoice not found or has been deleted");
        }
        return invoice;
    }

    private InvoiceDTO convertToDTO(Invoices invoice) {
        InvoiceDTO dto = new InvoiceDTO();
        dto.setInvoiceId(invoice.getInvoiceId());

        // Get trip information from first invoice detail
        List<InvoiceDetails> details = invoiceDetailsRepository.findByInvoiceId(invoice.getInvoiceId());
        if (!details.isEmpty()) {
            TripSeats firstSeat = details.get(0).getTripSeat();
            Trips trip = firstSeat.getTrip();
            dto.setTripId(trip.getTripId());
            dto.setPlateNumber(firstSeat.getVehicleSeat().getVehicle().getPlateNumber());

            Routes route = trip.getRouteSchedule().getRoute();
            dto.setRouteName(route.getRouteName());
        }

        // Customer information
        Customers customer = invoice.getCustomer();
        dto.setCustomerId(customer.getCustomerId());
        dto.setFullName(customer.getUser().getFullName());
        dto.setPhoneNumber(customer.getUser().getPhoneNumber());

        // Get selected seats
        dto.setSelectedSeats(details.stream()
                .map(detail -> detail.getTripSeat().getVehicleSeat().getSeatNumber())
                .collect(Collectors.toList()));

        dto.setTotalPrice(invoice.getTotalPrice());
        dto.setPaymentStatus(invoice.getPaymentStatus().toString());
        dto.setPaymentMethod(invoice.getPaymentMethod().toString());
        dto.setInvoiceDate(invoice.getInvoiceDate());

        return dto;
    }

    public List<InvoiceDTO> getCustomerInvoices(Integer customerId) {
        return invoicesRepository.findByCustomerIdNotDeleted(customerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<TicketDTO> getCustomerTickets(
            Integer customerId,
            Integer invoiceId,
            String seatNumber) {

        List<Invoices> invoices;
        if (invoiceId != null) {
            // If invoiceId is provided, only get that specific invoice
            invoices = Collections.singletonList(
                    invoicesRepository.findByIdNotDeleted(invoiceId)
            );
        } else {
            // Otherwise get all customer's invoices
            invoices = invoicesRepository.findByCustomerIdNotDeleted(customerId);
        }

        List<TicketDTO> tickets = new ArrayList<>();

        for (Invoices invoice : invoices) {
            // Get invoice details
            List<InvoiceDetails> details = invoiceDetailsRepository.findByInvoiceId(invoice.getInvoiceId());

            for (InvoiceDetails detail : details) {
                // If seatNumber is provided, filter by it
                if (seatNumber != null &&
                        !detail.getTripSeat().getVehicleSeat().getSeatNumber().equals(seatNumber)) {
                    continue;
                }

                TicketDTO ticket = convertToTicketDTO(detail);
                tickets.add(ticket);
            }
        }

        // Sort by invoice date descending
        tickets.sort((t1, t2) -> t2.getInvoiceDate().compareTo(t1.getInvoiceDate()));

        return tickets;
    }

    private TicketDTO convertToTicketDTO(InvoiceDetails detail) {
        TicketDTO ticket = new TicketDTO();
        Invoices invoice = detail.getInvoice();

        // Set customer info
        ticket.setFullName(invoice.getCustomer().getUser().getFullName());
        ticket.setPhoneNumber(invoice.getCustomer().getUser().getPhoneNumber());

        // Set trip info
        TripSeats tripSeat = detail.getTripSeat();
        Trips trip = tripSeat.getTrip();
        Routes route = trip.getRouteSchedule().getRoute();

        ticket.setRouteName(route.getRouteName());
        ticket.setScheduledDeparture(trip.getScheduledDeparture());
        ticket.setScheduledArrival(trip.getScheduledArrival());
        ticket.setSeatNumber(tripSeat.getVehicleSeat().getSeatNumber());

        // Set invoice info
        ticket.setInvoiceId(invoice.getInvoiceId());
        ticket.setInvoiceDate(invoice.getInvoiceDate());
        ticket.setTicketPrice(route.getTicketPrice());

        return ticket;
    }
}