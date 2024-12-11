package com.example.be.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

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

    @Transactional
    public Assistants createAssistant(Assistants assistant) {
        // Validate required fields
        validateAssistantFields(assistant);

        // First, create the user associated with the assistant
        Users user = usersService.createUserForAssistant(assistant.getUser());

        // Set the created user to the assistant
        assistant.setUser(user);
        assistant.setCreatedAt(LocalDateTime.now());
        assistant.setAssistantStatus(Assistants.AssistantStatus.available); // Default status

        return assistantsRepository.save(assistant);
    }

    private void validateAssistantFields(Assistants assistant) {
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

    @Transactional
    public Assistants updateAssistant(Integer assistantId, Assistants assistantDetails) {
        // Retrieve existing assistant
        Assistants existingAssistant = getAssistantById(assistantId);

        // Update user details first
        Users updatedUser = usersService.updateUserForAssistant(
                existingAssistant.getUser().getUserId(),
                assistantDetails.getUser()
        );

        // Update assistant-specific details
        existingAssistant.setAssistantStatus(assistantDetails.getAssistantStatus());
        existingAssistant.setUpdatedAt(LocalDateTime.now());

        // Save updated assistant
        return assistantsRepository.save(existingAssistant);
    }

    @Transactional
    public void deleteAssistant(Integer assistantId) {
        Assistants assistant = getAssistantById(assistantId);

        // Soft delete assistant
        assistant.markAsDeleted();
        assistantsRepository.save(assistant);

        // Soft delete associated user
        usersService.softDeleteUserForAssistant(assistant.getUser().getUserId());
    }

    public Assistants getAssistantById(Integer assistantId) {
        Assistants assistant = assistantsRepository.findByIdNotDeleted(assistantId);
        if (assistant == null) {
            throw new RuntimeException("Assistant not found or has been deleted");
        }
        return assistant;
    }

    public List<Assistants> getAllAssistants() {
        return assistantsRepository.findAllNotDeleted();
    }
}