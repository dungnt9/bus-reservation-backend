package com.example.be.controller;

import java.util.List;
import java.util.Optional;

import com.example.be.dto.TicketDTO;
import com.example.be.model.*;
import com.example.be.repository.CustomersRepository;
import com.example.be.security.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.example.be.dto.InvoiceDTO;
import com.example.be.dto.CreateInvoiceRequest;
import com.example.be.service.InvoicesService;
import com.example.be.repository.InvoiceDetailsRepository;

@RestController
@RequestMapping("/api/invoices")
public class InvoicesController {

    private final InvoicesService invoicesService;
    private final JwtUtil jwtUtil;
    private final InvoiceDetailsRepository invoiceDetailsRepository;
    private final CustomersRepository customersRepository;

    public InvoicesController(
            InvoicesService invoicesService,
            JwtUtil jwtUtil,
            CustomersRepository customersRepository,
            InvoiceDetailsRepository invoiceDetailsRepository) {
        this.invoicesService = invoicesService;
        this.jwtUtil = jwtUtil;
        this.customersRepository = customersRepository;
        this.invoiceDetailsRepository = invoiceDetailsRepository;
    }

    @GetMapping
    public ResponseEntity<Page<InvoiceDTO>> getAllInvoices(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(invoicesService.getAllInvoicesDTO(pageable));
    }

    @PostMapping("/customer/{userId}")
    public ResponseEntity<InvoiceDTO> createCustomerInvoice(
            @PathVariable Integer userId,
            @RequestBody CreateInvoiceRequest request) {
        try {
            // Tìm customer theo userId
            Optional<Customers> customerOpt = customersRepository.findByUserId(userId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Tạo invoice với customerId
            return ResponseEntity.ok(invoicesService.createInvoice(
                    customerOpt.get().getCustomerId(),
                    request.getTripId(),
                    request.getSelectedSeats(),
                    request.getPaymentStatus(),
                    request.getPaymentMethod()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDTO> getInvoiceById(@PathVariable Integer invoiceId) {
        return ResponseEntity.ok(invoicesService.getInvoiceById(invoiceId));
    }

    @PostMapping
    public ResponseEntity<InvoiceDTO> createInvoice(@RequestBody CreateInvoiceRequest request) {
        return ResponseEntity.ok(invoicesService.createInvoice(
                request.getCustomerId(),
                request.getTripId(),
                request.getSelectedSeats(),
                request.getPaymentStatus(),
                request.getPaymentMethod()
        ));
    }

    @PutMapping("/{invoiceId}")
    public ResponseEntity<InvoiceDTO> updateInvoice(
            @PathVariable Integer invoiceId,
            @RequestParam String paymentStatus,
            @RequestParam String paymentMethod) {
        return ResponseEntity.ok(invoicesService.updateInvoice(invoiceId, paymentStatus, paymentMethod));
    }

    @GetMapping("/customer/{userId}")
    public ResponseEntity<List<InvoiceDTO>> getCustomerInvoices(@PathVariable Integer userId) {
        try {
            // Find customer by userId
            Optional<Customers> customerOpt = customersRepository.findByUserId(userId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Get customer's invoices
            List<InvoiceDTO> invoices = invoicesService.getCustomerInvoices(customerOpt.get().getCustomerId());
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/customer/{userId}/tickets")
    public ResponseEntity<List<TicketDTO>> getCustomerTickets(
            @PathVariable Integer userId,
            @RequestParam(required = false) Integer invoiceId,
            @RequestParam(required = false) String seatNumber) {
        try {
            // Find the customer
            Optional<Customers> customerOpt = customersRepository.findByUserId(userId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Get tickets
            List<TicketDTO> tickets = invoicesService.getCustomerTickets(
                    customerOpt.get().getCustomerId(),
                    invoiceId,
                    seatNumber
            );

            return ResponseEntity.ok(tickets);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/customer/{userId}/tickets/{ticketId}")
    public ResponseEntity<TicketDTO> getTicketDetail(
            @PathVariable Integer userId,
            @PathVariable Integer ticketId) {
        try {
            // First find the customer
            Optional<Customers> customerOpt = customersRepository.findByUserId(userId);
            if (customerOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Find the invoice detail
            Optional<InvoiceDetails> detailOpt = invoiceDetailsRepository.findById(ticketId);
            if (detailOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            InvoiceDetails detail = detailOpt.get();
            Invoices invoice = detail.getInvoice();

            // Verify that this ticket belongs to the requesting user
            if (!invoice.getCustomer().getCustomerId().equals(customerOpt.get().getCustomerId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }

            TicketDTO ticket = new TicketDTO();

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

            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}