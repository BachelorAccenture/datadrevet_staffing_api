package com.example.demo.config;

import com.example.demo.model.*;
import com.example.demo.model.relationship.AssignedTo;
import com.example.demo.model.relationship.HasSkill;
import com.example.demo.model.relationship.RequiresSkill;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.*;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataLoader {

    private final CompanyRepository companyRepository;
    private final ConsultantRepository consultantRepository;
    private final SkillRepository skillRepository;
    private final ProjectRepository projectRepository;

    /**
     * Strictly parses an integer from a string.
     * Throws IllegalArgumentException with a clear error message if parsing fails.
     * This ensures that CSV files must be correct - no invalid data is accepted.
     */
    private static Integer strictParseInt(String value, String fieldName, String context) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    String.format("[DataLoader] - FEIL: Tomt felt '%s' i %s. CSV-filen må inneholde en gyldig tallverdi.",
                            fieldName, context)
            );
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    String.format("[DataLoader] - FEIL: Ugyldig tallverdi '%s' for felt '%s' i %s. Forventet et heltall, men fikk tekst. Vennligst korriger CSV-filen.",
                            value, fieldName, context)
            );
        }
    }

    @Bean
    @Profile("dev")
    public CommandLineRunner loadData() {
        return args -> {
            log.info("[DataLoader] - Starting data initialization from CSV files...");

            try {
                // Clear existing data
                log.info("[DataLoader] - Clearing existing data...");
                projectRepository.deleteAll();
                consultantRepository.deleteAll();
                companyRepository.deleteAll();
                skillRepository.deleteAll();
                log.info("[DataLoader] - Existing data cleared successfully.");

                // Load skills from CSV
                Map<String, Skill> skillMap = loadSkills();
                log.info("[DataLoader] - Loaded {} skills", skillMap.size());

                // Load companies from CSV
                Map<String, Company> companyMap = loadCompanies();
                log.info("[DataLoader] - Loaded {} companies", companyMap.size());

                // Load projects from CSV (before consultants to create project references)
                Map<String, Project> projectMap = loadProjects(companyMap, skillMap);
                log.info("[DataLoader] - Loaded {} projects", projectMap.size());

                // Load consultants from CSV with project assignments
                loadConsultants(skillMap, projectMap);
                log.info("[DataLoader] - Loaded {} consultants", consultantRepository.count());

                log.info("[DataLoader] - Data initialization completed successfully!");
            } catch (IOException e) {
                log.error("[DataLoader] - Failed to load data from CSV files: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to initialize data", e);
            }
        };
    }

    private Map<String, Skill> loadSkills() throws IOException {
        Map<String, Skill> skillMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/skills.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.readLine(); // Skip header
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    List<String> synonyms = parts[1].isEmpty() ? new ArrayList<>() :
                            Arrays.asList(parts[1].split(";"));

                    Skill skill = new Skill();
                    skill.setName(name);
                    skill.setSynonyms(synonyms);
                    skill = skillRepository.save(skill);
                    skillMap.put(name, skill);
                }
            }
        }

        return skillMap;
    }

    private Map<String, Company> loadCompanies() throws IOException {
        Map<String, Company> companyMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/companies.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.readLine(); // Skip header
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    String field = parts[1].trim();

                    Company company = new Company();
                    company.setName(name);
                    company.setField(field);
                    company = companyRepository.save(company);
                    companyMap.put(name, company);
                }
            }
        }

        return companyMap;
    }

    private void loadConsultants(Map<String, Skill> skillMap, Map<String, Project> projectMap) throws IOException {
        ClassPathResource resource = new ClassPathResource("data/consultants.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.readLine(); // Skip header
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 14) {
                    Consultant consultant = getConsultant(parts);

                    // Parse and add skills (column 9)
                    addSkillsToConsultant(consultant, parts[9], skillMap);

                    // Parse previous projects (column 10) and add as inactive assignments
                    addProjectAssignments(consultant, parts[10], projectMap, parts[12], false);

                    // Parse current project (column 11) and add as active assignment
                    addProjectAssignments(consultant, parts[11], projectMap, parts[13], true);

                    consultantRepository.save(consultant);
                }
            }
        }
    }

    private void addProjectAssignments(Consultant consultant, String projectsStr, Map<String, Project> projectMap,
                                       String rolesStr, boolean isActive) {
        String cleanProjects = projectsStr.trim().replace("\"", "");
        String cleanRoles = rolesStr.trim().replace("\"", "");

        if (!cleanProjects.isEmpty()) {
            String[] projectNames = cleanProjects.split(";");
            String[] roles = cleanRoles.isEmpty() ? new String[0] : cleanRoles.split(";");

            for (int i = 0; i < projectNames.length; i++) {
                String projectName = projectNames[i].trim();
                Project project = projectMap.get(projectName);

                if (project != null) {
                    AssignedTo assignment = new AssignedTo();
                    assignment.setProject(project);
                    assignment.setIsActive(isActive);
                    assignment.setAllocationPercent(isActive ? 100 : null);

                    // Get role from the roles array
                    if (i < roles.length) {
                        assignment.setRole(roles[i].trim());
                    } else if (roles.length > 0) {
                        assignment.setRole(roles[roles.length - 1].trim());
                    }

                    consultant.getProjectAssignments().add(assignment);
                }
            }
        }
    }

    private void addSkillsToConsultant(Consultant consultant, String skillsStr, Map<String, Skill> skillMap) {
        String clean = skillsStr.trim().replace("\"", "");
        if (!clean.isEmpty()) {
            for (String skillEntry : clean.split(";")) {
                String[] skillParts = skillEntry.split(":");
                if (skillParts.length >= 2) {
                    String skillName = skillParts[0].trim();
                    try {
                        Integer yearsExp = strictParseInt(
                                skillParts[1],
                                "yearsOfExperience",
                                String.format("skill '%s' for konsulent '%s'", skillName, consultant.getName())
                        );
                        Skill skill = skillMap.get(skillName);
                        if (skill != null) {
                            HasSkill hasSkill = new HasSkill();
                            hasSkill.setSkill(skill);
                            hasSkill.setSkillYearsOfExperience(yearsExp);
                            consultant.getSkills().add(hasSkill);
                        }
                    } catch (IllegalArgumentException e) {
                        log.error(e.getMessage());
                        throw e; // Stop hele datalasten
                    }
                }
            }
        }
    }

    private static Consultant getConsultant(String[] parts) {
        Consultant consultant = new Consultant();
        consultant.setName(parts[0].trim());
        consultant.setEmail(parts[1].trim());
        // Note: parts[2] is the role column from CSV, but we no longer set it on the consultant
        // Roles are now only stored in the AssignedTo relationships

        try {
            Integer yearsExp = strictParseInt(
                    parts[3],
                    "yearsOfExperience",
                    String.format("konsulent '%s' (%s)", parts[0].trim(), parts[1].trim())
            );
            consultant.setYearsOfExperience(yearsExp);
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            throw e; // Stop hele datalasten
        }

        consultant.setAvailability(Boolean.parseBoolean(parts[4].trim()));
        consultant.setWantsNewProject(Boolean.parseBoolean(parts[5].trim()));
        consultant.setOpenToRemote(Boolean.parseBoolean(parts[6].trim()));
        consultant.setOpenToRelocation(Boolean.parseBoolean(parts[7].trim()));

        // Parse preferred regions
        String regionsStr = parts[8].trim().replace("\"", "");
        List<String> regions = regionsStr.isEmpty() ? new ArrayList<>() :
                Arrays.asList(regionsStr.split(";"));
        consultant.setPreferredRegions(regions);
        return consultant;
    }

    private Map<String, Project> loadProjects(Map<String, Company> companyMap, Map<String, Skill> skillMap) throws IOException {
        Map<String, Project> projectMap = new HashMap<>();
        ClassPathResource resource = new ClassPathResource("data/projects.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.readLine(); // Skip header

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 5) {
                    Project project = new Project();
                    project.setName(parts[0].trim());

                    String companyName = parts[1].trim();
                    Company company = companyMap.get(companyName);
                    if (company != null) {
                        project.setCompany(company);
                    }

                    // Parse requirements
                    String requirementsStr = parts[2].trim().replace("\"", "");
                    List<String> requirements = requirementsStr.isEmpty() ? new ArrayList<>() :
                            Arrays.asList(requirementsStr.split(";"));
                    project.setRequirements(requirements);
                    project.setDate(LocalDateTime.now());

                    // Parse required skills (format: SkillName:MinYearsExp:IsMandatory;...)
                    String skillsStr = parts[3].trim().replace("\"", "");
                    if (!skillsStr.isEmpty()) {
                        for (String skillEntry : skillsStr.split(";")) {
                            String[] skillParts = skillEntry.split(":");
                            if (skillParts.length >= 3) {
                                String skillName = skillParts[0].trim();

                                try {
                                    Integer minYears = strictParseInt(
                                            skillParts[1],
                                            "minYearsOfExperience",
                                            String.format("required skill '%s' for prosjekt '%s'", skillName, parts[0].trim())
                                    );
                                    Boolean isMandatory = Boolean.parseBoolean(skillParts[2].trim());

                                    Skill skill = skillMap.get(skillName);
                                    if (skill != null) {
                                        RequiresSkill requiresSkill = new RequiresSkill();
                                        requiresSkill.setSkill(skill);
                                        requiresSkill.setMinYearsOfExperience(minYears);
                                        requiresSkill.setIsMandatory(isMandatory);
                                        project.getRequiredSkills().add(requiresSkill);
                                    }
                                } catch (IllegalArgumentException e) {
                                    log.error(e.getMessage());
                                    throw e; // Stop hele datalasten
                                }
                            }
                        }
                    }

                    // Parse roles (format: RoleName:Count;...)
                    String rolesStr = parts[4].trim().replace("\"", "");
                    if (!rolesStr.isEmpty()) {
                        Map<String, Integer> roles = new HashMap<>();
                        for (String roleEntry : rolesStr.split(";")) {
                            String[] roleParts = roleEntry.split(":");
                            if (roleParts.length >= 2) {
                                String roleName = roleParts[0].trim();

                                try {
                                    Integer count = strictParseInt(
                                            roleParts[1],
                                            "count",
                                            String.format("rolle '%s' for prosjekt '%s'", roleName, parts[0].trim())
                                    );
                                    roles.put(roleName, count);
                                } catch (IllegalArgumentException e) {
                                    log.error(e.getMessage());
                                    throw e; // Stop hele datalasten
                                }
                            }
                        }
                        project.setRoles(roles);
                    }

                    project = projectRepository.save(project);
                    projectMap.put(project.getName(), project);
                }
            }
        }
        return projectMap;
    }
}