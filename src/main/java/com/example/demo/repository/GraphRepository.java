package com.example.demo.repository;

import org.neo4j.driver.Record;

import java.util.List;


public interface GraphRepository {
    List<Record> consultantsProjectsGraph();
}
