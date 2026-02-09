package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddSkillRequest(
        @NotBlank(message = "Skill ID is required")
        String skillId,
        @Min(value = 0, message = "Years of experience must be non-negative")
        Integer skillYearsOfExperience
) {}
