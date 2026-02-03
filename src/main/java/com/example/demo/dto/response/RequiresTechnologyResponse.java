package com.example.demo.dto.response;

import com.example.demo.model.ProficiencyLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class RequiresTechnologyResponse {

    private final String technologyId;
    private final String technologyName;
    private final ProficiencyLevel minLevel;
    private final Boolean isMandatory;
}
