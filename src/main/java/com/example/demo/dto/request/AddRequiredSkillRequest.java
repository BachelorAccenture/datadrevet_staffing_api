package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddRequiredSkillRequest(
        @NotBlank(message = "Skill ID is required")
        String skillId,
        @NotNull(message = "Minimum years of experience is required")
        @Min(value = 0, message = "Minimum years of experience must be non-negative")
        Integer minYearsOfExperience,
        Boolean isMandatory
) {}
