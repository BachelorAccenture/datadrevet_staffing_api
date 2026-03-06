package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/graph")
public class GraphCredentialsController {

    @Value("${spring.neo4j.uri}")
    private String neo4jUri;

    @Value("${spring.neo4j.authentication.password}")
    private String neo4jPassword;

    @GetMapping("/credentials")
    public Map<String, String> getCredentials() {
        return Map.of(
                "url",      neo4jUri.replace("bolt://neo4j:", "bolt://" + getBoltHost() + ":"),
                "username", "neo4j",
                "password", neo4jPassword
        );
    }

    // Translates the internal Docker hostname "neo4j" to the browser-reachable hostname
    private String getBoltHost() {
        return System.getenv().getOrDefault("BOLT_PUBLIC_HOST", "localhost");
    }
}