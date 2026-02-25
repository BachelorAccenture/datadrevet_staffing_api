package com.example.demo.dto.request;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Request DTO for searching consultants with multiple filter criteria.
 * All fields are optional - null values indicate no filtering on that criterion.
 */
public record SearchConsultantRequest(
        List<String> skillNames,
        List<String> roles,
        Boolean availability,
        Boolean wantsNewProject,
        Boolean openToRemote,
        List<String> previousCompanies,
        LocalDateTime startDate
) {}