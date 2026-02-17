package com.example.demo.controller;

import com.example.demo.config.Neo4jTestContainerConfig;
import com.example.demo.dto.request.CreateCompanyRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.oauth2Login;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * FULL integration test:
 * - Starts Neo4j in Docker (Testcontainers)
 * - Uses real repositories/services
 * - Hits controller through HTTP layer
 * - Mocks Azure login
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CompanyControllerTest extends Neo4jTestContainerConfig {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private Neo4jClient neo4jClient;



    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void cleanDatabase() {
        // Reset Neo4j between tests
        neo4jClient.query("MATCH (n) DETACH DELETE n").run();
    }

    private CreateCompanyRequest createCompanyRequest(String name, String field) {
        return new CreateCompanyRequest(name, field);
    }

    @Test
    void shouldCreateCompany() throws Exception {
        var request = createCompanyRequest("Tesla", "Technology");

        mockMvc.perform(post("/api/v1/companies")
                        .with(oauth2Login()) // 👈 simulate Azure login
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tesla"))
                .andExpect(jsonPath("$.field").value("Technology"));
    }

    @Test
    void shouldGetAllCompanies() throws Exception {
        // create two companies first
        shouldCreateCompanyInternal("Apple", "Technology");
        shouldCreateCompanyInternal("Equinor", "Energy");

        mockMvc.perform(get("/api/v1/companies")
                        .with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldGetCompanyById() throws Exception {
        String id = shouldCreateCompanyInternal("Spotify", "Music");

        mockMvc.perform(get("/api/v1/companies/{id}", id)
                        .with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Spotify"));
    }

    @Test
    void shouldSearchCompanies() throws Exception {
        shouldCreateCompanyInternal("Google", "Technology");
        shouldCreateCompanyInternal("Goldman Sachs", "Finance");

        mockMvc.perform(get("/api/v1/companies/search")
                        .param("query", "Go")
                        .with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void shouldGetCompaniesByField() throws Exception {
        shouldCreateCompanyInternal("Meta", "Technology");
        shouldCreateCompanyInternal("Shell", "Energy");

        mockMvc.perform(get("/api/v1/companies/by-field")
                        .param("field", "Technology")
                        .with(oauth2Login()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void shouldUpdateCompany() throws Exception {
        String id = shouldCreateCompanyInternal("OldName", "Finance");

        var updateRequest = createCompanyRequest("NewName", "Fintech");

        mockMvc.perform(put("/api/v1/companies/{id}", id)
                        .with(oauth2Login())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.field").value("Fintech"));
    }

    @Test
    void shouldDeleteCompany() throws Exception {
        String id = shouldCreateCompanyInternal("DeleteMe", "Test");

        mockMvc.perform(delete("/api/v1/companies/{id}", id)
                        .with(oauth2Login()))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/v1/companies/{id}", id)
                        .with(oauth2Login()))
                .andExpect(status().isNotFound());
    }

    /**
     * Helper method to create companies inside tests.
     */
    private String shouldCreateCompanyInternal(String name, String field) throws Exception {
        var request = createCompanyRequest(name, field);

        var mvcResult = mockMvc.perform(post("/api/v1/companies")
                        .with(oauth2Login())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asText();
    }
}
