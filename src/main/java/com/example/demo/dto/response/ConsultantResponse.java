package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
@Builder(setterPrefix = "with")
public class ConsultantResponse {

    private final String id;
    private final String name;
    private final String email;
    private final String role;
    private final Integer yearsOfExperience;
    private final Boolean availability;
    private final Boolean wantsNewProject;
    private final Boolean openToRelocation;
    private final Boolean openToRemote;
    private final List<String> preferredRegions;
    private final Set<HasSkillResponse> skills;
    private final Set<AssignedToResponse> projectAssignments;
}
