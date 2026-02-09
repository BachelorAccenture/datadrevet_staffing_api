package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class KnowsTechnologyResponse {

    private final String technologyId;
    private final String technologyName;
    private final Integer skillYearsOfExperience;
}