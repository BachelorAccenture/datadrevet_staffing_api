package com.example.demo.controller;

import com.example.demo.config.Neo4jTestContainerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oidcLogin;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthTestControllerTest extends Neo4jTestContainerConfig {

    @Autowired
    MockMvc mockMvc;

    @Test
    void shouldReturnAuthenticatedUser() throws Exception {

        mockMvc.perform(get("/me")
                        .with(oidcLogin().idToken(token -> token
                                .claim("name", "Hugo")
                                .claim("email", "hugo@test.no")
                        ))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Hugo"))
                .andExpect(jsonPath("$.email").value("hugo@test.no"));
    }
}
