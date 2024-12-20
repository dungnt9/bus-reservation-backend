// CustomersService.java
package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.example.be.dto.CustomerDTO;
import com.example.be.model.Customers;
import com.example.be.model.Users;
import com.example.be.repository.CustomersRepository;
import com.example.be.repository.UsersRepository;

@Service
public class CustomersService {

    private final CustomersRepository customersRepository;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    public CustomersService(CustomersRepository customersRepository,
                            UsersRepository usersRepository,
                            UsersService usersService) {
        this.customersRepository = customersRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    protected CustomerDTO convertToDTO(Customers customer) {
        CustomerDTO dto = new CustomerDTO();
        dto.setCustomerId(customer.getCustomerId());
        dto.setUserId(customer.getUser().getUserId());
        dto.setFullName(customer.getUser().getFullName());
        dto.setPhoneNumber(customer.getUser().getPhoneNumber());
        dto.setEmail(customer.getUser().getEmail());
        dto.setPassword_hash(customer.getUser().getPassword_hash());
        dto.setGender(customer.getUser().getGender() != null ? customer.getUser().getGender().toString() : null);
        dto.setAddress(customer.getUser().getAddress());
        dto.setDateOfBirth(customer.getUser().getDateOfBirth());
        return dto;
    }

    public List<CustomerDTO> getAllCustomersDTO() {
        return getAllCustomers().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CustomerDTO getCustomerDTOById(Integer customerId) {
        return convertToDTO(getCustomerById(customerId));
    }

    public CustomerDTO createCustomer(Customers customer) {
        return convertToDTO(createCustomerEntity(customer));
    }

    @Transactional
    protected Customers createCustomerEntity(Customers customer) {
        validateCustomerFields(customer);
        Users user = usersService.createUserForCustomer(customer.getUser());
        customer.setUser(user);
        customer.setCreatedAt(LocalDateTime.now());
        return customersRepository.save(customer);
    }

    protected void validateCustomerFields(Customers customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer information cannot be null");
        }
        Users user = customer.getUser();
        if (user == null ||
                !StringUtils.hasText(user.getFullName()) ||
                !StringUtils.hasText(user.getPhoneNumber())) {
            throw new IllegalArgumentException("User full name and phone number are required");
        }
    }

    public CustomerDTO updateCustomer(Integer customerId, Customers customerDetails) {
        return convertToDTO(updateCustomerEntity(customerId, customerDetails));
    }

    @Transactional
    protected Customers updateCustomerEntity(Integer customerId, Customers customerDetails) {
        Customers existingCustomer = getCustomerById(customerId);
        Users updatedUser = usersService.updateUserForCustomer(
                existingCustomer.getUser().getUserId(),
                customerDetails.getUser()
        );
        existingCustomer.setUpdatedAt(LocalDateTime.now());
        return customersRepository.save(existingCustomer);
    }

    @Transactional
    public void deleteCustomer(Integer customerId) {
        Customers customer = getCustomerById(customerId);
        customer.markAsDeleted();
        customersRepository.save(customer);
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