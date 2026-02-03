package com.example.demo.mapper;

import com.example.demo.dto.request.CreateCompanyRequest;
import com.example.demo.dto.response.CompanyResponse;
import com.example.demo.model.Company;

import java.util.Collections;
import java.util.List;

public final class CompanyMapper {

    private CompanyMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static CompanyResponse toResponse(final Company company) {
        if (company == null) {
            return null;
        }
        return CompanyResponse.builder()
                .withId(company.getId())
                .withName(company.getName())
                .withField(company.getField())
                .build();
    }

    public static List<CompanyResponse> toResponseList(final List<Company> companies) {
        if (companies == null || companies.isEmpty()) {
            return Collections.emptyList();
        }
        return companies.stream()
                .map(CompanyMapper::toResponse)
                .toList();
    }

    public static Company toEntity(final CreateCompanyRequest request) {
        if (request == null) {
            return null;
        }
        final Company company = new Company();
        company.setName(request.name());
        company.setField(request.field());
        return company;
    }
}