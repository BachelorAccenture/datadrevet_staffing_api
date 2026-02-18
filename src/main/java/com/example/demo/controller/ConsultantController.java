package com.example.demo.controller;

import com.example.demo.dto.request.AddSkillRequest;
import com.example.demo.dto.request.AssignProjectRequest;
import com.example.demo.dto.request.CreateConsultantRequest;
import com.example.demo.dto.response.ConsultantResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ConsultantMapper;
import com.example.demo.model.Consultant;
import com.example.demo.service.ConsultantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/consultants")
@RequiredArgsConstructor
@Slf4j
public class ConsultantController {

    private final ConsultantService consultantService;

    @PostMapping
    public ResponseEntity<ConsultantResponse> create(@Valid @RequestBody final CreateConsultantRequest request) {
        log.info("[ConsultantController] - CREATE_REQUEST: email: {}", request.email());
        final Consultant consultant = ConsultantMapper.toEntity(request);
        final Consultant savedConsultant = consultantService.create(consultant);
        return ResponseEntity.status(HttpStatus.CREATED).body(ConsultantMapper.toResponse(savedConsultant));
    }

    @GetMapping
    public ResponseEntity<List<ConsultantResponse>> getAll() {
        log.info("[ConsultantController] - GET_ALL");
        final List<Consultant> consultants = consultantService.findAll();
        return ResponseEntity.ok(ConsultantMapper.toResponseList(consultants));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultantResponse> getById(@PathVariable final String id) {
        log.info("[ConsultantController] - GET_BY_ID: id: {}", id);
        return consultantService.findById(id)
                .map(ConsultantMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Consultant", id));
    }

    @GetMapping("/by-email")
    public ResponseEntity<ConsultantResponse> getByEmail(@RequestParam final String email) {
        log.info("[ConsultantController] - GET_BY_EMAIL: email: {}", email);
        return consultantService.findByEmail(email)
                .map(ConsultantMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Consultant", email));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ConsultantResponse>> search(
            @RequestParam(required = false) final List<String> skillNames,
            @RequestParam(required = false) final String role,
            @RequestParam(required = false) final Integer minYearsOfExperience,
            @RequestParam(required = false) final Boolean availability,
            @RequestParam(required = false) final Boolean wantsNewProject,
            @RequestParam(required = false) final Boolean openToRemote,
            @RequestParam(required = false) final Boolean openToRelocation,
            @RequestParam(required = false) final List<String> previousCompanies,
            @RequestParam(required = false) final LocalDateTime startDate,
            @RequestParam(required = false) final LocalDateTime endDate) {
        log.info("[ConsultantController] - SEARCH: skills: {}, role: {}, minYears: {}, availability: {}, " +
                        "wantsNewProject: {}, openToRemote: {}, openToRelocation: {}, previousCompanies: {}, " +
                        "startDate: {}, endDate: {}",
                skillNames, role, minYearsOfExperience, availability, wantsNewProject,
                openToRemote, openToRelocation, previousCompanies, startDate, endDate);

        final List<Consultant> consultants = consultantService.searchConsultants(
                skillNames, role, minYearsOfExperience, availability, wantsNewProject,
                openToRemote, previousCompanies, startDate, endDate);
        return ResponseEntity.ok(ConsultantMapper.toResponseList(consultants));
    }

    @GetMapping("/available")
    public ResponseEntity<List<ConsultantResponse>> getAvailable() {
        log.info("[ConsultantController] - GET_AVAILABLE");
        final List<Consultant> consultants = consultantService.findAvailable();
        return ResponseEntity.ok(ConsultantMapper.toResponseList(consultants));
    }

    @GetMapping("/wanting-new-project")
    public ResponseEntity<List<ConsultantResponse>> getWantingNewProject() {
        log.info("[ConsultantController] - GET_WANTING_NEW_PROJECT");
        final List<Consultant> consultants = consultantService.findWantingNewProject();
        return ResponseEntity.ok(ConsultantMapper.toResponseList(consultants));
    }

    @GetMapping("/by-skills")
    public ResponseEntity<List<ConsultantResponse>> getBySkills(@RequestParam final List<String> skillNames) {
        log.info("[ConsultantController] - GET_BY_SKILLS: skills: {}", skillNames);
        final List<Consultant> consultants = consultantService.findBySkillNames(skillNames);
        return ResponseEntity.ok(ConsultantMapper.toResponseList(consultants));
    }

    @GetMapping("/available-with-experience")
    public ResponseEntity<List<ConsultantResponse>> getAvailableWithMinExperience(
            @RequestParam final Integer minYears) {
        log.info("[ConsultantController] - GET_AVAILABLE_WITH_MIN_EXPERIENCE: minYears: {}", minYears);
        final List<Consultant> consultants = consultantService.findAvailableWithMinExperience(minYears);
        return ResponseEntity.ok(ConsultantMapper.toResponseList(consultants));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultantResponse> update(
            @PathVariable final String id,
            @Valid @RequestBody final CreateConsultantRequest request) {
        log.info("[ConsultantController] - UPDATE: id: {}", id);
        final Consultant updatedConsultant = ConsultantMapper.toEntity(request);
        final Consultant savedConsultant = consultantService.update(id, updatedConsultant);
        return ResponseEntity.ok(ConsultantMapper.toResponse(savedConsultant));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        log.info("[ConsultantController] - DELETE: id: {}", id);
        consultantService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/skills")
    public ResponseEntity<ConsultantResponse> addSkill(
            @PathVariable final String id,
            @Valid @RequestBody final AddSkillRequest request) {
        log.info("[ConsultantController] - ADD_SKILL: consultantId: {}, skillId: {}, skillYearsOfExperience: {}",
                id, request.skillId(), request.skillYearsOfExperience());
        final Consultant consultant = consultantService.addSkill(id, request.skillId(), request.skillYearsOfExperience());
        return ResponseEntity.ok(ConsultantMapper.toResponse(consultant));
    }

    @PostMapping("/{id}/projects")
    public ResponseEntity<ConsultantResponse> assignToProject(
            @PathVariable final String id,
            @Valid @RequestBody final AssignProjectRequest request) {
        log.info("[ConsultantController] - ASSIGN_TO_PROJECT: consultantId: {}, projectId: {}", id, request.projectId());
        final Consultant consultant = consultantService.assignToProject(
                id, request.projectId(), request.role(), request.allocationPercent(),
                request.isActive(), request.startDate(), request.endDate());
        return ResponseEntity.ok(ConsultantMapper.toResponse(consultant));
    }

    @PatchMapping("/{id}/projects/{projectId}/deactivate")
    public ResponseEntity<ConsultantResponse> deactivateProjectAssignment(
            @PathVariable final String id,
            @PathVariable final String projectId) {
        log.info("[ConsultantController] - DEACTIVATE_PROJECT: consultantId: {}, projectId: {}", id, projectId);
        final Consultant consultant = consultantService.deactivateProjectAssignment(id, projectId);
        return ResponseEntity.ok(ConsultantMapper.toResponse(consultant));
    }

    @DeleteMapping("/{id}/projects/{projectId}")
    public ResponseEntity<ConsultantResponse> removeProjectAssignment(
            @PathVariable final String id,
            @PathVariable final String projectId) {
        log.info("[ConsultantController] - REMOVE_PROJECT: consultantId: {}, projectId: {}", id, projectId);
        final Consultant consultant = consultantService.removeProjectAssignment(id, projectId);
        return ResponseEntity.ok(ConsultantMapper.toResponse(consultant));
    }
}