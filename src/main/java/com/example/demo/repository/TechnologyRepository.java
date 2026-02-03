package com.example.demo.repository;

import com.example.demo.model.Technology;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TechnologyRepository extends Neo4jRepository<Technology, String> {

    Optional<Technology> findByName(String name);

    Optional<Technology> findByNameIgnoreCase(String name);

    List<Technology> findByNameContainingIgnoreCase(String name);

    @Query("""
        MATCH (t:Technology)
        WHERE t.name = $name OR $name IN t.synonyms
        RETURN t
        """)
    Optional<Technology> findByNameOrSynonym(@Param("name") String name);

    boolean existsByName(String name);
}