package com.example.be.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class InvoiceDTO {
    private Integer invoiceId;
    private Integer tripId;
    private String routeName;
    private String plateNumber;
    private Integer customerId;
    private String fullName;
    private String phoneNumber;
    private List<String> selectedSeats;
    private BigDecimal totalPrice;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime invoiceDate;
}