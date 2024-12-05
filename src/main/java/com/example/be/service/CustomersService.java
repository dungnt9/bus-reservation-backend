package com.example.be.service;

import com.example.be.model.Customers;
import com.example.be.repository.CustomersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomersService {

    @Autowired
    private CustomersRepository customersRepository;

    public Customers createCustomer(Customers customer) {
        return customersRepository.save(customer);
    }

    public List<Customers> getAllCustomers() {
        return customersRepository.findAll();
    }

    public Customers getCustomerById(Integer customerId) {
        return customersRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
    }

    public void deleteCustomer(Integer customerId) {
        Customers customer = getCustomerById(customerId);
        customersRepository.delete(customer);
    }

    public Customers updateCustomer(Integer customerId, Customers customerDetails) {
        Customers customer = getCustomerById(customerId);
        
        // Update the user reference instead of user ID
        customer.setUser (customerDetails.getUser());
    
        return customersRepository.save(customer);
    }
}