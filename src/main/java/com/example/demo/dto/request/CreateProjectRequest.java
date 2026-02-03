package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

public record CreateProjectRequest(
        @NotBlank(message = "Name is required")
        String name,
        List<String> requirements,
        LocalDateTime date,
        String companyId
) {}
