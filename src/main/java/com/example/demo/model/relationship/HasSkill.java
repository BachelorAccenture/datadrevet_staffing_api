package com.example.demo.model.relationship;

import com.example.demo.model.Skill;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

@RelationshipProperties
@Getter
@Setter
public class HasSkill {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    @TargetNode
    private Skill skill;

    @Property("skillYearsOfExperience")
    private Integer skillYearsOfExperience;
}