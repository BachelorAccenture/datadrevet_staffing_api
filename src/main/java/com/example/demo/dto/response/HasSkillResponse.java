package com.example.demo.dto.response;

import com.example.demo.model.ProficiencyLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class HasSkillResponse {

    private final String skillId;
    private final String skillName;
    private final ProficiencyLevel level;
}