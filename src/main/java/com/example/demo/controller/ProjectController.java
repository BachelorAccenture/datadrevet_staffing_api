package com.example.demo.controller;

import com.example.demo.dto.request.AddRequiredSkillRequest;
import com.example.demo.dto.request.CreateProjectRequest;
import com.example.demo.dto.request.UpdateProjectRequest;
import com.example.demo.dto.response.ProjectResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.ProjectMapper;
import com.example.demo.model.Project;
import com.example.demo.service.ProjectService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
@Slf4j
public class ProjectController {

    private final ProjectService projectService;

    @PostMapping
    public ResponseEntity<ProjectResponse> create(@Valid @RequestBody final CreateProjectRequest request) {
        log.info("[ProjectController] - CREATE_REQUEST: name: {}", request.name());
        final Project project = ProjectMapper.toEntity(request);
        final Project savedProject = projectService.create(project);

        if (request.companyId() != null) {
            final Project projectWithCompany = projectService.assignCompany(savedProject.getId(), request.companyId());
            return ResponseEntity.status(HttpStatus.CREATED).body(ProjectMapper.toResponse(projectWithCompany));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ProjectMapper.toResponse(savedProject));
    }

    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAll() {
        log.info("[ProjectController] - GET_ALL");
        final List<Project> projects = projectService.findAll();
        return ResponseEntity.ok(ProjectMapper.toResponseList(projects));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getById(@PathVariable final String id) {
        log.info("[ProjectController] - GET_BY_ID: id: {}", id);
        return projectService.findById(id)
                .map(ProjectMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    @GetMapping("/by-name")
    public ResponseEntity<ProjectResponse> getByName(@RequestParam final String name) {
        log.info("[ProjectController] - GET_BY_NAME: name: {}", name);
        return projectService.findByName(name)
                .map(ProjectMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Project", name));
    }

    @GetMapping("/by-company/{companyId}")
    public ResponseEntity<List<ProjectResponse>> getByCompanyId(@PathVariable final String companyId) {
        log.info("[ProjectController] - GET_BY_COMPANY_ID: companyId: {}", companyId);
        final List<Project> projects = projectService.findByCompanyId(companyId);
        return ResponseEntity.ok(ProjectMapper.toResponseList(projects));
    }

    @GetMapping("/by-required-skills")
    public ResponseEntity<List<ProjectResponse>> getByRequiredSkills(@RequestParam final List<String> skillNames) {
        log.info("[ProjectController] - GET_BY_REQUIRED_SKILLS: skills: {}", skillNames);
        final List<Project> projects = projectService.findByRequiredSkillNames(skillNames);
        return ResponseEntity.ok(ProjectMapper.toResponseList(projects));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> update(
            @PathVariable final String id,
            @Valid @RequestBody final UpdateProjectRequest request) {
        log.info("[ProjectController] - UPDATE: id: {}", id);
        final Project updatedProject = ProjectMapper.toEntity(request);
        final Project savedProject = projectService.update(id, updatedProject);
        return ResponseEntity.ok(ProjectMapper.toResponse(savedProject));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        log.info("[ProjectController] - DELETE: id: {}", id);
        projectService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/company/{companyId}")
    public ResponseEntity<ProjectResponse> assignCompany(
            @PathVariable final String id,
            @PathVariable final String companyId) {
        log.info("[ProjectController] - ASSIGN_COMPANY: projectId: {}, companyId: {}", id, companyId);
        final Project project = projectService.assignCompany(id, companyId);
        return ResponseEntity.ok(ProjectMapper.toResponse(project));
    }

    @PostMapping("/{id}/required-skills")
    public ResponseEntity<ProjectResponse> addRequiredSkill(
            @PathVariable final String id,
            @Valid @RequestBody final AddRequiredSkillRequest request) {
        log.info("[ProjectController] - ADD_REQUIRED_SKILL: projectId: {}, skillId: {}", id, request.skillId());
        final Boolean isMandatory = request.isMandatory() != null ? request.isMandatory() : false;
        final Project project = projectService.addRequiredSkill(id, request.skillId(), request.minYearsOfExperience(), isMandatory);
        return ResponseEntity.ok(ProjectMapper.toResponse(project));
    }
}