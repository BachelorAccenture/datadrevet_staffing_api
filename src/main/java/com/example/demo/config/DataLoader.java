package com.example.demo.config;

import com.example.demo.model.*;
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

                // Load consultants from CSV
                loadConsultants(skillMap);
                log.info("[DataLoader] - Loaded {} consultants", consultantRepository.count());

                // Load projects from CSV
                loadProjects(companyMap, skillMap);
                log.info("[DataLoader] - Loaded {} projects", projectRepository.count());

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

    private void loadConsultants(Map<String, Skill> skillMap) throws IOException {
        ClassPathResource resource = new ClassPathResource("data/consultants.csv");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()))) {
            reader.readLine(); // Skip header
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",", -1);
                if (parts.length >= 11) {
                    Consultant consultant = getConsultant(parts);

                    // Parse and add skills from both columns
                    addSkillsToConsultant(consultant, parts[9], skillMap);
                    addSkillsToConsultant(consultant, parts[10], skillMap);

                    consultantRepository.save(consultant);
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
                    Integer yearsExp = Integer.parseInt(skillParts[1].trim());
                    Skill skill = skillMap.get(skillName);
                    if (skill != null) {
                        HasSkill hasSkill = new HasSkill();
                        hasSkill.setSkill(skill);
                        hasSkill.setSkillYearsOfExperience(yearsExp);
                        consultant.getSkills().add(hasSkill);
                    }
                }
            }
        }
    }

    private static Consultant getConsultant(String[] parts) {
        Consultant consultant = new Consultant();
        consultant.setName(parts[0].trim());
        consultant.setEmail(parts[1].trim());
        consultant.setRole(parts[2].trim());
        consultant.setYearsOfExperience(Integer.parseInt(parts[3].trim()));
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

    private void loadProjects(Map<String, Company> companyMap, Map<String, Skill> skillMap) throws IOException {
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

                    // Parse required skills (format: SkillName:MinLevel:IsMandatory;...)
                    String skillsStr = parts[3].trim().replace("\"", "");
                    if (!skillsStr.isEmpty()) {
                        for (String skillEntry : skillsStr.split(";")) {
                            String[] skillParts = skillEntry.split(":");
                            if (skillParts.length >= 3) {
                                String skillName = skillParts[0].trim();
                                ProficiencyLevel minLevel = ProficiencyLevel.valueOf(skillParts[1].trim());
                                Boolean isMandatory = Boolean.parseBoolean(skillParts[2].trim());

                                Skill skill = skillMap.get(skillName);
                                if (skill != null) {
                                    RequiresSkill requiresSkill = new RequiresSkill();
                                    requiresSkill.setSkill(skill);
                                    requiresSkill.setMinLevel(minLevel);
                                    requiresSkill.setIsMandatory(isMandatory);
                                    project.getRequiredSkills().add(requiresSkill);
                                }
                            }
                        }
                    }
                    projectRepository.save(project);
                }
            }
        }
    }
}
