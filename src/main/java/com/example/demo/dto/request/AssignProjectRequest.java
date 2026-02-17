// src/main/java/com/example/demo/dto/request/AssignProjectRequest.java
package com.example.demo.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record AssignProjectRequest(
        @NotBlank(message = "Project ID is required")
        String projectId,
        String role,
        @Min(value = 0, message = "Allocation percent must be between 0 and 100")
        @Max(value = 100, message = "Allocation percent must be between 0 and 100")
        Integer allocationPercent,
        Boolean isActive,
        Long startDate,
        Long endDate
) {}
