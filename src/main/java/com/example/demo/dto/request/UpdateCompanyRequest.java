package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

public record UpdateCompanyRequest(
        @NotBlank(message = "Name is required")
        String name,
        String field
) {}
