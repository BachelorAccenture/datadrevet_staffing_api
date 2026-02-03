package com.example.demo.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(setterPrefix = "with")
public class CompanyResponse {

    private final String id;
    private final String name;
    private final String field;
}
