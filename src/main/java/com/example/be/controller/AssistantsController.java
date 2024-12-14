package com.example.be.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.Assistants;
import com.example.be.service.AssistantsService;
import com.example.be.dto.AssistantDTO;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/assistants")
public class AssistantsController {
    private final AssistantsService assistantsService;

    public AssistantsController(AssistantsService assistantsService) {
        this.assistantsService = assistantsService;
    }

    @GetMapping
    public ResponseEntity<List<AssistantDTO>> getAllAssistants() {
        return ResponseEntity.ok(assistantsService.getAllAssistantsDTO());
    }

    @GetMapping("/{assistantId}")
    public ResponseEntity<AssistantDTO> getAssistantById(@PathVariable Integer assistantId) {
        return ResponseEntity.ok(assistantsService.getAssistantDTOById(assistantId));
    }

    @PostMapping
    public ResponseEntity<AssistantDTO> createAssistant(@Valid @RequestBody Assistants assistant) {
        return ResponseEntity.ok(assistantsService.createAssistant(assistant));
    }

    @PutMapping("/{assistantId}")
    public ResponseEntity<AssistantDTO> updateAssistant(@PathVariable Integer assistantId, @Valid @RequestBody Assistants assistant) {
        return ResponseEntity.ok(assistantsService.updateAssistant(assistantId, assistant));
    }

    @DeleteMapping("/{assistantId}")
    public ResponseEntity<Void> deleteAssistant(@PathVariable Integer assistantId) {
        assistantsService.deleteAssistant(assistantId);
        return ResponseEntity.noContent().build();
    }
}