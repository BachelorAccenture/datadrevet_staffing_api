package com.example.demo.service;

import com.example.demo.model.Skill;
import com.example.demo.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkillService {

    private final SkillRepository skillRepository;

    public Skill create(final Skill skill) {
        log.info("[SkillService] - CREATE: name: {}", skill.getName());

        if (skillRepository.existsByName(skill.getName())) {
            throw new IllegalArgumentException("Skill already exists with name: " + skill.getName());
        }

        return skillRepository.save(skill);
    }

    public Optional<Skill> findById(final String id) {
        log.debug("[SkillService] - FIND_BY_ID: id: {}", id);
        return skillRepository.findById(id);
    }

    public List<Skill> findAll() {
        log.debug("[SkillService] - FIND_ALL");
        return skillRepository.findAll();
    }

    public List<Skill> search(final String query) {
        log.debug("[SkillService] - SEARCH: query: {}", query);
        return skillRepository.findByNameContainingIgnoreCase(query);
    }

    public Skill update(final String id, final Skill updatedSkill) {
        log.info("[SkillService] - UPDATE: id: {}", id);
        final Skill existingSkill = skillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found with id: " + id));

        existingSkill.setName(updatedSkill.getName());
        existingSkill.setSynonyms(updatedSkill.getSynonyms());

        return skillRepository.save(existingSkill);
    }

    public void delete(final String id) {
        log.info("[SkillService] - DELETE: id: {}", id);
        skillRepository.deleteById(id);
    }
}