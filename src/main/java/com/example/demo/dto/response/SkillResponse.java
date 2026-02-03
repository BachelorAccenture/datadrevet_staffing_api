package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder(setterPrefix = "with")
public class SkillResponse {

    private final String id;
    private final String name;
    private final List<String> synonyms;
}
