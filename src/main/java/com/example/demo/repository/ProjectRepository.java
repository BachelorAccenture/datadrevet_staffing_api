package com.example.demo.repository;

import com.example.demo.model.Project;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends Neo4jRepository<Project, String> {

    Optional<Project> findByName(String name);

    @Query("""
        MATCH (p:Project)-[o:OWNED_BY]->(c:Company)
        WHERE c.id = $companyId
        RETURN p, o, c
        """)
    List<Project> findByCompanyId(@Param("companyId") String companyId);

    @Query("""
        MATCH (p:Project)-[rs:REQUIRES_SKILL]->(s:Skill)
        WHERE s.name IN $skillNames
        RETURN p, collect(rs), collect(s)
        """)
    List<Project> findByRequiredSkillNames(@Param("skillNames") List<String> skillNames);


    boolean existsByName(String name);
}