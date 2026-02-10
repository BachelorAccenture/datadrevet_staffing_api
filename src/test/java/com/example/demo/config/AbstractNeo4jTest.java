package com.example.demo.config;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.Neo4jContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;


@Testcontainers
@SpringBootTest
public abstract class AbstractNeo4jTest {

    @Container
    protected static final Neo4jContainer<?> neo4jContainer = new Neo4jContainer<>("neo4j:5.26.0")
            .withAdminPassword("testpassword")
            .withReuse(false);

    @DynamicPropertySource
    static void neo4jProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.neo4j.uri", () -> neo4jContainer.getBoltUrl());
        registry.add("spring.neo4j.authentication.username", () -> "neo4j");
        registry.add("spring.neo4j.authentication.password", () -> "testpassword");
    }
}

