// CustomersService.java
package com.example.be.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
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

    public Page<CustomerDTO> getAllCustomersDTO(
            Pageable pageable,
            String fullName,
            String phoneNumber,
            String email,
            String gender,
            String address,
            String dateOfBirth
    ) {
        Specification<Customers> spec = (root, query, criteriaBuilder) -> {
            List<jakarta.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (fullName != null && !fullName.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("user").get("fullName")),
                        "%" + fullName.toLowerCase() + "%"
                ));
            }

            if (phoneNumber != null && !phoneNumber.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("user").get("phoneNumber"),
                        "%" + phoneNumber + "%"
                ));
            }

            if (email != null && !email.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("user").get("email")),
                        "%" + email.toLowerCase() + "%"
                ));
            }

            if (gender != null && !gender.isEmpty()) {
                predicates.add(criteriaBuilder.equal(
                        root.get("user").get("gender"),
                        Users.Gender.valueOf(gender.toLowerCase())
                ));
            }

            if (address != null && !address.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        criteriaBuilder.lower(root.get("user").get("address")),
                        "%" + address.toLowerCase() + "%"
                ));
            }

            if (dateOfBirth != null && !dateOfBirth.isEmpty()) {
                try {
                    LocalDate date = LocalDate.parse(dateOfBirth);
                    predicates.add(criteriaBuilder.equal(root.get("user").get("dateOfBirth"), date));
                } catch (Exception e) {
                    System.err.println("Invalid date format: " + dateOfBirth);
                }
            }

            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<Customers> customerPage = customersRepository.findAll(spec, pageable);
        return customerPage.map(this::convertToDTO);
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

    public Customers getCustomerById(Integer customerId) {
        Customers customer = customersRepository.findByIdNotDeleted(customerId);
        if (customer == null) {
            throw new RuntimeException("Customer not found or has been deleted");
        }
        return customer;
    }
}