package com.example.demo.repository;

import com.example.demo.model.Consultant;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ConsultantRepository extends Neo4jRepository<Consultant, String> {

    Optional<Consultant> findByEmail(String email);

    List<Consultant> findByAvailabilityTrue();

    List<Consultant> findByWantsNewProjectTrue();

    @Query("""
        MATCH (c:Consultant)-[hs:HAS_SKILL]->(s:Skill)
        WHERE s.name IN $skillNames
        RETURN c, collect(hs), collect(s)
        """)
    List<Consultant> findBySkillNames(@Param("skillNames") List<String> skillNames);


    @Query("""
        MATCH (c:Consultant)
        WHERE c.availability = true AND c.yearsOfExperience >= $minYears
        RETURN c
        """)
    List<Consultant> findAvailableWithMinExperience(@Param("minYears") Integer minYears);

    @Query("""
        MATCH (c:Consultant)
        WHERE ($role IS NULL OR $role = '' OR toLower(c.role) CONTAINS toLower($role))
          AND ($minYearsOfExperience IS NULL OR c.yearsOfExperience >= $minYearsOfExperience)
          AND (size($skillNames) = 0 OR EXISTS {
              MATCH (c)-[:HAS_SKILL]->(s:Skill) WHERE s.name IN $skillNames
          })
          AND (size($technologyNames) = 0 OR EXISTS {
              MATCH (c)-[:KNOWS]->(t:Technology) WHERE t.name IN $technologyNames
          })
        OPTIONAL MATCH (c)-[hs:HAS_SKILL]->(skill:Skill)
        OPTIONAL MATCH (c)-[k:KNOWS]->(tech:Technology)
        RETURN c, collect(DISTINCT hs), collect(DISTINCT skill), collect(DISTINCT k), collect(DISTINCT tech)
        """)
    List<Consultant> searchConsultants(
            @Param("skillNames") List<String> skillNames,
            @Param("technologyNames") List<String> technologyNames,
            @Param("role") String role,
            @Param("minYearsOfExperience") Integer minYearsOfExperience
    );

    boolean existsByEmail(String email);
}