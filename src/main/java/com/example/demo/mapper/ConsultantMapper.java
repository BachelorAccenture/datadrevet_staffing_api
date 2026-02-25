package com.example.demo.mapper;

import com.example.demo.dto.request.CreateConsultantRequest;
import com.example.demo.dto.request.UpdateConsultantRequest;
import com.example.demo.dto.response.AssignedToResponse;
import com.example.demo.dto.response.ConsultantResponse;
import com.example.demo.dto.response.HasSkillResponse;
import com.example.demo.model.Consultant;
import com.example.demo.model.relationship.AssignedTo;
import com.example.demo.model.relationship.HasSkill;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ConsultantMapper {

    private ConsultantMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ConsultantResponse toResponse(final Consultant consultant) {
        if (consultant == null) {
            return null;
        }
        return ConsultantResponse.builder()
                .withId(consultant.getId())
                .withName(consultant.getName())
                .withEmail(consultant.getEmail())
                .withYearsOfExperience(consultant.getYearsOfExperience())
                .withAvailability(consultant.getAvailability())
                .withWantsNewProject(consultant.getWantsNewProject())
                .withOpenToRemote(consultant.getOpenToRemote())
                .withSkills(mapSkills(consultant.getSkills()))
                .withProjectAssignments(mapProjectAssignments(consultant.getProjectAssignments()))
                .build();
    }

    public static List<ConsultantResponse> toResponseList(final List<Consultant> consultants) {
        if (consultants == null || consultants.isEmpty()) {
            return Collections.emptyList();
        }
        return consultants.stream()
                .map(ConsultantMapper::toResponse)
                .toList();
    }

    public static Consultant toEntity(final CreateConsultantRequest request) {
        if (request == null) {
            return null;
        }
        final Consultant consultant = new Consultant();
        consultant.setName(request.name());
        consultant.setEmail(request.email());
        consultant.setYearsOfExperience(request.yearsOfExperience());
        consultant.setAvailability(request.availability() != null ? request.availability() : false);
        consultant.setWantsNewProject(request.wantsNewProject() != null ? request.wantsNewProject() : false);
        consultant.setOpenToRemote(request.openToRemote() != null ? request.openToRemote() : false);
        return consultant;
    }
    public static Consultant toEntity(final UpdateConsultantRequest request) {
        if (request == null) {
            return null;
        }
        final Consultant consultant = new Consultant();
        consultant.setName(request.name());
        consultant.setEmail(request.email());
        consultant.setYearsOfExperience(request.yearsOfExperience());
        consultant.setWantsNewProject(request.wantsNewProject() != null ? request.wantsNewProject() : false);
        consultant.setOpenToRemote(request.openToRemote() != null ? request.openToRemote() : false);
        return consultant;
    }

    private static Set<HasSkillResponse> mapSkills(final Set<HasSkill> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptySet();
        }
        return skills.stream()
                .map(ConsultantMapper::mapHasSkill)
                .collect(Collectors.toSet());
    }

    private static HasSkillResponse mapHasSkill(final HasSkill hasSkill) {
        return HasSkillResponse.builder()
                .withSkillId(hasSkill.getSkill() != null ? hasSkill.getSkill().getId() : null)
                .withSkillName(hasSkill.getSkill() != null ? hasSkill.getSkill().getName() : null)
                .withSkillYearsOfExperience(hasSkill.getSkillYearsOfExperience())
                .build();
    }

    private static Set<AssignedToResponse> mapProjectAssignments(final Set<AssignedTo> assignments) {
        if (assignments == null || assignments.isEmpty()) {
            return Collections.emptySet();
        }
        return assignments.stream()
                .map(ConsultantMapper::mapAssignedTo)
                .collect(Collectors.toSet());
    }

    private static AssignedToResponse mapAssignedTo(final AssignedTo assignedTo) {
        return AssignedToResponse.builder()
                .withProjectId(assignedTo.getProject() != null ? assignedTo.getProject().getId() : null)
                .withProjectName(assignedTo.getProject() != null ? assignedTo.getProject().getName() : null)
                .withRole(assignedTo.getRole())
                .withAllocationPercent(assignedTo.getAllocationPercent())
                .withIsActive(assignedTo.getIsActive())
                .withStartDate(assignedTo.getStartDate())
                .withEndDate(assignedTo.getEndDate())
                .build();
    }
}