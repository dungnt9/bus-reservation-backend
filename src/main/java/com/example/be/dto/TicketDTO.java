package com.example.be.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TicketDTO {
    private String fullName;
    private String phoneNumber;
    private String routeName;
    private LocalDateTime scheduledDeparture;
    private LocalDateTime scheduledArrival;
    private String seatNumber;
    private Integer invoiceId;
    private LocalDateTime invoiceDate;
    private BigDecimal ticketPrice;
}
