package com.example.demo.repository;

import com.example.demo.model.Company;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends Neo4jRepository<Company, String> {

    Optional<Company> findByName(String name);

    Optional<Company> findByNameIgnoreCase(String name);

    List<Company> findByNameContainingIgnoreCase(String name);

    List<Company> findByField(String field);

    boolean existsByName(String name);
}