package com.example.demo.repository;

import com.example.demo.model.Consultant;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
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
        WHERE ($minYearsOfExperience IS NULL OR c.yearsOfExperience >= $minYearsOfExperience)
          AND ($availability IS NULL
              OR (
                  $startDate IS NOT NULL AND $endDate IS NOT NULL
                  AND NOT EXISTS {
                      MATCH (c)-[at2:ASSIGNED_TO]->(p2:Project)
                      WHERE at2.isActive = true
                        AND (at2.startDate IS NULL OR at2.startDate <= $endDate)
                        AND (at2.endDate IS NULL OR at2.endDate >= $startDate)
                  }
              )
              OR (
                  ($startDate IS NULL OR $endDate IS NULL)
                  AND c.availability = $availability
              )
          )
          AND ($wantsNewProject IS NULL OR c.wantsNewProject = $wantsNewProject)
          AND ($openToRemote IS NULL OR c.openToRemote = $openToRemote)
          AND ($role IS NULL OR $role = '' OR EXISTS {
              MATCH (c)-[at:ASSIGNED_TO]->(p:Project)
              WHERE toLower(at.role) CONTAINS toLower($role)
          })
          AND (size($skillNames) = 0 OR EXISTS {
              MATCH (c)-[:HAS_SKILL]->(s:Skill) WHERE s.name IN $skillNames
          })
          AND (size($previousCompanies) = 0 OR EXISTS {
              MATCH (c)-[:ASSIGNED_TO]->(p:Project)-[:OWNED_BY]->(co:Company)
              WHERE co.name IN $previousCompanies
          })
        OPTIONAL MATCH (c)-[hs:HAS_SKILL]->(skill:Skill)
        OPTIONAL MATCH (c)-[at:ASSIGNED_TO]->(project:Project)
        RETURN c, collect(DISTINCT hs), collect(DISTINCT skill), collect(DISTINCT at), collect(DISTINCT project)
        """)
    List<Consultant> searchConsultants(
            @Param("skillNames") List<String> skillNames,
            @Param("role") String role,
            @Param("minYearsOfExperience") Integer minYearsOfExperience,
            @Param("availability") Boolean availability,
            @Param("wantsNewProject") Boolean wantsNewProject,
            @Param("openToRemote") Boolean openToRemote,
            @Param("previousCompanies") List<String> previousCompanies,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    boolean existsByEmail(String email);
}