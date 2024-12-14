package com.example.be.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class InvoiceDTO {
    private Integer invoiceId;
    private Integer customerId;  // Add this line
    private CustomerInfo customer;
    private BigDecimal totalPrice;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime invoiceDate;

    // Inner class for customer information
    public static class CustomerInfo {
        private Integer userId;
        private String fullName;
        private String phoneNumber;

        // Constructors
        public CustomerInfo() {}

        public CustomerInfo(Integer userId, String fullName, String phoneNumber) {
            this.userId = userId;
            this.fullName = fullName;
            this.phoneNumber = phoneNumber;
        }

        // Getters and Setters
        public Integer getUserId() { return userId; }
        public void setUserId(Integer userId) { this.userId = userId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    }

    // Constructors
    public InvoiceDTO() {}

    public InvoiceDTO(Integer invoiceId, Integer customerId, CustomerInfo customer, BigDecimal totalPrice,
                      String paymentStatus, String paymentMethod, LocalDateTime invoiceDate) {
        this.invoiceId = invoiceId;
        this.customerId = customerId;
        this.customer = customer;
        this.totalPrice = totalPrice;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.invoiceDate = invoiceDate;
    }

    // Getters and Setters
    public Integer getInvoiceId() { return invoiceId; }
    public void setInvoiceId(Integer invoiceId) { this.invoiceId = invoiceId; }
    public Integer getCustomerId() { return customerId; }
    public void setCustomerId(Integer customerId) { this.customerId = customerId; }
    public CustomerInfo getCustomer() { return customer; }
    public void setCustomer(CustomerInfo customer) { this.customer = customer; }
    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public LocalDateTime getInvoiceDate() { return invoiceDate; }
    public void setInvoiceDate(LocalDateTime invoiceDate) { this.invoiceDate = invoiceDate; }
}