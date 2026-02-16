package com.example.demo.model.relationship;

import com.example.demo.model.Project;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;
import org.springframework.data.neo4j.core.support.UUIDStringGenerator;

import java.util.Date;

@RelationshipProperties
@Getter
@Setter
public class AssignedTo {

    @Id
    @GeneratedValue(generatorClass = UUIDStringGenerator.class)
    private String id;

    @TargetNode
    private Project project;

    @Property("allocationPercent")
    private Integer allocationPercent;

    @Property("isActive")
    private Boolean isActive;

    @Property("role")
    private String role;

    @Property("startDate")
    private Date startDate;
    @Property("endDate")
    private Date endDate;
}
