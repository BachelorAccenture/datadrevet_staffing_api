package com.example.demo.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;


public record CreateConsultantRequest(
        @NotBlank(message = "Name is required")
        String name,
        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        String email,
        @Min(value = 0, message = "Years of experience must be non-negative")
        Integer yearsOfExperience,
        Boolean availability,
        Boolean wantsNewProject,
        Boolean openToRemote
) {}
