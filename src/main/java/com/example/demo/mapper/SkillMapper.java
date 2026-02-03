package com.example.demo.mapper;

import com.example.demo.dto.request.CreateSkillRequest;
import com.example.demo.dto.response.SkillResponse;
import com.example.demo.model.Skill;

import java.util.Collections;
import java.util.List;

public final class SkillMapper {

    private SkillMapper() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static SkillResponse toResponse(final Skill skill) {
        if (skill == null) {
            return null;
        }
        return SkillResponse.builder()
                .withId(skill.getId())
                .withName(skill.getName())
                .withSynonyms(skill.getSynonyms() != null ? skill.getSynonyms() : Collections.emptyList())
                .build();
    }

    public static List<SkillResponse> toResponseList(final List<Skill> skills) {
        if (skills == null || skills.isEmpty()) {
            return Collections.emptyList();
        }
        return skills.stream()
                .map(SkillMapper::toResponse)
                .toList();
    }

    public static Skill toEntity(final CreateSkillRequest request) {
        if (request == null) {
            return null;
        }
        final Skill skill = new Skill();
        skill.setName(request.name());
        skill.setSynonyms(request.synonyms() != null ? request.synonyms() : Collections.emptyList());
        return skill;
    }
}