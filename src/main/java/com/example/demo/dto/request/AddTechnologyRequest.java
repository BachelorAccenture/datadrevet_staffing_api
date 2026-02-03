package com.example.demo.dto.request;

import com.example.demo.model.ProficiencyLevel;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddTechnologyRequest(
        @NotBlank(message = "Technology ID is required")
        String technologyId,
        @NotNull(message = "Proficiency level is required")
        ProficiencyLevel level,
        @Min(value = 0, message = "Years of experience must be non-negative")
        Integer yearsExperience
) {}
