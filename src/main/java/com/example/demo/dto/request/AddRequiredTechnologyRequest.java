package com.example.demo.dto.request;

import com.example.demo.model.ProficiencyLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddRequiredTechnologyRequest(
        @NotBlank(message = "Technology ID is required")
        String technologyId,
        @NotNull(message = "Minimum level is required")
        ProficiencyLevel minLevel,
        Boolean isMandatory
) {}