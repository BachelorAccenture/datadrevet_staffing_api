package com.example.demo.controller;

import com.example.demo.config.Neo4jTestContainerConfig;
import com.example.demo.dto.request.CreateConsultantRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

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
class ConsultantControllerTest extends Neo4jTestContainerConfig {

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

    private CreateConsultantRequest createConsultantRequest(String name, String email, Integer yearsOfExperience, Boolean availability, Boolean wantsNewProject, Boolean openToRemote) {
        return new CreateConsultantRequest(name, email, yearsOfExperience, availability, wantsNewProject, openToRemote);
    }

  /*  @Test
    void shouldCreateCompany() throws Exception {
        var request = createCompanyRequest("Tesla", "Technology");

        mockMvc.perform(post("/api/v1/companies")
                        .with(oauth2Login()) // 👈 simulate Azure login
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Tesla"))
                .andExpect(jsonPath("$.field").value("Technology"));
    }*/

}
