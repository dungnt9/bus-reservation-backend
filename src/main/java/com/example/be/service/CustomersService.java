package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.be.model.Customers;
import com.example.be.model.Users;
import com.example.be.repository.CustomersRepository;
import com.example.be.repository.UsersRepository;

@Service
public class CustomersService {

    private final CustomersRepository customersRepository;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    // Constructor injection
    public CustomersService(
            CustomersRepository customersRepository,
            UsersRepository usersRepository,
            UsersService usersService
    ) {
        this.customersRepository = customersRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    @Transactional
    public Customers createCustomer(Customers customer) {
        // Validate required fields
        validateCustomerFields(customer);

        // First, create the user associated with the customer
        Users user = usersService.createUserForCustomer(customer.getUser());

        // Set the created user to the customer
        customer.setUser(user);
        customer.setCreatedAt(LocalDateTime.now());

        return customersRepository.save(customer);
    }

    private void validateCustomerFields(Customers customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer information cannot be null");
        }

        // Validate user fields
        Users user = customer.getUser();
        if (user == null ||
                !StringUtils.hasText(user.getFullName()) ||
                !StringUtils.hasText(user.getPhoneNumber())) {
            throw new IllegalArgumentException("User full name and phone number are required");
        }
    }

    @Transactional
    public Customers updateCustomer(Integer customerId, Customers customerDetails) {
        // Retrieve existing customer
        Customers existingCustomer = getCustomerById(customerId);

        // Update user details first
        Users updatedUser = usersService.updateUserForCustomer(
                existingCustomer.getUser().getUserId(),
                customerDetails.getUser()
        );

        // Update customer-specific details
        existingCustomer.setUser(updatedUser);
        existingCustomer.setUpdatedAt(LocalDateTime.now());

        // Save updated customer
        return customersRepository.save(existingCustomer);
    }

    @Transactional
    public void deleteCustomer(Integer customerId) {
        Customers customer = getCustomerById(customerId);

        // Soft delete customer
        customer.markAsDeleted();
        customersRepository.save(customer);

        // Soft delete associated user
        usersService.softDeleteUserForCustomer(customer.getUser().getUserId());
    }

    public Customers getCustomerById(Integer customerId) {
        Customers customer = customersRepository.findByIdNotDeleted(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found or has been deleted");
        }
        return customer;
    }

    public List<Customers> getAllCustomers() {
        return customersRepository.findAllNotDeleted();
    }
}