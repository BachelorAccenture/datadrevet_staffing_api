package com.example.demo.dto.request;

import com.example.demo.model.ProficiencyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddSkillRequest(
        @NotBlank(message = "Skill ID is required")
        String skillId,
        @NotNull(message = "Proficiency level is required")
        ProficiencyLevel level
) {}
