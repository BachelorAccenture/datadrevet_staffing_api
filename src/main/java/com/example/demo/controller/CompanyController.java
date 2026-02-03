package com.example.demo.controller;

import com.example.demo.dto.request.CreateCompanyRequest;
import com.example.demo.dto.response.CompanyResponse;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.mapper.CompanyMapper;
import com.example.demo.model.Company;
import com.example.demo.service.CompanyService;
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
@RequestMapping("/api/v1/companies")
@RequiredArgsConstructor
@Slf4j
public class CompanyController {

    private final CompanyService companyService;

    @PostMapping
    public ResponseEntity<CompanyResponse> create(@Valid @RequestBody final CreateCompanyRequest request) {
        log.info("[CompanyController] - CREATE_REQUEST: name: {}", request.name());
        final Company company = CompanyMapper.toEntity(request);
        final Company savedCompany = companyService.create(company);
        return ResponseEntity.status(HttpStatus.CREATED).body(CompanyMapper.toResponse(savedCompany));
    }

    @GetMapping
    public ResponseEntity<List<CompanyResponse>> getAll() {
        log.info("[CompanyController] - GET_ALL");
        final List<Company> companies = companyService.findAll();
        return ResponseEntity.ok(CompanyMapper.toResponseList(companies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getById(@PathVariable final String id) {
        log.info("[CompanyController] - GET_BY_ID: id: {}", id);
        return companyService.findById(id)
                .map(CompanyMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResourceNotFoundException("Company", id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<CompanyResponse>> search(@RequestParam final String query) {
        log.info("[CompanyController] - SEARCH: query: {}", query);
        final List<Company> companies = companyService.search(query);
        return ResponseEntity.ok(CompanyMapper.toResponseList(companies));
    }

    @GetMapping("/by-field")
    public ResponseEntity<List<CompanyResponse>> getByField(@RequestParam final String field) {
        log.info("[CompanyController] - GET_BY_FIELD: field: {}", field);
        final List<Company> companies = companyService.findByField(field);
        return ResponseEntity.ok(CompanyMapper.toResponseList(companies));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> update(
            @PathVariable final String id,
            @Valid @RequestBody final CreateCompanyRequest request) {
        log.info("[CompanyController] - UPDATE: id: {}", id);
        final Company updatedCompany = CompanyMapper.toEntity(request);
        final Company savedCompany = companyService.update(id, updatedCompany);
        return ResponseEntity.ok(CompanyMapper.toResponse(savedCompany));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable final String id) {
        log.info("[CompanyController] - DELETE: id: {}", id);
        companyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}