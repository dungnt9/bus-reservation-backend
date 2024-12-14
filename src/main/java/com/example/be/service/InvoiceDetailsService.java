package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.be.model.InvoiceDetails;
import com.example.be.repository.InvoiceDetailsRepository;

@Service
public class InvoiceDetailsService {

    private final InvoiceDetailsRepository invoiceDetailsRepository;

    // Constructor injection
    public InvoiceDetailsService(InvoiceDetailsRepository invoiceDetailsRepository) {
        this.invoiceDetailsRepository = invoiceDetailsRepository;
    }

    public InvoiceDetails createInvoiceDetail(InvoiceDetails invoiceDetail) {
        invoiceDetail.setCreatedAt(LocalDateTime.now());
        return invoiceDetailsRepository.save(invoiceDetail);
    }

    public List<InvoiceDetails> getAllInvoiceDetails() {
        return invoiceDetailsRepository.findAllNotDeleted();
    }

    public InvoiceDetails getInvoiceDetailById(Integer detailId) {
        InvoiceDetails invoiceDetail = invoiceDetailsRepository.findByIdNotDeleted(detailId);
        if (invoiceDetail == null) {
            throw new RuntimeException("Invoice detail not found or has been deleted");
        }
        return invoiceDetail;
    }

    public InvoiceDetails updateInvoiceDetail(Integer detailId, InvoiceDetails invoiceDetailDetails) {
        InvoiceDetails invoiceDetail = getInvoiceDetailById(detailId);

        invoiceDetail.setInvoice(invoiceDetailDetails.getInvoice());
        invoiceDetail.setTripSeat(invoiceDetailDetails.getTripSeat());
        invoiceDetail.setUpdatedAt(LocalDateTime.now());

        return invoiceDetailsRepository.save(invoiceDetail);
    }

    public void deleteInvoiceDetail(Integer detailId) {
        InvoiceDetails invoiceDetail = getInvoiceDetailById(detailId);
        invoiceDetail.markAsDeleted();
        invoiceDetailsRepository.save(invoiceDetail);
    }
}