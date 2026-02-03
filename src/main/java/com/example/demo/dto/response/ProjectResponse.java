package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Builder(setterPrefix = "with")
public class ProjectResponse {

    private final String id;
    private final String name;
    private final List<String> requirements;
    private final LocalDateTime date;
    private final CompanyResponse company;
    private final Set<RequiresSkillResponse> requiredSkills;
    private final Set<RequiresTechnologyResponse> requiredTechnologies;
}
