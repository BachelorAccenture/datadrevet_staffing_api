package com.example.demo.repository;

import com.example.demo.config.AbstractNeo4jTest;
import com.example.demo.model.Consultant;
import com.example.demo.model.Skill;
import com.example.demo.model.relationship.HasSkill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
class ConsultantRepositoryTest extends AbstractNeo4jTest {

    @Autowired
    private ConsultantRepository consultantRepository;

    @Autowired
    private SkillRepository skillRepository;

    private Consultant testConsultant;

    @BeforeEach
    void setUp() {
        consultantRepository.deleteAll();
        skillRepository.deleteAll();

        testConsultant = new Consultant();
        testConsultant.setName("Ola Nordmann");
        testConsultant.setEmail("ola.nordmann@example.com");
        testConsultant.setRole("Senior Developer");
        testConsultant.setYearsOfExperience(8);
        testConsultant.setAvailability(true);
        testConsultant.setWantsNewProject(true);
        testConsultant.setOpenToRemote(true);
        testConsultant.setOpenToRelocation(false);
        testConsultant.setPreferredRegions(Arrays.asList("Oslo", "Bergen"));
        testConsultant = consultantRepository.save(testConsultant);
    }

    @Test
    void shouldSaveAndFindConsultant() {
        // When
        Optional<Consultant> found = consultantRepository.findById(testConsultant.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ola Nordmann");
        assertThat(found.get().getEmail()).isEqualTo("ola.nordmann@example.com");
        assertThat(found.get().getYearsOfExperience()).isEqualTo(8);
    }

    @Test
    void shouldFindConsultantByEmail() {
        // When
        Optional<Consultant> found = consultantRepository.findByEmail("ola.nordmann@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Ola Nordmann");
    }

    @Test
    void shouldFindAvailableConsultants() {
        // Given
        Consultant unavailable = new Consultant();
        unavailable.setName("Kari Hansen");
        unavailable.setEmail("kari.hansen@example.com");
        unavailable.setRole("Developer");
        unavailable.setYearsOfExperience(5);
        unavailable.setAvailability(false);
        unavailable.setWantsNewProject(false);
        consultantRepository.save(unavailable);

        // When
        List<Consultant> available = consultantRepository.findByAvailabilityTrue();

        // Then
        assertThat(available).hasSize(1);
        assertThat(available.get(0).getName()).isEqualTo("Ola Nordmann");
    }

    @Test
    void shouldFindConsultantsWantingNewProject() {
        // Given
        Consultant notInterested = new Consultant();
        notInterested.setName("Per Jensen");
        notInterested.setEmail("per.jensen@example.com");
        notInterested.setRole("Developer");
        notInterested.setYearsOfExperience(3);
        notInterested.setAvailability(true);
        notInterested.setWantsNewProject(false);
        consultantRepository.save(notInterested);

        // When
        List<Consultant> wanting = consultantRepository.findByWantsNewProjectTrue();

        // Then
        assertThat(wanting).hasSize(1);
        assertThat(wanting.get(0).getName()).isEqualTo("Ola Nordmann");
    }

    @Test
    void shouldFindConsultantsBySkillNames() {
        // Given
        Skill javaSkill = new Skill();
        javaSkill.setName("Java");
        javaSkill = skillRepository.save(javaSkill);

        HasSkill hasSkill = new HasSkill();
        hasSkill.setSkill(javaSkill);
        testConsultant.getSkills().add(hasSkill);
        consultantRepository.save(testConsultant);

        // When
        List<Consultant> found = consultantRepository.findBySkillNames(Arrays.asList("Java"));

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Ola Nordmann");
    }


    @Test
    void findAvailableWithMinExperience_withMixedExperience_returnsOnlyExperienced() {
        // given
        final Consultant junior = new Consultant();
        junior.setName("Junior Dev");
        junior.setEmail("junior@example.com");
        junior.setRole("Junior Developer");
        junior.setYearsOfExperience(2);
        junior.setAvailability(true);
        junior.setWantsNewProject(true);
        consultantRepository.save(junior);

        // when
        final List<Consultant> experienced = consultantRepository.findAvailableWithMinExperience(5);

        // then
        assertThat(experienced).hasSize(1);
        assertThat(experienced.getFirst().getName()).isEqualTo("Ola Nordmann");
        assertThat(experienced.getFirst().getYearsOfExperience()).isGreaterThanOrEqualTo(5);
    }

    @Test
    void existsByEmail_withExistingEmail_returnsTrue() {
        // given - testConsultant from setUp

        // when
        final boolean exists = consultantRepository.existsByEmail("ola.nordmann@example.com");
        final boolean notExists = consultantRepository.existsByEmail("nonexistent@example.com");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}

