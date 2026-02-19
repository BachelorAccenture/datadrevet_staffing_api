package com.example.demo.controller;

import com.example.demo.dto.request.CreateSkillRequest;
import com.example.demo.dto.request.UpdateSkillRequest;
import com.example.demo.dto.response.SkillResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.SkillMapper;
import com.example.demo.model.Skill;
import com.example.demo.service.SkillService;
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
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Slf4j
public class SkillController {

    private final SkillService skillService;

    @PostMapping
    public ResponseEntity<SkillResponse> create(@Valid @RequestBody final CreateSkillRequest request) {
        log.info("[SkillController] - CREATE_REQUEST: name: {}", request.name());
        final Skill skill = SkillMapper.toEntity(request);
        final Skill savedSkill = skillService.create(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(SkillMapper.toResponse(savedSkill));
    }

    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAll() {
        log.info("[SkillController] - GET_ALL");
        final List<Skill> skills = skillService.findAll();
        return ResponseEntity.ok(SkillMapper.toResponseList(skills));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SkillResponse> getById(@PathVariable final String id) {
        log.info("[SkillController] - GET_BY_ID: id: {}", id);
        return skillService.findById(id)
                .map(SkillMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Skill", id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<SkillResponse>> search(@RequestParam final String query) {
        log.info("[SkillController] - SEARCH: query: {}", query);
        final List<Skill> skills = skillService.search(query);
        return ResponseEntity.ok(SkillMapper.toResponseList(skills));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> update(
            @PathVariable final String id,
            @Valid @RequestBody final UpdateSkillRequest request) {
        log.info("[SkillController] - UPDATE: id: {}", id);
        final Skill updatedSkill = SkillMapper.toEntity(request);
        final Skill savedSkill = skillService.update(id, updatedSkill);
        return ResponseEntity.ok(SkillMapper.toResponse(savedSkill));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        log.info("[SkillController] - DELETE: id: {}", id);
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
