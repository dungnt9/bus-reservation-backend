package com.example.be.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<Page<AssistantDTO>> getAllAssistants(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(assistantsService.getAllAssistantsDTO(pageable));
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
}