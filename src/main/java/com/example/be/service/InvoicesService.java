package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.be.model.Customers;
import com.example.be.model.Invoices;
import com.example.be.model.Users;
import com.example.be.dto.InvoiceDTO;
import com.example.be.repository.CustomersRepository;
import com.example.be.repository.InvoicesRepository;
import com.example.be.repository.UsersRepository;

@Service
public class InvoicesService {

    private final InvoicesRepository invoicesRepository;
    private final CustomersRepository customersRepository;
    private final UsersRepository usersRepository;

    // Constructor injection
    public InvoicesService(
            InvoicesRepository invoicesRepository,
            CustomersRepository customersRepository,
            UsersRepository usersRepository
    ) {
        this.invoicesRepository = invoicesRepository;
        this.customersRepository = customersRepository;
        this.usersRepository = usersRepository;
    }

    @Transactional
    public InvoiceDTO createInvoice(Invoices invoice) {
        invoice.setCreatedAt(LocalDateTime.now());
        invoice.setInvoiceDate(LocalDateTime.now());
        Invoices savedInvoice = invoicesRepository.save(invoice);
        return mapToInvoiceDTO(savedInvoice);
    }

    public List<InvoiceDTO> getAllInvoices() {
        return invoicesRepository.findAllNotDeleted().stream()
                .map(this::mapToInvoiceDTO)
                .collect(Collectors.toList());
    }

    public InvoiceDTO getInvoiceById(Integer invoiceId) {
        Invoices invoice = invoicesRepository.findByIdNotDeleted(invoiceId);
        if (invoice == null) {
            throw new RuntimeException("Invoice not found or has been deleted");
        }
        return mapToInvoiceDTO(invoice);
    }

    @Transactional
    public InvoiceDTO updateInvoice(Integer invoiceId, Invoices invoiceDetails) {
        Invoices invoice = invoicesRepository.findByIdNotDeleted(invoiceId);
        if (invoice == null) {
            throw new RuntimeException("Invoice not found or has been deleted");
        }

        // Find the customer by ID
        Customers customer = customersRepository.findById(invoiceDetails.getCustomer().getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        invoice.setCustomer(customer);
        invoice.setTotalPrice(invoiceDetails.getTotalPrice());
        invoice.setPaymentStatus(invoiceDetails.getPaymentStatus());
        invoice.setPaymentMethod(invoiceDetails.getPaymentMethod());
        invoice.setInvoiceDate(invoiceDetails.getInvoiceDate());
        invoice.setUpdatedAt(LocalDateTime.now());

        Invoices updatedInvoice = invoicesRepository.save(invoice);
        return mapToInvoiceDTO(updatedInvoice);
    }

    @Transactional
    public void deleteInvoice(Integer invoiceId) {
        Invoices invoice = invoicesRepository.findByIdNotDeleted(invoiceId);
        if (invoice == null) {
            throw new RuntimeException("Invoice not found or has been deleted");
        }
        invoice.markAsDeleted();
        invoicesRepository.save(invoice);
    }

    // Helper method to map Invoice to InvoiceDTO
    private InvoiceDTO mapToInvoiceDTO(Invoices invoice) {
        InvoiceDTO.CustomerInfo customerInfo = createCustomerInfo(invoice.getCustomer());

        return new InvoiceDTO(
                invoice.getInvoiceId(),
                invoice.getCustomer() != null ? invoice.getCustomer().getCustomerId() : null,  // Add this line
                customerInfo,
                invoice.getTotalPrice(),
                invoice.getPaymentStatus().toString(),
                invoice.getPaymentMethod(),
                invoice.getInvoiceDate()
        );
    }

    // Helper method to create CustomerInfo, handling soft-deleted customers
    private InvoiceDTO.CustomerInfo createCustomerInfo(Customers customer) {
        if (customer == null || customer.getDeletedAt() != null) {
            return new InvoiceDTO.CustomerInfo(
                    null,
                    "Unavailable",
                    "Unavailable"
            );
        }

        Users user = customer.getUser();
        if (user == null || user.getDeletedAt() != null) {
            return new InvoiceDTO.CustomerInfo(
                    customer.getCustomerId(),
                    "Unavailable",
                    "Unavailable"
            );
        }

        return new InvoiceDTO.CustomerInfo(
                user.getUserId(),
                user.getFullName(),
                user.getPhoneNumber()
        );
    }
}