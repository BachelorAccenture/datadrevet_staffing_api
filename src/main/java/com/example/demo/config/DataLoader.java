package com.example.demo.config;

import com.example.demo.model.Company;
import com.example.demo.model.Consultant;
import com.example.demo.model.Project;
import com.example.demo.model.Skill;
import com.example.demo.model.relationship.AssignedTo;
import com.example.demo.model.relationship.HasSkill;
import com.example.demo.model.relationship.RequiresSkill;
import org.neo4j.driver.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.neo4j.core.Neo4jTemplate;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Configuration
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    @Bean
    @Profile("!test")
    CommandLineRunner loadData(Neo4jTemplate neo4jTemplate, Driver driver) {
        return args -> {
            // Clear existing data
            try (var session = driver.session()) {
                session.run("MATCH (n) DETACH DELETE n");
            }
            log.info("Cleared existing data");

            // ── Load Skills ─────────────────────────────────────────
            Map<String, Skill> skillMap = new HashMap<>();
            for (String[] row : readCsv("data/skills.csv")) {
                Skill skill = new Skill();
                skill.setName(row[0]);
                skill.setSynonyms(parseSemicolonList(row[1]));
                skill = neo4jTemplate.save(skill);
                skillMap.put(skill.getName(), skill);
            }
            log.info("Loaded {} skills", skillMap.size());

            // ── Load Companies ──────────────────────────────────────
            Map<String, Company> companyMap = new HashMap<>();
            for (String[] row : readCsv("data/companies.csv")) {
                Company company = new Company();
                company.setName(row[0]);
                company.setField(row[1]);
                company = neo4jTemplate.save(company);
                companyMap.put(company.getName(), company);
            }
            log.info("Loaded {} companies", companyMap.size());

            // ── Load Projects ───────────────────────────────────────
            Map<String, Project> projectMap = new HashMap<>();
            for (String[] row : readCsv("data/projects.csv")) {
                Project project = new Project();
                project.setName(row[0]);

                Company company = companyMap.get(row[1]);
                if (company == null) {
                    log.warn("Company '{}' not found for project '{}', skipping", row[1], row[0]);
                    continue;
                }
                project.setCompany(company);

                project.setStartDate(LocalDateTime.parse(row[2]));
                project.setEndDate(LocalDateTime.parse(row[3]));
                project.setRequirements(parseSemicolonList(row[4]));
                project.setRoles(parseRolesMap(row[5]));

                project = neo4jTemplate.save(project);
                projectMap.put(project.getName(), project);
            }
            log.info("Loaded {} projects", projectMap.size());

            // ── Load Project Required Skills ────────────────────────
            for (String[] row : readCsv("data/project_skills.csv")) {
                Project project = projectMap.get(row[0]);
                Skill skill = skillMap.get(row[1]);
                if (project == null || skill == null) {
                    log.warn("Skipping project skill: project='{}', skill='{}'", row[0], row[1]);
                    continue;
                }

                RequiresSkill rs = new RequiresSkill();
                rs.setSkill(skill);
                rs.setMinYearsOfExperience(Integer.parseInt(row[2]));
                rs.setIsMandatory(Boolean.parseBoolean(row[3]));
                project.getRequiredSkills().add(rs);

                neo4jTemplate.save(project);
                projectMap.put(project.getName(), project);
            }
            log.info("Loaded project skill requirements");

            // ── Load Consultants ────────────────────────────────────
            Map<String, Consultant> consultantMap = new HashMap<>();
            for (String[] row : readCsv("data/consultants.csv")) {
                Consultant consultant = new Consultant();
                consultant.setName(row[0]);
                consultant.setEmail(row[1]);
                consultant.setYearsOfExperience(Integer.parseInt(row[2]));
                consultant.setAvailability(true);
                consultant.setWantsNewProject(Boolean.parseBoolean(row[3]));
                consultant.setOpenToRemote(Boolean.parseBoolean(row[4]));
                consultant = neo4jTemplate.save(consultant);
                consultantMap.put(consultant.getName(), consultant);
            }
            log.info("Loaded {} consultants", consultantMap.size());

            // ── Load Consultant Skills ──────────────────────────────
            for (String[] row : readCsv("data/consultant_skills.csv")) {
                Consultant consultant = consultantMap.get(row[0]);
                Skill skill = skillMap.get(row[1]);
                if (consultant == null || skill == null) {
                    log.warn("Skipping consultant skill: consultant='{}', skill='{}'", row[0], row[1]);
                    continue;
                }

                HasSkill hs = new HasSkill();
                hs.setSkill(skill);
                hs.setSkillYearsOfExperience(Integer.parseInt(row[2]));
                consultant.getSkills().add(hs);

                neo4jTemplate.save(consultant);
                consultantMap.put(consultant.getName(), consultant);
            }
            log.info("Loaded consultant skills");

            // ── Load Consultant Project Assignments ─────────────────
            for (String[] row : readCsv("data/consultant_projects.csv")) {
                Consultant consultant = consultantMap.get(row[0]);
                Project project = projectMap.get(row[1]);
                if (consultant == null || project == null) {
                    log.warn("Skipping assignment: consultant='{}', project='{}'", row[0], row[1]);
                    continue;
                }

                AssignedTo at = new AssignedTo();
                at.setProject(project);
                at.setRole(row[2]);
                at.setAllocationPercent(Integer.parseInt(row[3]));
                at.setIsActive(Boolean.parseBoolean(row[4]));
                if (row.length > 5 && !row[5].isBlank()) {
                    at.setStartDate(LocalDateTime.parse(row[5]));
                }
                if (row.length > 6 && !row[6].isBlank()) {
                    at.setEndDate(LocalDateTime.parse(row[6]));
                }
                consultant.getProjectAssignments().add(at);

                neo4jTemplate.save(consultant);
                consultantMap.put(consultant.getName(), consultant);
            }
            log.info("Loaded consultant project assignments");

            // ── Recalculate availability based on active assignments ─
            for (Consultant consultant : consultantMap.values()) {
                final boolean hasActiveAssignment = consultant.getProjectAssignments().stream()
                        .anyMatch(a -> Boolean.TRUE.equals(a.getIsActive()));
                final boolean correctAvailability = !hasActiveAssignment;

                if (correctAvailability != Boolean.TRUE.equals(consultant.getAvailability())) {
                    log.info("Correcting availability for '{}': {} -> {}",
                            consultant.getName(), consultant.getAvailability(), correctAvailability);
                    consultant.setAvailability(correctAvailability);
                    neo4jTemplate.save(consultant);
                }
            }
            log.info("Recalculated consultant availability");

            log.info("Data loading complete!");
        };
    }

    // ── CSV Parsing Helpers ─────────────────────────────────────────

    /**
     * Reads a CSV file from the classpath, skipping the header row.
     * Handles quoted fields containing commas.
     */
    private List<String[]> readCsv(String classpathLocation) {
        List<String[]> rows = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource(classpathLocation);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                // Skip header
                reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) continue;
                    rows.add(parseCsvLine(line));
                }
            }
        } catch (Exception e) {
            log.error("Failed to read CSV file: {}", classpathLocation, e);
        }
        return rows;
    }

    /**
     * Parses a single CSV line, respecting quoted fields.
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString().trim());
        return fields.toArray(new String[0]);
    }

    /**
     * Splits a semicolon-delimited string into a list.
     */
    private List<String> parseSemicolonList(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(value.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * Parses a role string like "Backend Developer:2;Frontend Developer:1" into a Map.
     */
    private Map<String, Integer> parseRolesMap(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyMap();
        }
        Map<String, Integer> roles = new HashMap<>();
        for (String entry : value.split(";")) {
            String[] parts = entry.trim().split(":");
            if (parts.length == 2) {
                roles.put(parts[0].trim(), Integer.parseInt(parts[1].trim()));
            }
        }
        return roles;
    }
}