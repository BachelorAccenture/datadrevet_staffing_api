package com.example.demo.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AddTechnologyRequest(
        @NotBlank(message = "Technology ID is required")
        String technologyId,
        @Min(value = 0, message = "Years of experience must be non-negative")
        Integer skillYearsOfExperience
) {}
