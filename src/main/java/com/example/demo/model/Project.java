package com.example.demo.model;

import com.example.demo.model.relationship.RequiresSkill;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

    // Store roles as parallel lists since Neo4j doesn't support Map properties
    @Property("roleNames")
    private List<String> roleNames = new ArrayList<>();

    @Property("roleCounts")
    private List<Integer> roleCounts = new ArrayList<>();

    @Relationship(type = "OWNED_BY", direction = Relationship.Direction.OUTGOING)
    private Company company;

    @Relationship(type = "REQUIRES_SKILL", direction = Relationship.Direction.OUTGOING)
    private Set<RequiresSkill> requiredSkills = new HashSet<>();

    // Convenience methods for working with roles as a Map
    public Map<String, Integer> getRoles() {
        Map<String, Integer> roles = new HashMap<>();
        for (int i = 0; i < roleNames.size() && i < roleCounts.size(); i++) {
            roles.put(roleNames.get(i), roleCounts.get(i));
        }
        return roles;
    }

    public void setRoles(Map<String, Integer> roles) {
        this.roleNames = new ArrayList<>(roles.keySet());
        this.roleCounts = new ArrayList<>(roles.values());
    }
}