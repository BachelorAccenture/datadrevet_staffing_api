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
        
        OPTIONAL MATCH (c)-[:HAS_SKILL]->(s:Skill)
        WHERE s.name IN $skillNames
        
        OPTIONAL MATCH (c)-[r:ASSIGNED_TO]->(p:Project)
        WHERE r.role IS NOT NULL
          AND ANY(role IN $roles WHERE toLower(r.role) CONTAINS toLower(role))
        
        OPTIONAL MATCH (c)-[:ASSIGNED_TO]->(:Project)-[:OWNED_BY]->(co:Company)
        WHERE co.name IN $previousCompanies
        
        OPTIONAL MATCH (c)-[dateCheck:ASSIGNED_TO]->(dateProject:Project)
        WHERE dateCheck.isActive = true
          AND $startDate IS NOT NULL
          AND (dateCheck.endDate IS NULL OR dateCheck.endDate > $startDate)
        
        WITH c,
             collect(DISTINCT s)  AS matchedSkills,
             collect(DISTINCT r)  AS matchedRoles,
             collect(DISTINCT co) AS matchedCompanies,
             collect(DISTINCT dateCheck) AS overlappingAssignments
        
        WHERE (
                $availability IS NULL
                OR $availability <> true
                OR c.availability = true
                OR ($startDate IS NOT NULL AND size(overlappingAssignments) = 0)
              )
          AND ($wantsNewProject IS NULL OR c.wantsNewProject = $wantsNewProject)
          AND ($openToRemote IS NULL OR c.openToRemote = $openToRemote)
          AND (
                (size($skillNames)=0 AND size($roles)=0 AND size($previousCompanies)=0)
                OR size(matchedSkills) > 0
                OR size(matchedRoles) > 0
                OR size(matchedCompanies) > 0
              )
        
        WITH c, matchedSkills, matchedRoles, matchedCompanies,
        
             size(matchedSkills) * $skillWeight
             + size(matchedRoles) * $roleWeight
             + size(matchedCompanies) * $companyWeight
             AS totalScore
        
        ORDER BY totalScore DESC
        
        OPTIONAL MATCH (c)-[hs:HAS_SKILL]->(skill:Skill)
        OPTIONAL MATCH (c)-[at:ASSIGNED_TO]->(project:Project)
        
        RETURN c,
               collect(DISTINCT hs),
               collect(DISTINCT skill),
               collect(DISTINCT at),
               collect(DISTINCT project),
               collect(DISTINCT totalScore)
        """)

    List<Consultant> searchConsultants(
            @Param("skillNames") List<String> skillNames,
            @Param("roles") List<String> roles,
            @Param("availability") Boolean availability,
            @Param("wantsNewProject") Boolean wantsNewProject,
            @Param("openToRemote") Boolean openToRemote,
            @Param("previousCompanies") List<String> previousCompanies,
            @Param("startDate") LocalDateTime startDate,
            @Param("skillWeight") int skillWeight,
            @Param("roleWeight") int roleWeight,
            @Param("companyWeight") int companyWeight

    );

    boolean existsByEmail(String email);
}