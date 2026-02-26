package com.example.demo.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Driver;
import org.neo4j.driver.Record;
import org.neo4j.driver.Session;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class GraphRepositoryImpl implements GraphRepository {

    private final Driver neo4jDriver;

    private static final String CONSULTANTS_PROJECTS_GRAPH = """
            MATCH (c:Consultant)-[at:ASSIGNED_TO]->(p:Project)
            RETURN c, at, p
            """;

    @Override
    public List<Record> consultantsProjectsGraph() {
        log.debug("[GraphRepositoryImpl] - FIND_CONSULTANTS_PROJECTS_GRAPH");
        try (final Session session = neo4jDriver.session()) {
            return session.run(CONSULTANTS_PROJECTS_GRAPH).list();
        }
    }
}
