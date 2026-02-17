package com.example.demo.model.relationship;

import com.example.demo.model.Project;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Property;
import org.springframework.data.neo4j.core.schema.RelationshipProperties;
import org.springframework.data.neo4j.core.schema.TargetNode;

import java.time.LocalDateTime;
import java.util.Date;

@RelationshipProperties
@Getter
@Setter
public class AssignedTo {

    @Id
    @GeneratedValue
    private Long id;

    @TargetNode
    private Project project;

    @Property("allocationPercent")
    private Integer allocationPercent;

    @Property("isActive")
    private Boolean isActive;

    @Property("role")
    private String role;

    @Property("startDate")
    private LocalDateTime startDate;
    @Property("endDate")
    private LocalDateTime endDate;
}
