package com.example.demo.dto.response;

import lombok.Builder;

@Builder(setterPrefix = "with")
public record KnowsTechnologyResponse(String technologyId, String technologyName, Integer yearsExperience) {

}