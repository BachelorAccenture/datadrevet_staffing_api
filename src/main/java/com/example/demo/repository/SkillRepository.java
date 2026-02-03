package com.example.demo.repository;

import com.example.demo.model.Skill;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends Neo4jRepository<Skill, String> {

    Optional<Skill> findByName(String name);

    Optional<Skill> findByNameIgnoreCase(String name);

    List<Skill> findByNameContainingIgnoreCase(String name);

    @Query("""
        MATCH (s:Skill)
        WHERE s.name = $name OR $name IN s.synonyms
        RETURN s
        """)
    Optional<Skill> findByNameOrSynonym(@Param("name") String name);

    boolean existsByName(String name);
}