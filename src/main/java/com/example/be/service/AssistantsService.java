package com.example.be.service;

import com.example.be.model.Assistants;
import com.example.be.repository.AssistantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssistantsService {

    @Autowired
    private AssistantsRepository assistantsRepository;

    public Assistants createAssistant(Assistants assistant) {
        assistant.setCreatedAt(LocalDateTime.now());
        return assistantsRepository.save(assistant);
    }

    public List<Assistants> getAllAssistants() {
        return assistantsRepository.findAllNotDeleted();
    }

    public Assistants getAssistantById(Integer assistantId) {
        Assistants assistant = assistantsRepository.findByIdNotDeleted(assistantId);
        if (assistant == null) {
            throw new RuntimeException("Assistant not found or has been deleted");
        }
        return assistant;
    }

    public Assistants updateAssistant(Integer assistantId, Assistants assistantDetails) {
        Assistants assistant = getAssistantById(assistantId);

        assistant.setUser(assistantDetails.getUser());
        assistant.setAssistantStatus(assistantDetails.getAssistantStatus());
        assistant.setUpdatedAt(LocalDateTime.now());

        return assistantsRepository.save(assistant);
    }

    public void deleteAssistant(Integer assistantId) {
        Assistants assistant = getAssistantById(assistantId);
        assistant.markAsDeleted();
        assistantsRepository.save(assistant);
    }
}