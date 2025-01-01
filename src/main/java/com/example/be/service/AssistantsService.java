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

import com.example.be.dto.AssistantDTO;
import com.example.be.model.Assistants;
import com.example.be.model.Users;
import com.example.be.repository.AssistantsRepository;
import com.example.be.repository.UsersRepository;

@Service
public class AssistantsService {

    private final AssistantsRepository assistantsRepository;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    // Constructor injection
    public AssistantsService(AssistantsRepository assistantsRepository,
                             UsersRepository usersRepository,
                             UsersService usersService) {
        this.assistantsRepository = assistantsRepository;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    protected AssistantDTO convertToDTO(Assistants assistant) {
        AssistantDTO dto = new AssistantDTO();
        dto.setAssistantId(assistant.getAssistantId());
        dto.setUserId(assistant.getUser().getUserId());
        dto.setFullName(assistant.getUser().getFullName());
        dto.setPhoneNumber(assistant.getUser().getPhoneNumber());
        dto.setEmail(assistant.getUser().getEmail());
        dto.setPassword_hash(assistant.getUser().getPassword_hash());
        dto.setGender(assistant.getUser().getGender() != null ? assistant.getUser().getGender().toString() : null);
        dto.setAddress(assistant.getUser().getAddress());
        dto.setDateOfBirth(assistant.getUser().getDateOfBirth());
        dto.setAssistantStatus(assistant.getAssistantStatus().toString());
        return dto;
    }

    public Page<AssistantDTO> getAllAssistantsDTO(
            Pageable pageable,
            String fullName,
            String phoneNumber,
            String email,
            String gender,
            String address,
            String dateOfBirth,
            String assistantStatus
    ) {
        Specification<Assistants> spec = (root, query, criteriaBuilder) -> {
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

            if (assistantStatus != null && !assistantStatus.isEmpty()) {
                try {
                    Assistants.AssistantStatus status = Assistants.AssistantStatus.valueOf(assistantStatus.toLowerCase());
                    predicates.add(criteriaBuilder.equal(root.get("assistantStatus"), status));
                } catch (IllegalArgumentException e) {
                    System.err.println("Invalid assistant status: " + assistantStatus);
                }
            }

            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));
            return criteriaBuilder.and(predicates.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };

        Page<Assistants> assistantPage = assistantsRepository.findAll(spec, pageable);
        return assistantPage.map(this::convertToDTO);
    }

    public AssistantDTO getAssistantDTOById(Integer assistantId) {
        return convertToDTO(getAssistantById(assistantId));
    }

    public AssistantDTO createAssistant(Assistants assistant) {
        return convertToDTO(createAssistantEntity(assistant));
    }

    @Transactional
    protected Assistants createAssistantEntity(Assistants assistant) {
        validateAssistantFields(assistant);
        Users user = usersService.createUserForAssistant(assistant.getUser());
        assistant.setUser(user);
        assistant.setCreatedAt(LocalDateTime.now());
        assistant.setAssistantStatus(Assistants.AssistantStatus.available);
        return assistantsRepository.save(assistant);
    }

    protected void validateAssistantFields(Assistants assistant) {
        if (assistant == null) {
            throw new IllegalArgumentException("Assistant information cannot be null");
        }

        // Validate user fields
        Users user = assistant.getUser();
        if (user == null ||
                !StringUtils.hasText(user.getFullName()) ||
                !StringUtils.hasText(user.getPhoneNumber())) {
            throw new IllegalArgumentException("User full name and phone number are required");
        }
    }

    public AssistantDTO updateAssistant(Integer assistantId, Assistants assistantDetails) {
        return convertToDTO(updateAssistantEntity(assistantId, assistantDetails));
    }

    @Transactional
    protected Assistants updateAssistantEntity(Integer assistantId, Assistants assistantDetails) {
        Assistants existingAssistant = getAssistantById(assistantId);
        Users updatedUser = usersService.updateUserForAssistant(
                existingAssistant.getUser().getUserId(),
                assistantDetails.getUser()
        );
        existingAssistant.setAssistantStatus(assistantDetails.getAssistantStatus());
        existingAssistant.setUpdatedAt(LocalDateTime.now());
        return assistantsRepository.save(existingAssistant);
    }

    public Assistants getAssistantById(Integer assistantId) {
        Assistants assistant = assistantsRepository.findByIdNotDeleted(assistantId);
        if (assistant == null) {
            throw new RuntimeException("Assistant not found or has been deleted");
        }
        return assistant;
    }
}