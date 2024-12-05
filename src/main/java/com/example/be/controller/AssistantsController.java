package com.example.be.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.be.model.Assistants;
import com.example.be.service.AssistantsService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/assistants")
public class AssistantsController {

    @Autowired
    private AssistantsService assistantsService;

    @PostMapping
    public ResponseEntity<Assistants> createAssistant(@Valid @RequestBody Assistants assistant) {
        return ResponseEntity.ok(assistantsService.createAssistant(assistant));
    }

    @GetMapping
    public ResponseEntity<List<Assistants>> getAllAssistants() {
        return ResponseEntity.ok(assistantsService.getAllAssistants());
    }

    @GetMapping("/{assistantId}")
    public ResponseEntity<Assistants> getAssistantById(@PathVariable Integer assistantId) {
        return ResponseEntity.ok(assistantsService.getAssistantById(assistantId));
    }

    @PutMapping("/{assistantId}")
    public ResponseEntity<Assistants> updateAssistant(@PathVariable Integer assistantId, @Valid @RequestBody Assistants assistant) {
        return ResponseEntity.ok(assistantsService.updateAssistant(assistantId, assistant));
    }

    @DeleteMapping("/{assistantId}")
    public ResponseEntity<Void> deleteAssistant(@PathVariable Integer assistantId) {
        assistantsService.deleteAssistant(assistantId);
        return ResponseEntity.noContent().build();
    }
}