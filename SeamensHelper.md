## For å filtrere på søke siden

public List<Consultant> searchConsultants;

    @GetMapping("/search")
    public ResponseEntity<List<ConsultantResponse>> search(
            @RequestParam(required = false) final List<String> skillNames,
            @RequestParam(required = false) final String role,
            @RequestParam(required = false) final Integer minYearsOfExperience) {
        log.info("[ConsultantController] - SEARCH: skills: {}, role: {}, minYears: {}",
                skillNames, role, minYearsOfExperience);
        final List<Consultant> consultants = consultantService.searchConsultants(
                skillNames, role, minYearsOfExperience);
        return ResponseEntity.ok(ConsultantMapper.toResponseList(consultants));
    }
