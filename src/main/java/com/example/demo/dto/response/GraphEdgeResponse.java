package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder(setterPrefix = "with")
public class GraphEdgeResponse {

    private final String source;
    private final String target;
    private final String type;
    private final Map<String, Object> properties;
}