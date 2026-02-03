package com.example.demo.service;

import com.example.demo.model.Technology;
import com.example.demo.repository.TechnologyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class TechnologyService {

    private final TechnologyRepository technologyRepository;

    public Technology create(final Technology technology) {
        log.info("[TechnologyService] - CREATE: name: {}", technology.getName());
        return technologyRepository.save(technology);
    }

    public Optional<Technology> findById(final String id) {
        log.debug("[TechnologyService] - FIND_BY_ID: id: {}", id);
        return technologyRepository.findById(id);
    }

    public Optional<Technology> findByName(final String name) {
        log.debug("[TechnologyService] - FIND_BY_NAME: name: {}", name);
        return technologyRepository.findByName(name);
    }

    public Optional<Technology> findByNameOrSynonym(final String name) {
        log.debug("[TechnologyService] - FIND_BY_NAME_OR_SYNONYM: name: {}", name);
        return technologyRepository.findByNameOrSynonym(name);
    }

    public List<Technology> findAll() {
        log.debug("[TechnologyService] - FIND_ALL");
        return technologyRepository.findAll();
    }

    public List<Technology> search(final String query) {
        log.debug("[TechnologyService] - SEARCH: query: {}", query);
        return technologyRepository.findByNameContainingIgnoreCase(query);
    }

    public Technology update(final String id, final Technology updatedTechnology) {
        log.info("[TechnologyService] - UPDATE: id: {}", id);
        final Technology existingTechnology = technologyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Technology not found with id: " + id));

        existingTechnology.setName(updatedTechnology.getName());
        existingTechnology.setSynonyms(updatedTechnology.getSynonyms());

        return technologyRepository.save(existingTechnology);
    }

    public void delete(final String id) {
        log.info("[TechnologyService] - DELETE: id: {}", id);
        technologyRepository.deleteById(id);
    }

    public boolean existsByName(final String name) {
        return technologyRepository.existsByName(name);
    }
}