package com.example.demo.dto.request;

import com.example.demo.model.ProficiencyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddRequiredSkillRequest(
        @NotBlank(message = "Skill ID is required")
        String skillId,
        @NotNull(message = "Minimum level is required")
        ProficiencyLevel minLevel,
        Boolean isMandatory
) {}
