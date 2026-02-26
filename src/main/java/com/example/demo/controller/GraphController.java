package com.example.demo.controller;

import com.example.demo.dto.response.GraphResponse;
import com.example.demo.service.GraphService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/graph")
@RequiredArgsConstructor
@Slf4j
public class GraphController {

    private final GraphService graphService;

    @GetMapping
    public ResponseEntity<GraphResponse> getGraph() {
        log.info("[GraphController] - GET_CONSULTANTS_PROJECTS_GRAPH");
        final GraphResponse graphResponse = graphService.getConsultantsProjectsGraph();
        return ResponseEntity.ok(graphResponse);
    }
}