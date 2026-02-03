package com.example.demo.controller;

import com.example.demo.dto.request.CreateTechnologyRequest;
import com.example.demo.dto.response.TechnologyResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.TechnologyMapper;
import com.example.demo.model.Technology;
import com.example.demo.service.TechnologyService;
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
@RequestMapping("/api/v1/technologies")
@RequiredArgsConstructor
@Slf4j
public class TechnologyController {

    private final TechnologyService technologyService;

    @PostMapping
    public ResponseEntity<TechnologyResponse> create(@Valid @RequestBody final CreateTechnologyRequest request) {
        log.info("[TechnologyController] - CREATE_REQUEST: name: {}", request.name());
        final Technology technology = TechnologyMapper.toEntity(request);
        final Technology savedTechnology = technologyService.create(technology);
        return ResponseEntity.status(HttpStatus.CREATED).body(TechnologyMapper.toResponse(savedTechnology));
    }

    @GetMapping
    public ResponseEntity<List<TechnologyResponse>> getAll() {
        log.info("[TechnologyController] - GET_ALL");
        final List<Technology> technologies = technologyService.findAll();
        return ResponseEntity.ok(TechnologyMapper.toResponseList(technologies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TechnologyResponse> getById(@PathVariable final String id) {
        log.info("[TechnologyController] - GET_BY_ID: id: {}", id);
        return technologyService.findById(id)
                .map(TechnologyMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Technology", id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TechnologyResponse>> search(@RequestParam final String query) {
        log.info("[TechnologyController] - SEARCH: query: {}", query);
        final List<Technology> technologies = technologyService.search(query);
        return ResponseEntity.ok(TechnologyMapper.toResponseList(technologies));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TechnologyResponse> update(
            @PathVariable final String id,
            @Valid @RequestBody final CreateTechnologyRequest request) {
        log.info("[TechnologyController] - UPDATE: id: {}", id);
        final Technology updatedTechnology = TechnologyMapper.toEntity(request);
        final Technology savedTechnology = technologyService.update(id, updatedTechnology);
        return ResponseEntity.ok(TechnologyMapper.toResponse(savedTechnology));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        log.info("[TechnologyController] - DELETE: id: {}", id);
        technologyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}