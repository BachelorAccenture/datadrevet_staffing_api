package com.example.demo.mapper;

import com.example.demo.dto.request.CreateTechnologyRequest;
import com.example.demo.dto.response.TechnologyResponse;
import com.example.demo.model.Technology;

import java.util.Collections;
import java.util.List;

public final class TechnologyMapper {

    private TechnologyMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static TechnologyResponse toResponse(final Technology technology) {
        if (technology == null) {
            return null;
        }
        return TechnologyResponse.builder()
                .withId(technology.getId())
                .withName(technology.getName())
                .withSynonyms(technology.getSynonyms() != null ? technology.getSynonyms() : Collections.emptyList())
                .build();
    }

    public static List<TechnologyResponse> toResponseList(final List<Technology> technologies) {
        if (technologies == null || technologies.isEmpty()) {
            return Collections.emptyList();
        }
        return technologies.stream()
                .map(TechnologyMapper::toResponse)
                .toList();
    }

    public static Technology toEntity(final CreateTechnologyRequest request) {
        if (request == null) {
            return null;
        }
        final Technology technology = new Technology();
        technology.setName(request.name());
        technology.setSynonyms(request.synonyms() != null ? request.synonyms() : Collections.emptyList());
        return technology;
    }
}