package com.example.demo.service;

import com.example.demo.model.Consultant;
import com.example.demo.model.Project;
import com.example.demo.model.Skill;
import com.example.demo.model.relationship.AssignedTo;
import com.example.demo.model.relationship.HasSkill;
import com.example.demo.repository.ConsultantRepository;
import com.example.demo.repository.ProjectRepository;
import com.example.demo.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultantService {

    private final ConsultantRepository consultantRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;

    public Consultant create(final Consultant consultant) {
        log.info("[ConsultantService] - CREATE: email: {}", consultant.getEmail());
        return consultantRepository.save(consultant);
    }

    public Optional<Consultant> findById(final String id) {
        log.debug("[ConsultantService] - FIND_BY_ID: id: {}", id);
        return consultantRepository.findById(id);
    }

    public Optional<Consultant> findByEmail(final String email) {
        log.debug("[ConsultantService] - FIND_BY_EMAIL: email: {}", email);
        return consultantRepository.findByEmail(email);
    }

    public List<Consultant> findAll() {
        log.debug("[ConsultantService] - FIND_ALL");
        return consultantRepository.findAll();
    }

    public List<Consultant> findAvailable() {
        log.debug("[ConsultantService] - FIND_AVAILABLE");
        return consultantRepository.findByAvailabilityTrue();
    }

    public List<Consultant> findWantingNewProject() {
        log.debug("[ConsultantService] - FIND_WANTING_NEW_PROJECT");
        return consultantRepository.findByWantsNewProjectTrue();
    }

    public List<Consultant> findBySkillNames(final List<String> skillNames) {
        log.debug("[ConsultantService] - FIND_BY_SKILL_NAMES: skills: {}", skillNames);
        return consultantRepository.findBySkillNames(skillNames);
    }

    public List<Consultant> findAvailableWithMinExperience(final Integer minYears) {
        log.debug("[ConsultantService] - FIND_AVAILABLE_WITH_MIN_EXPERIENCE: minYears: {}", minYears);
        return consultantRepository.findAvailableWithMinExperience(minYears);
    }

    public List<Consultant> searchConsultants(final List<String> skillNames,
                                              final String role,
                                              final Integer minYearsOfExperience,
                                              final Boolean availability,
                                              final Boolean wantsNewProject,
                                              final Boolean openToRemote,
                                              final List<String> previousCompanies,
                                              final Long startDate,
                                              final Long endDate) {
        log.info("[ConsultantService] - SEARCH: skills: {}, role: {}, minYears: {}, availability: {}, " +
                        "wantsNewProject: {}, openToRemote: {}, previousCompanies: {}, " +
                        "startDate: {}, endDate: {}",
                skillNames, role, minYearsOfExperience, availability, wantsNewProject,
                openToRemote, previousCompanies, startDate, endDate);

        final List<String> safeSkillNames = skillNames != null ? skillNames : Collections.emptyList();
        final List<String> safePreviousCompanies = previousCompanies != null ? previousCompanies : Collections.emptyList();

        return consultantRepository.searchConsultants(
                safeSkillNames,
                role,
                minYearsOfExperience,
                availability,
                wantsNewProject,
                openToRemote,
                safePreviousCompanies,
                startDate,
                endDate
        );
    }

    public Consultant update(final String id, final Consultant updatedConsultant) {
        log.info("[ConsultantService] - UPDATE: id: {}", id);
        final Consultant existingConsultant = consultantRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Consultant not found with id: " + id));

        existingConsultant.setName(updatedConsultant.getName());
        existingConsultant.setEmail(updatedConsultant.getEmail());
        existingConsultant.setYearsOfExperience(updatedConsultant.getYearsOfExperience());
        existingConsultant.setAvailability(updatedConsultant.getAvailability());
        existingConsultant.setWantsNewProject(updatedConsultant.getWantsNewProject());
        existingConsultant.setOpenToRemote(updatedConsultant.getOpenToRemote());

        return consultantRepository.save(existingConsultant);
    }

    public void delete(final String id) {
        log.info("[ConsultantService] - DELETE: id: {}", id);
        consultantRepository.deleteById(id);
    }

    public Consultant addSkill(final String consultantId, final String skillId, final Integer skillYearsOfExperience) {
        log.info("[ConsultantService] - ADD_SKILL: consultantId: {}, skillId: {}, skillYearsOfExperience: {}",
                consultantId, skillId, skillYearsOfExperience);

        final Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new IllegalArgumentException("Consultant not found with id: " + consultantId));

        final Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found with id: " + skillId));

        final HasSkill hasSkill = new HasSkill();
        hasSkill.setSkill(skill);
        hasSkill.setSkillYearsOfExperience(skillYearsOfExperience);

        consultant.getSkills().add(hasSkill);
        return consultantRepository.save(consultant);
    }

    public Consultant assignToProject(final String consultantId, final String projectId,
                                      final String role, final Integer allocationPercent,
                                      final Boolean isActive, final Long startDate, final Long endDate) {
        log.info("[ConsultantService] - ASSIGN_TO_PROJECT: consultantId: {}, projectId: {}", consultantId, projectId);

        final Consultant consultant = consultantRepository.findById(consultantId)
                .orElseThrow(() -> new IllegalArgumentException("Consultant not found with id: " + consultantId));

        final Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with id: " + projectId));

        final AssignedTo assignedTo = new AssignedTo();
        assignedTo.setProject(project);
        assignedTo.setRole(role);
        assignedTo.setAllocationPercent(allocationPercent != null ? allocationPercent : 100);
        assignedTo.setIsActive(isActive != null ? isActive : true);
        if (startDate != null) assignedTo.setStartDate(new Date(startDate));
        if (endDate != null) assignedTo.setEndDate(new Date(endDate));

        consultant.getProjectAssignments().add(assignedTo);
        return consultantRepository.save(consultant);
    }

    public boolean existsByEmail(final String email) {
        return consultantRepository.existsByEmail(email);
    }
}