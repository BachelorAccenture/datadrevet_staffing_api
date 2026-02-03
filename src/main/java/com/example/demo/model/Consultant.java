package com.example.demo.model;

import com.example.demo.model.relationship.AssignedTo;
import com.example.demo.model.relationship.HasSkill;
import com.example.demo.model.relationship.Knows;
import com.example.demo.model.relationship.WorkedFor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.Relationship;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
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

    @Property("role")
    private String role;

    @Property("yearsOfExperience")
    private Integer yearsOfExperience;

    @Property("availability")
    private Boolean availability;

    @Property("wantsNewProject")
    private Boolean wantsNewProject;

    @Property("openToRelocation")
    private Boolean openToRelocation;

    @Property("openToRemote")
    private Boolean openToRemote;

    @Property("preferredRegions")
    private List<String> preferredRegions = new ArrayList<>();

    @Relationship(type = "HAS_SKILL", direction = Relationship.Direction.OUTGOING)
    private Set<HasSkill> skills = new HashSet<>();

    @Relationship(type = "KNOWS", direction = Relationship.Direction.OUTGOING)
    private Set<Knows> technologies = new HashSet<>();

    @Relationship(type = "ASSIGNED_TO", direction = Relationship.Direction.OUTGOING)
    private Set<AssignedTo> projectAssignments = new HashSet<>();

    @Relationship(type = "WORKED_FOR", direction = Relationship.Direction.OUTGOING)
    private Set<WorkedFor> workHistory = new HashSet<>();
}