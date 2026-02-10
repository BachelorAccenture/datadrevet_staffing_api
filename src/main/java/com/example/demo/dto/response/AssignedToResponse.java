package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class AssignedToResponse {

    private final String projectId;
    private final String projectName;
    private final String role;
    private final Integer allocationPercent;
    private final Boolean isActive;
}

