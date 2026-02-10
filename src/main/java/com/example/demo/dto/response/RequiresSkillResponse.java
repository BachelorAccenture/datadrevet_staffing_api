package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class RequiresSkillResponse {

    private final String skillId;
    private final String skillName;
    private final Integer minYearsOfExperience;
    private final Boolean isMandatory;
}
