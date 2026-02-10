package com.example.demo.mapper;

import com.example.demo.dto.request.CreateProjectRequest;
import com.example.demo.dto.response.ProjectResponse;
import com.example.demo.dto.response.RequiresSkillResponse;
import com.example.demo.dto.response.RequiresTechnologyResponse;
import com.example.demo.model.Project;
import com.example.demo.model.relationship.RequiresSkill;
import com.example.demo.model.relationship.RequiresTechnology;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class ProjectMapper {

    private ProjectMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static ProjectResponse toResponse(final Project project) {
        if (project == null) {
            return null;
        }
        return ProjectResponse.builder()
                .withId(project.getId())
                .withName(project.getName())
                .withRequirements(project.getRequirements())
                .withDate(project.getDate())
                .withCompany(CompanyMapper.toResponse(project.getCompany()))
                .withRequiredSkills(mapRequiredSkills(project.getRequiredSkills()))
                .withRequiredTechnologies(mapRequiredTechnologies(project.getRequiredTechnologies()))
                .build();
    }

    public static List<ProjectResponse> toResponseList(final List<Project> projects) {
        if (projects == null || projects.isEmpty()) {
            return Collections.emptyList();
        }
        return projects.stream()
                .map(ProjectMapper::toResponse)
                .toList();
    }

    public static Project toEntity(final CreateProjectRequest request) {
        if (request == null) {
            return null;
        }
        final Project project = new Project();
        project.setName(request.name());
        project.setRequirements(request.requirements() != null ? request.requirements() : Collections.emptyList());
        project.setDate(request.date());
        return project;
    }

    private static Set<RequiresSkillResponse> mapRequiredSkills(final Set<RequiresSkill> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptySet();
        }
        return skills.stream()
                .map(ProjectMapper::mapRequiresSkill)
                .collect(Collectors.toSet());
    }

    private static RequiresSkillResponse mapRequiresSkill(final RequiresSkill requiresSkill) {
        return RequiresSkillResponse.builder()
                .withSkillId(requiresSkill.getSkill() != null ? requiresSkill.getSkill().getId() : null)
                .withSkillName(requiresSkill.getSkill() != null ? requiresSkill.getSkill().getName() : null)
                .withMinLevel(requiresSkill.getMinLevel())
                .withIsMandatory(requiresSkill.getIsMandatory())
                .build();
    }

    private static Set<RequiresTechnologyResponse> mapRequiredTechnologies(final Set<RequiresTechnology> technologies) {
        if (technologies == null || technologies.isEmpty()) {
            return Collections.emptySet();
        }
        return technologies.stream()
                .map(ProjectMapper::mapRequiresTechnology)
                .collect(Collectors.toSet());
    }

    private static RequiresTechnologyResponse mapRequiresTechnology(final RequiresTechnology requiresTechnology) {
        return RequiresTechnologyResponse.builder()
                .withTechnologyId(requiresTechnology.getSkill() != null ? requiresTechnology.getSkill().getId() : null)
                .withTechnologyName(requiresTechnology.getSkill() != null ? requiresTechnology.getSkill().getName() : null)
                .withMinLevel(requiresTechnology.getMinLevel())
                .withIsMandatory(requiresTechnology.getIsMandatory())
                .build();
    }
}
