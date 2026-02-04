package com.example.demo.repository;

import com.example.demo.model.Skill;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
class SkillRepositoryTest {

    @Autowired
    private SkillRepository skillRepository;

    private Skill testSkill;

    @BeforeEach
    void setUp() {
        skillRepository.deleteAll();

        testSkill = new Skill();
        testSkill.setName("Java");
        testSkill.setSynonyms(Arrays.asList("Java SE", "Java EE"));
        testSkill = skillRepository.save(testSkill);
    }

    @Test
    void shouldSaveAndFindSkill() {
        // When
        Optional<Skill> found = skillRepository.findById(testSkill.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Java");
        assertThat(found.get().getSynonyms()).containsExactlyInAnyOrder("Java SE", "Java EE");
    }

    @Test
    void shouldFindSkillByName() {
        // When
        Optional<Skill> found = skillRepository.findByName("Java");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(testSkill.getId());
    }

    @Test
    void shouldFindSkillByNameIgnoreCase() {
        // When
        Optional<Skill> found = skillRepository.findByNameIgnoreCase("java");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Java");
    }

    @Test
    void shouldFindSkillsByNameContainingIgnoreCase() {
        // Given
        Skill javascript = new Skill();
        javascript.setName("JavaScript");
        skillRepository.save(javascript);

        // When
        List<Skill> found = skillRepository.findByNameContainingIgnoreCase("java");

        // Then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Skill::getName)
                .contains("Java", "JavaScript");
    }

    @Test
    void shouldFindSkillByNameOrSynonym() {
        // When - find by exact name
        Optional<Skill> foundByName = skillRepository.findByNameOrSynonym("Java");

        // Then
        assertThat(foundByName).isPresent();
        assertThat(foundByName.get().getName()).isEqualTo("Java");

        // When - find by synonym
        Optional<Skill> foundBySynonym = skillRepository.findByNameOrSynonym("Java SE");

        // Then
        assertThat(foundBySynonym).isPresent();
        assertThat(foundBySynonym.get().getName()).isEqualTo("Java");
    }

    @Test
    void shouldCheckIfSkillExistsByName() {
        // When
        boolean exists = skillRepository.existsByName("Java");
        boolean notExists = skillRepository.existsByName("Python");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldReturnEmptyWhenSkillNotFound() {
        // When
        Optional<Skill> found = skillRepository.findByName("NonExistentSkill");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenSynonymNotFound() {
        // When
        Optional<Skill> found = skillRepository.findByNameOrSynonym("NonExistentSynonym");

        // Then
        assertThat(found).isEmpty();
    }
}

