package com.example.demo.service;

import com.example.demo.dto.response.GraphEdgeResponse;
import com.example.demo.dto.response.GraphNodeResponse;
import com.example.demo.dto.response.GraphResponse;
import com.example.demo.repository.GraphRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Record;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GraphService {

    private static final String NODE_TYPE_CONSULTANT = "Consultant";
    private static final String NODE_TYPE_PROJECT = "Project";

    private final GraphRepository graphRepository;

    public GraphResponse getConsultantsProjectsGraph() {
        log.info("[GraphService] - GET_CONSULTANTS_PROJECTS_GRAPH");

        final Map<String, GraphNodeResponse> nodeMap = new LinkedHashMap<>();
        final List<GraphEdgeResponse> edges = new ArrayList<>();

        final List<Record> records = graphRepository.consultantsProjectsGraph();

        for (final Record record : records) {
            processConsultantNode(record, nodeMap);
            processProjectNode(record, nodeMap);
            processAssignedToRelationship(record, edges, nodeMap);
        }

        log.info("[GraphService] - GET_CONSULTANTS_PROJECTS_GRAPH: nodeCount: {}, edgeCount: {}",
                nodeMap.size(), edges.size());

        return GraphResponse.builder()
                .withNodes(new ArrayList<>(nodeMap.values()))
                .withEdges(edges)
                .build();
    }

    private void processConsultantNode(final Record record, final Map<String, GraphNodeResponse> nodeMap) {
        if (record.get("c").isNull()) {
            return;
        }
        final Node node = record.get("c").asNode();
        final String nodeId = getStringProperty(node, "id");
        if (nodeMap.containsKey(nodeId)) {
            return;
        }

        final Map<String, Object> properties = new HashMap<>();
        properties.put("name", getStringProperty(node, "name"));
        properties.put("email", getStringProperty(node, "email"));

        nodeMap.put(nodeId, GraphNodeResponse.builder()
                .withId(nodeId)
                .withLabel(getStringProperty(node, "name"))
                .withType(NODE_TYPE_CONSULTANT)
                .withProperties(properties)
                .build());
    }

    private void processProjectNode(final Record record, final Map<String, GraphNodeResponse> nodeMap) {
        if (record.get("p").isNull()) {
            return;
        }
        final Node node = record.get("p").asNode();
        final String nodeId = getStringProperty(node, "id");
        if (nodeMap.containsKey(nodeId)) {
            return;
        }

        final Map<String, Object> properties = new HashMap<>();
        properties.put("name", getStringProperty(node, "name"));

        nodeMap.put(nodeId, GraphNodeResponse.builder()
                .withId(nodeId)
                .withLabel(getStringProperty(node, "name"))
                .withType(NODE_TYPE_PROJECT)
                .withProperties(properties)
                .build());
    }

    private void processAssignedToRelationship(final Record record, final List<GraphEdgeResponse> edges,
                                               final Map<String, GraphNodeResponse> nodeMap) {
        if (record.get("at").isNull() || record.get("c").isNull() || record.get("p").isNull()) {
            return;
        }

        final Node consultantNode = record.get("c").asNode();
        final Node projectNode = record.get("p").asNode();
        final String sourceId = getStringProperty(consultantNode, "id");
        final String targetId = getStringProperty(projectNode, "id");

        if (!nodeMap.containsKey(sourceId) || !nodeMap.containsKey(targetId)) {
            return;
        }

        final Relationship rel = record.get("at").asRelationship();
        final Map<String, Object> properties = new HashMap<>();

        if (rel.containsKey("role") && !rel.get("role").isNull()) {
            properties.put("role", rel.get("role").asString());
        }
        if (rel.containsKey("isActive") && !rel.get("isActive").isNull()) {
            properties.put("isActive", rel.get("isActive").asBoolean());
        }
        if (rel.containsKey("allocationPercent") && !rel.get("allocationPercent").isNull()) {
            properties.put("allocationPercent", rel.get("allocationPercent").asInt());
        }

        edges.add(GraphEdgeResponse.builder()
                .withSource(sourceId)
                .withTarget(targetId)
                .withType("ASSIGNED_TO")
                .withProperties(properties)
                .build());
    }

    private String getStringProperty(final Node node, final String key) {
        if (node.containsKey(key) && !node.get(key).isNull()) {
            return node.get(key).asString();
        }
        return "";
    }
}
