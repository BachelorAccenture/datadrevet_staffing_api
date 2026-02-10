package com.example.demo.repository;

import com.example.demo.config.AbstractNeo4jTest;
import com.example.demo.model.*;
import com.example.demo.model.relationship.RequiresSkill;
import com.example.demo.model.relationship.RequiresTechnology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
class ProjectRepositoryTest extends AbstractNeo4jTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private SkillRepository skillRepository;

    private Project testProject;
    private Company testCompany;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();
        companyRepository.deleteAll();
        skillRepository.deleteAll();

        testCompany = new Company();
        testCompany.setName("TechCorp AS");
        testCompany.setField("Software Development");
        testCompany = companyRepository.save(testCompany);

        testProject = new Project();
        testProject.setName("E-Commerce Platform");
        testProject.setRequirements(Arrays.asList("Build scalable platform", "Cloud deployment"));
        testProject.setDate(LocalDateTime.now());
        testProject.setCompany(testCompany);
        testProject = projectRepository.save(testProject);
    }

    @Test
    void shouldSaveAndFindProject() {
        // When
        Optional<Project> found = projectRepository.findById(testProject.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("E-Commerce Platform");
        assertThat(found.get().getRequirements()).hasSize(2);
        assertThat(found.get().getCompany().getName()).isEqualTo("TechCorp AS");
    }

    @Test
    void shouldFindProjectByName() {
        // When
        Optional<Project> found = projectRepository.findByName("E-Commerce Platform");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(testProject.getId());
    }

    @Test
    void shouldFindProjectsByCompanyId() {
        // Given
        Project project2 = new Project();
        project2.setName("Mobile App");
        project2.setRequirements(List.of("iOS and Android"));
        project2.setDate(LocalDateTime.now());
        project2.setCompany(testCompany);
        projectRepository.save(project2);

        // When
        List<Project> found = projectRepository.findByCompanyId(testCompany.getId());

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Project::getName)
                .contains("E-Commerce Platform", "Mobile App");
    }

    @Test
    void shouldFindProjectsByRequiredSkillNames() {
        // Given
        Skill javaSkill = new Skill();
        javaSkill.setName("Java");
        javaSkill = skillRepository.save(javaSkill);

        RequiresSkill requiresSkill = new RequiresSkill();
        requiresSkill.setSkill(javaSkill);
        requiresSkill.setMinLevel(ProficiencyLevel.ADVANCED);
        requiresSkill.setIsMandatory(true);
        testProject.getRequiredSkills().add(requiresSkill);
        projectRepository.save(testProject);

        // When
        List<Project> found = projectRepository.findByRequiredSkillNames(Arrays.asList("Java"));

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("E-Commerce Platform");
    }


    @Test
    void shouldFindProjectsWithMultipleRequiredSkills() {
        // Given
        Skill javaSkill = new Skill();
        javaSkill.setName("Java");
        javaSkill = skillRepository.save(javaSkill);

        Skill pythonSkill = new Skill();
        pythonSkill.setName("Python");
        pythonSkill = skillRepository.save(pythonSkill);

        RequiresSkill requiresJava = new RequiresSkill();
        requiresJava.setSkill(javaSkill);
        requiresJava.setMinLevel(ProficiencyLevel.ADVANCED);
        requiresJava.setIsMandatory(true);

        RequiresSkill requiresPython = new RequiresSkill();
        requiresPython.setSkill(pythonSkill);
        requiresPython.setMinLevel(ProficiencyLevel.INTERMEDIATE);
        requiresPython.setIsMandatory(false);

        testProject.getRequiredSkills().add(requiresJava);
        testProject.getRequiredSkills().add(requiresPython);
        projectRepository.save(testProject);

        // When
        List<Project> foundByJava = projectRepository.findByRequiredSkillNames(Arrays.asList("Java"));
        List<Project> foundByPython = projectRepository.findByRequiredSkillNames(Arrays.asList("Python"));
        List<Project> foundByBoth = projectRepository.findByRequiredSkillNames(Arrays.asList("Java", "Python"));

        // Then
        assertThat(foundByJava).hasSize(1);
        assertThat(foundByPython).hasSize(1);
        assertThat(foundByBoth).hasSize(1);
    }

    @Test
    void shouldCheckIfProjectExistsByName() {
        // When
        boolean exists = projectRepository.existsByName("E-Commerce Platform");
        boolean notExists = projectRepository.existsByName("NonExistent Project");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldReturnEmptyWhenProjectNotFound() {
        // When
        Optional<Project> found = projectRepository.findByName("NonExistent Project");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenNoProjectsForCompany() {
        // Given
        Company otherCompany = new Company();
        otherCompany.setName("Other Company");
        otherCompany.setField("Other Field");
        otherCompany = companyRepository.save(otherCompany);

        // When
        List<Project> found = projectRepository.findByCompanyId(otherCompany.getId());

        // Then
        assertThat(found).isEmpty();
    }
}

