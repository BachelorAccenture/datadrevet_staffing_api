package com.example.demo.service;

import com.example.demo.model.Company;
import com.example.demo.repository.CompanyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompanyService {

    private final CompanyRepository companyRepository;

    public Company create(final Company company) {
        log.info("[CompanyService] - CREATE: name: {}", company.getName());

        if (companyRepository.existsByName(company.getName())) {
            throw new IllegalArgumentException("Company already exists with name: " + company.getName());
        }

        return companyRepository.save(company);
    }

    public Optional<Company> findById(final String id) {
        log.debug("[CompanyService] - FIND_BY_ID: id: {}", id);
        return companyRepository.findById(id);
    }

    public List<Company> findAll() {
        log.debug("[CompanyService] - FIND_ALL");
        return companyRepository.findAll();
    }

    public List<Company> findByField(final String field) {
        log.debug("[CompanyService] - FIND_BY_FIELD: field: {}", field);
        return companyRepository.findByField(field);
    }

    public List<Company> search(final String query) {
        log.debug("[CompanyService] - SEARCH: query: {}", query);
        return companyRepository.findByNameContainingIgnoreCase(query);
    }

    public Company update(final String id, final Company updatedCompany) {
        log.info("[CompanyService] - UPDATE: id: {}", id);
        final Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + id));

        existingCompany.setName(updatedCompany.getName());
        existingCompany.setField(updatedCompany.getField());

        return companyRepository.save(existingCompany);
    }

    public void delete(final String id) {
        log.info("[CompanyService] - DELETE: id: {}", id);
        companyRepository.deleteById(id);
    }
}