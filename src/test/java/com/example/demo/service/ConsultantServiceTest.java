package com.example.demo.service;

import com.example.demo.model.Consultant;
import com.example.demo.model.Skill;
import com.example.demo.model.Technology;
import com.example.demo.model.relationship.HasSkill;
import com.example.demo.model.relationship.Knows;
import com.example.demo.repository.ConsultantRepository;
import com.example.demo.repository.SkillRepository;
import com.example.demo.repository.TechnologyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultantServiceTest {

    @Mock
    private ConsultantRepository consultantRepository;

    @Mock
    private SkillRepository skillRepository;

    @Mock
    private TechnologyRepository technologyRepository;

    @InjectMocks
    private ConsultantService consultantService;

    @Captor
    private ArgumentCaptor<Consultant> consultantCaptor;

    private Consultant testConsultant;
    private Skill testSkill;
    private Technology testTechnology;

    @BeforeEach
    void setUp() {
        testConsultant = new Consultant();
        testConsultant.setId("consultant-123");
        testConsultant.setName("Test Consultant");
        testConsultant.setEmail("test@example.com");
        testConsultant.setRole("Developer");
        testConsultant.setYearsOfExperience(5);
        testConsultant.setAvailability(true);
        testConsultant.setWantsNewProject(true);
        testConsultant.setOpenToRemote(true);
        testConsultant.setOpenToRelocation(false);
        testConsultant.setPreferredRegions(List.of("Oslo", "Bergen"));

        testSkill = new Skill();
        testSkill.setId("skill-123");
        testSkill.setName("Java");
        testSkill.setSynonyms(List.of("JDK", "Java SE"));

        testTechnology = new Technology();
        testTechnology.setId("tech-123");
        testTechnology.setName("Neo4j");
        testTechnology.setSynonyms(List.of("Neo4j Graph Database"));
    }

    @Nested
    @DisplayName("create")
    class Create {

        @Test
        void create_withValidConsultant_savesAndReturnsConsultant() {
            // given
            when(consultantRepository.save(any(Consultant.class))).thenReturn(testConsultant);

            // when
            final Consultant result = consultantService.create(testConsultant);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("consultant-123");
            assertThat(result.getEmail()).isEqualTo("test@example.com");
            verify(consultantRepository).save(testConsultant);
        }
    }

    @Nested
    @DisplayName("findById")
    class FindById {

        @Test
        void findById_withExistingId_returnsConsultant() {
            // given
            when(consultantRepository.findById("consultant-123")).thenReturn(Optional.of(testConsultant));

            // when
            final Optional<Consultant> result = consultantService.findById("consultant-123");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo("consultant-123");
            verify(consultantRepository).findById("consultant-123");
        }

        @Test
        void findById_withNonExistingId_returnsEmpty() {
            // given
            when(consultantRepository.findById("non-existing")).thenReturn(Optional.empty());

            // when
            final Optional<Consultant> result = consultantService.findById("non-existing");

            // then
            assertThat(result).isEmpty();
            verify(consultantRepository).findById("non-existing");
        }
    }

    @Nested
    @DisplayName("findByEmail")
    class FindByEmail {

        @Test
        void findByEmail_withExistingEmail_returnsConsultant() {
            // given
            when(consultantRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testConsultant));

            // when
            final Optional<Consultant> result = consultantService.findByEmail("test@example.com");

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getEmail()).isEqualTo("test@example.com");
            verify(consultantRepository).findByEmail("test@example.com");
        }

        @Test
        void findByEmail_withNonExistingEmail_returnsEmpty() {
            // given
            when(consultantRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

            // when
            final Optional<Consultant> result = consultantService.findByEmail("unknown@example.com");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAll")
    class FindAll {

        @Test
        void findAll_withConsultants_returnsList() {
            // given
            final Consultant secondConsultant = new Consultant();
            secondConsultant.setId("consultant-456");
            secondConsultant.setName("Second Consultant");
            when(consultantRepository.findAll()).thenReturn(List.of(testConsultant, secondConsultant));

            // when
            final List<Consultant> result = consultantService.findAll();

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Consultant::getId)
                    .containsExactly("consultant-123", "consultant-456");
        }

        @Test
        void findAll_withNoConsultants_returnsEmptyList() {
            // given
            when(consultantRepository.findAll()).thenReturn(Collections.emptyList());

            // when
            final List<Consultant> result = consultantService.findAll();

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findAvailable")
    class FindAvailable {

        @Test
        void findAvailable_withAvailableConsultants_returnsList() {
            // given
            when(consultantRepository.findByAvailabilityTrue()).thenReturn(List.of(testConsultant));

            // when
            final List<Consultant> result = consultantService.findAvailable();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getAvailability()).isTrue();
            verify(consultantRepository).findByAvailabilityTrue();
        }
    }

    @Nested
    @DisplayName("findWantingNewProject")
    class FindWantingNewProject {

        @Test
        void findWantingNewProject_withMatchingConsultants_returnsList() {
            // given
            when(consultantRepository.findByWantsNewProjectTrue()).thenReturn(List.of(testConsultant));

            // when
            final List<Consultant> result = consultantService.findWantingNewProject();

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getWantsNewProject()).isTrue();
            verify(consultantRepository).findByWantsNewProjectTrue();
        }
    }

    @Nested
    @DisplayName("findBySkillNames")
    class FindBySkillNames {

        @Test
        void findBySkillNames_withMatchingSkills_returnsConsultants() {
            // given
            final List<String> skillNames = List.of("Java", "Python");
            when(consultantRepository.findBySkillNames(skillNames)).thenReturn(List.of(testConsultant));

            // when
            final List<Consultant> result = consultantService.findBySkillNames(skillNames);

            // then
            assertThat(result).hasSize(1);
            verify(consultantRepository).findBySkillNames(skillNames);
        }

        @Test
        void findBySkillNames_withNoMatchingSkills_returnsEmptyList() {
            // given
            final List<String> skillNames = List.of("Cobol");
            when(consultantRepository.findBySkillNames(skillNames)).thenReturn(Collections.emptyList());

            // when
            final List<Consultant> result = consultantService.findBySkillNames(skillNames);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByTechnologyNames")
    class FindByTechnologyNames {

        @Test
        void findByTechnologyNames_withMatchingTechnologies_returnsConsultants() {
            // given
            final List<String> technologyNames = List.of("Neo4j", "PostgreSQL");
            when(consultantRepository.findByTechnologyNames(technologyNames)).thenReturn(List.of(testConsultant));

            // when
            final List<Consultant> result = consultantService.findByTechnologyNames(technologyNames);

            // then
            assertThat(result).hasSize(1);
            verify(consultantRepository).findByTechnologyNames(technologyNames);
        }
    }

    @Nested
    @DisplayName("findAvailableWithMinExperience")
    class FindAvailableWithMinExperience {

        @Test
        void findAvailableWithMinExperience_withMatchingConsultants_returnsList() {
            // given
            when(consultantRepository.findAvailableWithMinExperience(3)).thenReturn(List.of(testConsultant));

            // when
            final List<Consultant> result = consultantService.findAvailableWithMinExperience(3);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getYearsOfExperience()).isGreaterThanOrEqualTo(3);
            verify(consultantRepository).findAvailableWithMinExperience(3);
        }

        @Test
        void findAvailableWithMinExperience_withHighThreshold_returnsEmptyList() {
            // given
            when(consultantRepository.findAvailableWithMinExperience(20)).thenReturn(Collections.emptyList());

            // when
            final List<Consultant> result = consultantService.findAvailableWithMinExperience(20);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("update")
    class Update {

        @Test
        void update_withExistingConsultant_updatesAndReturns() {
            // given
            final Consultant updatedData = new Consultant();
            updatedData.setName("Updated Name");
            updatedData.setEmail("updated@example.com");
            updatedData.setRole("Senior Developer");
            updatedData.setYearsOfExperience(7);
            updatedData.setAvailability(false);
            updatedData.setWantsNewProject(false);
            updatedData.setOpenToRelocation(true);
            updatedData.setOpenToRemote(false);
            updatedData.setPreferredRegions(List.of("Trondheim"));

            when(consultantRepository.findById("consultant-123")).thenReturn(Optional.of(testConsultant));
            when(consultantRepository.save(any(Consultant.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            final Consultant result = consultantService.update("consultant-123", updatedData);

            // then
            verify(consultantRepository).save(consultantCaptor.capture());
            final Consultant savedConsultant = consultantCaptor.getValue();

            assertThat(savedConsultant.getName()).isEqualTo("Updated Name");
            assertThat(savedConsultant.getEmail()).isEqualTo("updated@example.com");
            assertThat(savedConsultant.getRole()).isEqualTo("Senior Developer");
            assertThat(savedConsultant.getYearsOfExperience()).isEqualTo(7);
            assertThat(savedConsultant.getAvailability()).isFalse();
            assertThat(savedConsultant.getWantsNewProject()).isFalse();
            assertThat(savedConsultant.getOpenToRelocation()).isTrue();
            assertThat(savedConsultant.getOpenToRemote()).isFalse();
            assertThat(savedConsultant.getPreferredRegions()).containsExactly("Trondheim");
        }

        @Test
        void update_withNonExistingConsultant_throwsException() {
            // given
            final Consultant updatedData = new Consultant();
            when(consultantRepository.findById("non-existing")).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> consultantService.update("non-existing", updatedData))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Consultant not found with id: non-existing");

            verify(consultantRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        void delete_withValidId_deletesConsultant() {
            // given / when
            consultantService.delete("consultant-123");

            // then
            verify(consultantRepository).deleteById("consultant-123");
        }
    }

    @Nested
    @DisplayName("addSkill")
    class AddSkill {

        @Test
        void addSkill_withValidIds_addsSkillToConsultant() {
            // given
            when(consultantRepository.findById("consultant-123")).thenReturn(Optional.of(testConsultant));
            when(skillRepository.findById("skill-123")).thenReturn(Optional.of(testSkill));
            when(consultantRepository.save(any(Consultant.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            final Consultant result = consultantService.addSkill("consultant-123", "skill-123", 5);

            // then
            verify(consultantRepository).save(consultantCaptor.capture());
            final Consultant savedConsultant = consultantCaptor.getValue();

            assertThat(savedConsultant.getSkills()).hasSize(1);
            final HasSkill addedSkill = savedConsultant.getSkills().iterator().next();
            assertThat(addedSkill.getSkill().getName()).isEqualTo("Java");
            assertThat(addedSkill.getYearsExperience()).isEqualTo(5);
        }

        @Test
        void addSkill_withNonExistingConsultant_throwsException() {
            // given
            when(consultantRepository.findById("non-existing")).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> consultantService.addSkill("non-existing", "skill-123", 2))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Consultant not found with id: non-existing");

            verify(skillRepository, never()).findById(any());
            verify(consultantRepository, never()).save(any());
        }

        @Test
        void addSkill_withNonExistingSkill_throwsException() {
            // given
            when(consultantRepository.findById("consultant-123")).thenReturn(Optional.of(testConsultant));
            when(skillRepository.findById("non-existing")).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> consultantService.addSkill("consultant-123", "non-existing", 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Skill not found with id: non-existing");

            verify(consultantRepository, never()).save(any());
        }

        @Test
        void addSkill_withYearsExperience_setsCorrectValue() {
            // given
            when(consultantRepository.findById("consultant-123")).thenReturn(Optional.of(testConsultant));
            when(skillRepository.findById("skill-123")).thenReturn(Optional.of(testSkill));
            when(consultantRepository.save(any(Consultant.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            consultantService.addSkill("consultant-123", "skill-123", 10);

            // then
            verify(consultantRepository).save(consultantCaptor.capture());
            final HasSkill addedSkill = consultantCaptor.getValue().getSkills().iterator().next();
            assertThat(addedSkill.getYearsExperience()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("addTechnology")
    class AddTechnology {

        @Test
        void addTechnology_withValidIds_addsTechnologyToConsultant() {
            // given
            when(consultantRepository.findById("consultant-123")).thenReturn(Optional.of(testConsultant));
            when(technologyRepository.findById("tech-123")).thenReturn(Optional.of(testTechnology));
            when(consultantRepository.save(any(Consultant.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            final Consultant result = consultantService.addTechnology(
                    "consultant-123", "tech-123", 3);

            // then
            verify(consultantRepository).save(consultantCaptor.capture());
            final Consultant savedConsultant = consultantCaptor.getValue();

            assertThat(savedConsultant.getTechnologies()).hasSize(1);
            final Knows addedTech = savedConsultant.getTechnologies().iterator().next();
            assertThat(addedTech.getTechnology().getName()).isEqualTo("Neo4j");
            assertThat(addedTech.getYearsExperience()).isEqualTo(3);
        }

        @Test
        void addTechnology_withNonExistingConsultant_throwsException() {
            // given
            when(consultantRepository.findById("non-existing")).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> consultantService.addTechnology(
                    "non-existing", "tech-123", 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Consultant not found with id: non-existing");

            verify(technologyRepository, never()).findById(any());
            verify(consultantRepository, never()).save(any());
        }

        @Test
        void addTechnology_withNonExistingTechnology_throwsException() {
            // given
            when(consultantRepository.findById("consultant-123")).thenReturn(Optional.of(testConsultant));
            when(technologyRepository.findById("non-existing")).thenReturn(Optional.empty());

            // when / then
            assertThatThrownBy(() -> consultantService.addTechnology(
                    "consultant-123", "non-existing", 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Technology not found with id: non-existing");

            verify(consultantRepository, never()).save(any());
        }

        @Test
        void addTechnology_withNullYearsExperience_savesWithNull() {
            // given
            when(consultantRepository.findById("consultant-123")).thenReturn(Optional.of(testConsultant));
            when(technologyRepository.findById("tech-123")).thenReturn(Optional.of(testTechnology));
            when(consultantRepository.save(any(Consultant.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // when
            consultantService.addTechnology("consultant-123", "tech-123", null);

            // then
            verify(consultantRepository).save(consultantCaptor.capture());
            final Knows addedTech = consultantCaptor.getValue().getTechnologies().iterator().next();
            assertThat(addedTech.getYearsExperience()).isNull();
        }
    }

    @Nested
    @DisplayName("existsByEmail")
    class ExistsByEmail {

        @Test
        void existsByEmail_withExistingEmail_returnsTrue() {
            // given
            when(consultantRepository.existsByEmail("test@example.com")).thenReturn(true);

            // when
            final boolean result = consultantService.existsByEmail("test@example.com");

            // then
            assertThat(result).isTrue();
        }

        @Test
        void existsByEmail_withNonExistingEmail_returnsFalse() {
            // given
            when(consultantRepository.existsByEmail("unknown@example.com")).thenReturn(false);

            // when
            final boolean result = consultantService.existsByEmail("unknown@example.com");

            // then
            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("searchConsultants")
    class SearchConsultants {

        @Test
        void searchConsultants_withAllParameters_returnsMatchingConsultants() {
            // given
            final List<String> skillNames = List.of("Java", "Python");
            final List<String> technologyNames = List.of("Neo4j", "PostgreSQL");
            final String role = "Developer";
            final Integer minYears = 3;

            when(consultantRepository.searchConsultants(skillNames, technologyNames, role, minYears))
                    .thenReturn(List.of(testConsultant));

            // when
            final List<Consultant> result = consultantService.searchConsultants(
                    skillNames, technologyNames, role, minYears);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getId()).isEqualTo("consultant-123");
            verify(consultantRepository).searchConsultants(skillNames, technologyNames, role, minYears);
        }

        @Test
        void searchConsultants_withNullSkillsAndTechnologies_convertsToEmptyLists() {
            // given
            final String role = "Developer";
            final Integer minYears = 5;

            when(consultantRepository.searchConsultants(
                    Collections.emptyList(), Collections.emptyList(), role, minYears))
                    .thenReturn(List.of(testConsultant));

            // when
            final List<Consultant> result = consultantService.searchConsultants(
                    null, null, role, minYears);

            // then
            assertThat(result).hasSize(1);
            verify(consultantRepository).searchConsultants(
                    Collections.emptyList(), Collections.emptyList(), role, minYears);
        }

        @Test
        void searchConsultants_withNoMatches_returnsEmptyList() {
            // given
            final List<String> skillNames = List.of("Cobol");
            final List<String> technologyNames = List.of("Mainframe");
            final String role = "DevOps Engineer";
            final Integer minYears = 15;

            when(consultantRepository.searchConsultants(skillNames, technologyNames, role, minYears))
                    .thenReturn(Collections.emptyList());

            // when
            final List<Consultant> result = consultantService.searchConsultants(
                    skillNames, technologyNames, role, minYears);

            // then
            assertThat(result).isEmpty();
            verify(consultantRepository).searchConsultants(skillNames, technologyNames, role, minYears);
        }

        @Test
        void searchConsultants_withOnlyRole_returnsConsultants() {
            // given
            final String role = "Developer";

            when(consultantRepository.searchConsultants(
                    Collections.emptyList(), Collections.emptyList(), role, null))
                    .thenReturn(List.of(testConsultant));

            // when
            final List<Consultant> result = consultantService.searchConsultants(
                    null, null, role, null);

            // then
            assertThat(result).hasSize(1);
            verify(consultantRepository).searchConsultants(
                    Collections.emptyList(), Collections.emptyList(), role, null);
        }

        @Test
        void searchConsultants_withMultipleResults_returnsAll() {
            // given
            final Consultant secondConsultant = new Consultant();
            secondConsultant.setId("consultant-456");
            secondConsultant.setName("Second Consultant");
            secondConsultant.setRole("Developer");

            final List<String> skillNames = List.of("Java");
            final List<String> technologyNames = Collections.emptyList();

            when(consultantRepository.searchConsultants(skillNames, technologyNames, null, null))
                    .thenReturn(List.of(testConsultant, secondConsultant));

            // when
            final List<Consultant> result = consultantService.searchConsultants(
                    skillNames, technologyNames, null, null);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).extracting(Consultant::getId)
                    .containsExactly("consultant-123", "consultant-456");
            verify(consultantRepository).searchConsultants(skillNames, technologyNames, null, null);
        }
    }
}
