package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder(setterPrefix = "with")
public class GraphNodeResponse {

    private final String id;
    private final String label;
    private final String type;
    private final Map<String, Object> properties;
}