package com.example.be.service;

import com.example.be.model.Assistants;
import com.example.be.repository.AssistantsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssistantsService {

    @Autowired
    private AssistantsRepository assistantsRepository;

    public Assistants createAssistant(Assistants assistant) {
        return assistantsRepository.save(assistant);
    }

    public List<Assistants> getAllAssistants() {
        return assistantsRepository.findAll();
    }

    public Assistants getAssistantById(Integer assistantId) {
        return assistantsRepository.findById(assistantId)
                .orElseThrow(() -> new RuntimeException("Assistant not found"));
    }

    public Assistants updateAssistant(Integer assistantId, Assistants assistantDetails) {
        Assistants assistant = getAssistantById(assistantId);
        
        assistant.setUser(assistantDetails.getUser());
        assistant.setAssistantStatus(assistantDetails.getAssistantStatus());

        return assistantsRepository.save(assistant);
    }

    public void deleteAssistant(Integer assistantId) {
        Assistants assistant = getAssistantById(assistantId);
        assistantsRepository.delete(assistant);
    }
}