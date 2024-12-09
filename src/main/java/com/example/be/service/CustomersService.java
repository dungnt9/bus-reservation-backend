package com.example.be.service;

import com.example.be.model.Customers;
import com.example.be.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomersService {

    @Autowired
    private CustomersRepository customersRepository;

    public Customers createCustomer(Customers customer) {
        customer.setCreatedAt(LocalDateTime.now());
        return customersRepository.save(customer);
    }

    public List<Customers> getAllCustomers() {
        return customersRepository.findAllNotDeleted();
    }

    public Customers getCustomerById(Integer customerId) {
        Customers customer = customersRepository.findByIdNotDeleted(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found or has been deleted");
        }
        return customer;
    }

    public Customers updateCustomer(Integer customerId, Customers customerDetails) {
        Customers customer = getCustomerById(customerId);

        customer.setUser(customerDetails.getUser());
        customer.setUpdatedAt(LocalDateTime.now());

        return customersRepository.save(customer);
    }

    public void deleteCustomer(Integer customerId) {
        Customers customer = getCustomerById(customerId);
        customer.markAsDeleted();
        customersRepository.save(customer);
    }
}