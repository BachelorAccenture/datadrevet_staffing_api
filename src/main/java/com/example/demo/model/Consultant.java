package com.example.demo.model;

import com.example.demo.model.relationship.AssignedTo;
import com.example.demo.model.relationship.HasSkill;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.HashSet;
import java.util.Set;

@Node("Consultant")
@Getter
@Setter
public class Consultant {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    @Property("name")
    private String name;

    @Property("email")
    private String email;

    @Property("yearsOfExperience")
    private Integer yearsOfExperience;

    @Property("availability")
    private Boolean availability;

    @Property("wantsNewProject")
    private Boolean wantsNewProject;

    @Property("openToRemote")
    private Boolean openToRemote;

    @Relationship(type = "HAS_SKILL", direction = Relationship.Direction.OUTGOING)
    private Set<HasSkill> skills = new HashSet<>();

    @Relationship(type = "ASSIGNED_TO", direction = Relationship.Direction.OUTGOING)
    private Set<AssignedTo> projectAssignments = new HashSet<>();


}