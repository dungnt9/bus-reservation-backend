package com.example.be.dto;

import java.util.List;
import lombok.Data;

@Data
public class CreateInvoiceRequest {
    private Integer customerId;
    private Integer tripId;
    private List<Integer> selectedSeats;
    private String paymentStatus;
    private String paymentMethod;
}