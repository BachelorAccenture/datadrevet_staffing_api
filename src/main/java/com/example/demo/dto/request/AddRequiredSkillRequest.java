package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddRequiredSkillRequest(
        @NotBlank(message = "Skill ID is required")
        String skillId,
        @NotNull(message = "Minimum years of experience is required")
        Integer minYearsOfExperience,
        Boolean isMandatory
) {}
