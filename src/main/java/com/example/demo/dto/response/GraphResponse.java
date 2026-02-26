package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(setterPrefix = "with")
public class GraphResponse {

    private final List<GraphNodeResponse> nodes;
    private final List<GraphEdgeResponse> edges;
}