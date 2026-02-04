package com.example.demo.repository;

import com.example.demo.model.Company;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.neo4j.DataNeo4jTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataNeo4jTest
class CompanyRepositoryTest {

    @Autowired
    private CompanyRepository companyRepository;

    private Company testCompany;

    @BeforeEach
    void setUp() {
        companyRepository.deleteAll();

        testCompany = new Company();
        testCompany.setName("TechCorp AS");
        testCompany.setField("Software Development");
        testCompany = companyRepository.save(testCompany);
    }

    @Test
    void findById_withExistingCompany_returnsCompany() {
        // given - company saved in setUp

        // when
        final Optional<Company> found = companyRepository.findById(testCompany.getId());

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("TechCorp AS");
        assertThat(found.get().getField()).isEqualTo("Software Development");
    }

    @Test
    void findByName_withExactName_returnsCompany() {
        // given - company saved in setUp

        // when
        final Optional<Company> found = companyRepository.findByName("TechCorp AS");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getId()).isEqualTo(testCompany.getId());
    }

    @Test
    void findByNameIgnoreCase_withDifferentCase_returnsCompany() {
        // given - company saved in setUp

        // when
        final Optional<Company> found = companyRepository.findByNameIgnoreCase("techcorp as");

        // then
        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("TechCorp AS");
    }

    @Test
    void findByNameContainingIgnoreCase_withPartialName_returnsMatchingCompanies() {
        // given
        final Company company2 = new Company();
        company2.setName("TechStart Solutions");
        company2.setField("Startups");
        companyRepository.save(company2);

        // when
        final List<Company> found = companyRepository.findByNameContainingIgnoreCase("tech");

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Company::getName)
                .contains("TechCorp AS", "TechStart Solutions");
    }

    @Test
    void findByField_withMatchingField_returnsCompanies() {
        // given
        final Company company2 = new Company();
        company2.setName("DevCorp");
        company2.setField("Software Development");
        companyRepository.save(company2);

        // when
        final List<Company> found = companyRepository.findByField("Software Development");

        // then
        assertThat(found).hasSize(2);
        assertThat(found).extracting(Company::getName)
                .contains("TechCorp AS", "DevCorp");
    }

    @Test
    void existsByName_withExistingName_returnsTrue() {
        // given - company saved in setUp

        // when
        final boolean exists = companyRepository.existsByName("TechCorp AS");
        final boolean notExists = companyRepository.existsByName("NonExistent Company");

        // then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    void findByName_withNonExistentName_returnsEmpty() {
        // given - only testCompany exists

        // when
        final Optional<Company> found = companyRepository.findByName("NonExistent Company");

        // then
        assertThat(found).isEmpty();
    }
}

