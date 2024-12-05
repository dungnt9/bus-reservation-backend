package com.example.be.service;

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
        return invoicesRepository.save(invoice);
    }

    public List<Invoices> getAllInvoices() {
        return invoicesRepository.findAll();
    }

    public Invoices getInvoiceById(Integer invoiceId) {
        return invoicesRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
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

        return invoicesRepository.save(invoice);
    }

    public void deleteInvoice(Integer invoiceId) {
        Invoices invoice = getInvoiceById(invoiceId);
        invoicesRepository.delete(invoice);
    }
}