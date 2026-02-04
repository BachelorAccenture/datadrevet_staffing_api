package com.example.demo.repository;

import com.example.demo.model.Technology;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
class TechnologyRepositoryTest {

    @Autowired
    private TechnologyRepository technologyRepository;

    private Technology testTechnology;

    @BeforeEach
    void setUp() {
        technologyRepository.deleteAll();

        testTechnology = new Technology();
        testTechnology.setName("Docker");
        testTechnology.setSynonyms(Arrays.asList("Docker Engine", "Docker CE"));
        testTechnology = technologyRepository.save(testTechnology);
    }

    @Test
    void shouldSaveAndFindTechnology() {
        // When
        Optional<Technology> found = technologyRepository.findById(testTechnology.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Docker");
        assertThat(found.get().getSynonyms()).containsExactlyInAnyOrder("Docker Engine", "Docker CE");
    }

    @Test
    void shouldFindTechnologyByName() {
        // When
        Optional<Technology> found = technologyRepository.findByName("Docker");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(testTechnology.getId());
    }

    @Test
    void shouldFindTechnologyByNameIgnoreCase() {
        // When
        Optional<Technology> found = technologyRepository.findByNameIgnoreCase("docker");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Docker");
    }

    @Test
    void shouldFindTechnologiesByNameContainingIgnoreCase() {
        // Given
        Technology kubernetes = new Technology();
        kubernetes.setName("Kubernetes");
        technologyRepository.save(kubernetes);

        Technology redis = new Technology();
        redis.setName("Redis");
        technologyRepository.save(redis);

        // When
        List<Technology> found = technologyRepository.findByNameContainingIgnoreCase("doc");

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getName()).isEqualTo("Docker");
    }

    @Test
    void shouldFindTechnologyByNameOrSynonym() {
        // When - find by exact name
        Optional<Technology> foundByName = technologyRepository.findByNameOrSynonym("Docker");

        // Then
        assertThat(foundByName).isPresent();
        assertThat(foundByName.get().getName()).isEqualTo("Docker");

        // When - find by synonym
        Optional<Technology> foundBySynonym = technologyRepository.findByNameOrSynonym("Docker Engine");

        // Then
        assertThat(foundBySynonym).isPresent();
        assertThat(foundBySynonym.get().getName()).isEqualTo("Docker");
    }

    @Test
    void shouldCheckIfTechnologyExistsByName() {
        // When
        boolean exists = technologyRepository.existsByName("Docker");
        boolean notExists = technologyRepository.existsByName("Kubernetes");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void shouldReturnEmptyWhenTechnologyNotFound() {
        // When
        Optional<Technology> found = technologyRepository.findByName("NonExistentTechnology");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldReturnEmptyWhenSynonymNotFound() {
        // When
        Optional<Technology> found = technologyRepository.findByNameOrSynonym("NonExistentSynonym");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    void shouldHandleTechnologyWithNoSynonyms() {
        // Given
        Technology git = new Technology();
        git.setName("Git");
        git.setSynonyms(List.of());
        git = technologyRepository.save(git);

        // When
        Optional<Technology> found = technologyRepository.findByName("Git");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getSynonyms()).isEmpty();
    }
}

