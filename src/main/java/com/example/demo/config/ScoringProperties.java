package com.example.demo.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "scoring")

public record ScoringProperties(
        int skillWeight,
        int roleWeight,
        int companyWeight
) {}
