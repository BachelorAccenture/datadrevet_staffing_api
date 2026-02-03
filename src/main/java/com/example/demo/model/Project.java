package com.example.demo.model;

import com.example.demo.model.relationship.RequiresSkill;
import com.example.demo.model.relationship.RequiresTechnology;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Node("Project")
@Getter
@Setter
public class Project {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    @Property("name")
    private String name;

    @Property("requirements")
    private List<String> requirements = new ArrayList<>();

    @Property("date")
    private LocalDateTime date;

    @Relationship(type = "OWNED_BY", direction = Relationship.Direction.OUTGOING)
    private Company company;

    @Relationship(type = "REQUIRES_SKILL", direction = Relationship.Direction.OUTGOING)
    private Set<RequiresSkill> requiredSkills = new HashSet<>();

    @Relationship(type = "REQUIRES_TECHNOLOGY", direction = Relationship.Direction.OUTGOING)
    private Set<RequiresTechnology> requiredTechnologies = new HashSet<>();
}