package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.be.model.Customers;
import com.example.be.model.Invoices;
import com.example.be.repository.CustomersRepository;
import com.example.be.repository.InvoicesRepository;

@Service
public class InvoicesService {

    @Autowired
    private InvoicesRepository invoicesRepository;

    @Autowired
    private CustomersRepository customersRepository;

    public Invoices createInvoice(Invoices invoice) {
        invoice.setCreatedAt(LocalDateTime.now());
        return invoicesRepository.save(invoice);
    }

    public List<Invoices> getAllInvoices() {
        return invoicesRepository.findAllNotDeleted();
    }

    public Invoices getInvoiceById(Integer invoiceId) {
        Invoices invoice = invoicesRepository.findByIdNotDeleted(invoiceId);
        if (invoice == null) {
            throw new RuntimeException("Invoice not found or has been deleted");
        }
        return invoice;
    }

    public Invoices updateInvoice(Integer invoiceId, Invoices invoiceDetails) {
        Invoices invoice = getInvoiceById(invoiceId);

        // Find the customer by ID
        Customers customer = customersRepository.findById(invoiceDetails.getCustomer().getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        invoice.setCustomer(customer);
        invoice.setTotalPrice(invoiceDetails.getTotalPrice());
        invoice.setPaymentStatus(invoiceDetails.getPaymentStatus());
        invoice.setPaymentMethod(invoiceDetails.getPaymentMethod());
        invoice.setInvoiceDate(invoiceDetails.getInvoiceDate());
        invoice.setUpdatedAt(LocalDateTime.now());

        return invoicesRepository.save(invoice);
    }

    public void deleteInvoice(Integer invoiceId) {
        Invoices invoice = getInvoiceById(invoiceId);
        invoice.markAsDeleted();
        invoicesRepository.save(invoice);
    }
}