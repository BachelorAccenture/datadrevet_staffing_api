package com.example.demo.service;

import com.example.demo.model.Company;
import com.example.demo.model.Project;
import com.example.demo.model.Skill;
import com.example.demo.model.relationship.RequiresSkill;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final CompanyRepository companyRepository;
    private final SkillRepository skillRepository;

    public Project create(final Project project) {
        log.info("[ProjectService] - CREATE: name: {}", project.getName());
        return projectRepository.save(project);
    }

    public Optional<Project> findById(final String id) {
        log.debug("[ProjectService] - FIND_BY_ID: id: {}", id);
        return projectRepository.findById(id);
    }

    public Optional<Project> findByName(final String name) {
        log.debug("[ProjectService] - FIND_BY_NAME: name: {}", name);
        return projectRepository.findByName(name);
    }

    public List<Project> findAll() {
        log.debug("[ProjectService] - FIND_ALL");
        return projectRepository.findAll();
    }

    public List<Project> findByCompanyId(final String companyId) {
        log.debug("[ProjectService] - FIND_BY_COMPANY_ID: companyId: {}", companyId);
        return projectRepository.findByCompanyId(companyId);
    }

    public List<Project> findByRequiredSkillNames(final List<String> skillNames) {
        log.debug("[ProjectService] - FIND_BY_REQUIRED_SKILL_NAMES: skills: {}", skillNames);
        return projectRepository.findByRequiredSkillNames(skillNames);
    }

    public Project update(final String id, final Project updatedProject) {
        log.info("[ProjectService] - UPDATE: id: {}", id);
        final Project existingProject = projectRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + id));

        existingProject.setName(updatedProject.getName());
        existingProject.setRequirements(updatedProject.getRequirements());
        existingProject.setDate(updatedProject.getDate());

        return projectRepository.save(existingProject);
    }

    public void delete(final String id) {
        log.info("[ProjectService] - DELETE: id: {}", id);
        projectRepository.deleteById(id);
    }

    public Project assignCompany(final String projectId, final String companyId) {
        log.info("[ProjectService] - ASSIGN_COMPANY: projectId: {}, companyId: {}", projectId, companyId);

        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));

        final Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found with id: " + companyId));

        project.setCompany(company);
        return projectRepository.save(project);
    }

    public Project addRequiredSkill(final String projectId, final String skillId,
                                    final Integer minYearsOfExperience, final Boolean isMandatory) {
        log.info("[ProjectService] - ADD_REQUIRED_SKILL: projectId: {}, skillId: {}, minYears: {}, mandatory: {}",
                projectId, skillId, minYearsOfExperience, isMandatory);

        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));

        final Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found with id: " + skillId));

        final RequiresSkill requiresSkill = new RequiresSkill();
        requiresSkill.setSkill(skill);
        requiresSkill.setMinYearsOfExperience(minYearsOfExperience);
        requiresSkill.setIsMandatory(isMandatory);

        project.getRequiredSkills().add(requiresSkill);
        return projectRepository.save(project);
    }

    public boolean existsByName(final String name) {
        return projectRepository.existsByName(name);
    }
}