package com.example.demo.dto.request;

import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateSkillRequest(
        @NotBlank(message = "Name is required")
        String name,
        List<String> synonyms
) {}