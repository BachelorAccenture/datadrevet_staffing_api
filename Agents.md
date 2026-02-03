# Agent Instructions

## Project Overview

This is a **data-driven staffing system** for consulting firms, built as a bachelor's thesis project. The system matches consultants to projects based on skills, experience, and availability using a graph database.

---

## Technology Stack & Versions

### Core

| Technology | Version | Notes |
|------------|---------|-------|
| Java | 21 (LTS) | Use modern features: records, pattern matching, sealed classes |
| Spring Boot | 3.5.x | Stable, supported until June 2026 |
| Neo4j | 5.x | Community or Enterprise |
| Spring Data Neo4j | 7.x | Included via Spring Boot starter |
| Maven | 3.9.x | Build tool |

### Dependencies

```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.5.10</version>
</parent>

<properties>
<java.version>21</java.version>
</properties>
```

### Key Starters

- `spring-boot-starter-data-neo4j`
- `spring-boot-starter-web`
- `spring-boot-starter-validation`
- `spring-boot-starter-test`
- `lombok`

---

## Code Formatting

| Rule | Value |
|------|-------|
| Indentation | 4 spaces |
| Line length | Maximum 120 characters |
| Blank lines | Use to separate logical blocks |
| Encoding | UTF-8 |
| IDE | IntelliJ IDEA default code style |

---

## Architecture Rules

### Project Structure

```
staffing-service/
├── src/main/java/com/accenture/staffing/
│   ├── config/
│   ├── controller/
│   ├── dto/
│   ├── exception/
│   ├── mapper/
│   ├── model/
│   ├── repository/
│   ├── service/
│   └── util/
├── src/main/resources/
└── src/test/java/
```

### Package Structure

```
com.accenture.staffing/
├── config/              # Configuration classes
├── controller/          # REST controllers
├── dto/                 # Request/Response objects
│   ├── request/
│   └── response/
├── exception/           # Custom exceptions & handlers
├── mapper/              # DTO ↔ Entity mappers
├── model/               # Neo4j node entities
│   └── relationship/    # Relationship entities
├── repository/          # Neo4j repositories
├── service/             # Business logic
└── util/                # Utility classes
```

---

## Coding Conventions

### General Rules

1. **All method parameters must be `final`**
2. **All variables should be `final` where possible**
3. **Use `@RequiredArgsConstructor`** for dependency injection (Lombok)
4. **Use Java records** for DTOs and immutable data
5. **Use `Optional`** for nullable return types, never return `null`
6. **Validate input** at controller level with `@Valid`
7. **Keep controllers thin** — delegate logic to services
8. **Prefer early returns** — avoid unnecessary `else` statements
9. **Avoid magic numbers/strings** — use constants
10. **Check null/empty** before operations on collections and strings

### Lombok Usage

| Annotation | Usage |
|------------|-------|
| `@RequiredArgsConstructor` | Dependency injection via constructor |
| `@Slf4j` | Logging |
| `@Builder(setterPrefix = "with")` | Complex object creation |
| `@Getter` / `@Setter` | When needed (prefer records for DTOs) |

```java
// ✅ DO: Lombok for DI, final fields
@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultantService {
    private final ConsultantRepository consultantRepository;
    private final ConsultantMapper consultantMapper;
    
    public ConsultantResponse findById(final Long id) {
        final Consultant consultant = consultantRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Consultant", id));
        return consultantMapper.toResponse(consultant);
    }
}

// ❌ DON'T: Manual constructor, missing final
@Service
public class ConsultantService {
    private ConsultantRepository consultantRepository;
    
    public ConsultantService(ConsultantRepository repo) {
        this.consultantRepository = repo;
    }
}
```

### Avoid Comments

Comments are allowed only for:
- Cron expressions
- Regex patterns
- TODOs
- `// given / when / then` separation in tests

### Boolean Wrapping

```java
// ✅ DO: Wrap complex conditions
final boolean isAvailable = consultant.getEndDate() == null 
    || consultant.getEndDate().isAfter(projectStart);
    
if (isAvailable) {
    // ...
}

// ❌ DON'T: Inline complex conditions
if (consultant.getEndDate() == null || consultant.getEndDate().isAfter(projectStart)) {
    // ...
}
```

---

## Naming Conventions (Kodestandard)

> **All names must be in English. Use descriptive names, never abbreviations.**

### Java

| Element | Convention | Example |
|---------|------------|---------|
| Classes & Interfaces | PascalCase | `Consultant`, `MatchingService` |
| Methods | camelCase | `findAvailableConsultants()` |
| Variables | camelCase | `projectStartDate`, `skillLevel` |
| Constants | SCREAMING_SNAKE_CASE | `MAX_SKILL_LEVEL`, `DEFAULT_PAGE_SIZE` |

```java
// ✅ DO: Descriptive English names
public class ConsultantMatchingService {
    private static final int MAX_RESULTS = 100;
    
    private final ConsultantRepository consultantRepository;
    
    public List<Consultant> findAvailableConsultants(LocalDate startDate) {
        // ...
    }
}

// ❌ DON'T: Abbreviations or non-English
public class ConsMtchSvc {           // Abbreviated
    private final ConsultantRepository consRepo;  // Abbreviated
    public List<Consultant> finnLedigeKonsulenter() { }  // Norwegian
}
```

### REST API Endpoints

| Rule | Description |
|------|-------------|
| Case | lowercase only |
| Separator | kebab-case |
| Language | English |
| Nouns | Plural for collections |

```java
// ✅ DO
@GetMapping("/api/v1/consultants")
@GetMapping("/api/v1/consultants/{id}/skill-profiles")
@GetMapping("/api/v1/project-assignments")

// ❌ DON'T
@GetMapping("/api/v1/Consultants")         // PascalCase
@GetMapping("/api/v1/skill_profiles")      // snake_case
@GetMapping("/api/v1/konsulenter")         // Norwegian
```

### Class Naming Patterns

| Type | Pattern | Example |
|------|---------|---------|
| Entity | Singular noun | `Consultant`, `Project`, `Skill` |
| Repository | `[Entity]Repository` | `ConsultantRepository` |
| Service | `[Domain]Service` | `MatchingService`, `ConsultantService` |
| Controller | `[Domain]Controller` | `ConsultantController` |
| DTO Request | `[Action][Entity]Request` (record) | `CreateConsultantRequest` |
| DTO Response | `[Entity]Response` (class + @Builder) | `ConsultantResponse` |
| Mapper | `[Entity]Mapper` | `ConsultantMapper` |
| Exception | `[Description]Exception` | `ResourceNotFoundException` |

### Code Style

```java
// ✅ DO: Records for DTOs
public record CreateConsultantRequest(
    @NotBlank String name,
    @Email String email,
    @NotEmpty Set<String> skills
) {}

// ✅ DO: Return Optional for nullable queries
public Optional<Consultant> findByEmail(final String email);

// ✅ DO: Early returns
public ConsultantResponse getConsultant(final Long id) {
    final Optional<Consultant> consultant = repository.findById(id);
    if (consultant.isEmpty()) {
        throw new ResourceNotFoundException("Consultant", id);
    }
    return mapper.toResponse(consultant.get());
}

// ❌ DON'T: Field injection
@Autowired
private ConsultantRepository repo;

// ❌ DON'T: Return null
public Consultant findById(Long id) { return null; }

// ❌ DON'T: Non-final parameters
public void process(String name) { }  // should be: final String name
```

---

## Neo4j Conventions

### Node Entities

```java
@Node("Consultant")
public class Consultant {
    
    @Id @GeneratedValue
    private Long id;
    
    @Property("name")
    private String name;
    
    // Relationships use specific annotations
    @Relationship(type = "HAS_SKILL", direction = Direction.OUTGOING)
    private Set<HasSkill> skills = new HashSet<>();
}
```

### Relationship Entities

```java
@RelationshipProperties
public class HasSkill {
    
    @Id @GeneratedValue
    private Long id;
    
    @TargetNode
    private Skill skill;
    
    @Property("proficiencyLevel")
    private Integer proficiencyLevel;  // 1-5
    
    @Property("yearsExperience")
    private Integer yearsExperience;
}
```

### Naming Rules

| Element | Convention | Example |
|---------|------------|---------|
| Node labels | PascalCase | `Consultant`, `Project` |
| Relationship types | SCREAMING_SNAKE_CASE | `HAS_SKILL`, `ASSIGNED_TO` |
| Properties | camelCase | `proficiencyLevel`, `startDate` |

### Repository Queries

```java
public interface ConsultantRepository extends Neo4jRepository<Consultant, Long> {
    
    // Derived query
    Optional<Consultant> findByEmail(String email);
    
    // Custom Cypher query
    @Query("""
        MATCH (c:Consultant)-[hs:HAS_SKILL]->(s:Skill)
        WHERE s.name IN $skillNames
        RETURN c, collect(hs), collect(s)
        ORDER BY size(collect(s)) DESC
        LIMIT $limit
        """)
    List<Consultant> findBySkillsIn(
        @Param("skillNames") Set<String> skillNames,
        @Param("limit") int limit
    );
}
```

### Query Best Practices

1. **Use parameterized queries** — never concatenate strings
2. **Return only needed data** — avoid `RETURN *`
3. **Use `OPTIONAL MATCH`** for nullable relationships
4. **Add indexes** for frequently queried properties
5. **Use text blocks** (`"""`) for multi-line Cypher

---

## REST API Conventions

### URL Structure

```
/api/v1/{resource}              # Collection
/api/v1/{resource}/{id}         # Single resource
/api/v1/{resource}/{id}/{sub}   # Sub-resource
```

### HTTP Methods

| Method | Usage | Response Code |
|--------|-------|---------------|
| GET | Retrieve | 200 OK |
| POST | Create | 201 Created |
| PUT | Full update | 200 OK |
| PATCH | Partial update | 200 OK |
| DELETE | Remove | 204 No Content |

### Response Format

```java
// Request DTO — use records (immutable, no builder needed)
public record CreateConsultantRequest(
    @NotBlank String name,
    @Email String email,
    @NotEmpty Set<String> skills
) {}

// Response DTO — use @Builder for mapper compatibility
@Getter
@Builder(setterPrefix = "with")
public class ConsultantResponse {
    private final Long id;
    private final String name;
    private final String email;
    private final Set<SkillResponse> skills;
}

// Error response
@Getter
@Builder(setterPrefix = "with")
public class ErrorResponse {
    private final int status;
    private final String error;
    private final String message;
    private final LocalDateTime timestamp;
}
```

### Controller Example

```java
@RestController
@RequestMapping("/api/v1/consultants")
@RequiredArgsConstructor
@Slf4j
public class ConsultantController {
    
    private final ConsultantService consultantService;
    
    @PostMapping
    public ResponseEntity<ConsultantResponse> create(
            @Valid @RequestBody final CreateConsultantRequest request) {
        log.info("[ConsultantController] - CREATE_REQUEST: email: {}", request.email());
        final ConsultantResponse response = consultantService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ConsultantResponse> getById(@PathVariable final Long id) {
        return consultantService.findById(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new ResourceNotFoundException("Consultant", id));
    }
}
```

---

## Exception Handling

### Custom Exceptions

```java
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String resource, Long id) {
        super("%s not found with id: %d".formatted(resource, id));
    }
}

public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) {
        super(message);
    }
}
```

### Global Handler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(final ResourceNotFoundException ex) {
        log.warn("[ExceptionHandler] - NOT_FOUND: message: {}", ex.getMessage());
        return ResponseEntity.status(404).body(new ErrorResponse(
            404, "Not Found", ex.getMessage(), LocalDateTime.now()
        ));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(final MethodArgumentNotValidException ex) {
        final String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        log.warn("[ExceptionHandler] - VALIDATION_FAILED: errors: {}", message);
        return ResponseEntity.status(400).body(new ErrorResponse(
            400, "Validation Error", message, LocalDateTime.now()
        ));
    }
}
```

---

## Mappers

Use **static mappers** for DTO ↔ Entity conversion.

### Structure

```java
public final class ConsultantMapper {
    
    private ConsultantMapper() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static ConsultantResponse toResponse(final Consultant consultant) {
        if (consultant == null) {
            return null;
        }
        return ConsultantResponse.builder()
            .withId(consultant.getId())
            .withName(consultant.getName())
            .withEmail(consultant.getEmail())
            .withSkills(mapSkills(consultant.getSkills()))
            .build();
    }
    
    public static Consultant toEntity(final CreateConsultantRequest request) {
        if (request == null) {
            return null;
        }
        final Consultant consultant = new Consultant();
        consultant.setName(request.name());
        consultant.setEmail(request.email());
        return consultant;
    }
    
    private static Set<SkillResponse> mapSkills(final Set<HasSkill> skills) {
        if (skills == null || skills.isEmpty()) {
            return Set.of();
        }
        return skills.stream()
            .map(SkillMapper::toResponse)
            .collect(Collectors.toSet());
    }
}
```

### Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Class | `[Entity]Mapper` | `ConsultantMapper`, `ProjectMapper` |
| To response | `toResponse()` | `ConsultantMapper.toResponse(entity)` |
| To entity | `toEntity()` | `ConsultantMapper.toEntity(request)` |
| To list | `toResponseList()` | `ConsultantMapper.toResponseList(entities)` |

### Rules

1. **Private constructor** — mappers are utility classes
2. **Null check first** — return null if input is null
3. **Static methods only** — no instance state
4. **Use `@Builder`** on response DTOs for clean mapping

---

## Testing Rules

### Test Structure

```
src/test/java/
├── integration/         # Full integration tests (@SpringBootTest)
├── repository/          # Repository tests (@DataNeo4jTest)
├── service/             # Service unit tests (Mockito)
└── controller/          # Controller tests (@WebMvcTest)
```

### Frameworks

| Tool | Usage |
|------|-------|
| JUnit 5 | Test framework |
| Mockito | Mocking dependencies |
| Testcontainers | Neo4j integration tests |
| AssertJ | Fluent assertions |

### Conventions

1. **Test naming**: `methodName_condition_expectedResult()` or snake_case `method_name_condition_expected_result()`
2. **Use `given / when / then`** comments to structure tests
3. **Use Testcontainers** for Neo4j integration tests
4. **Mock external dependencies** in unit tests with `@Mock` and `@InjectMocks`
5. **Field injection allowed** in tests with `@Autowired`

```java
@DataNeo4jTest
@Testcontainers
class ConsultantRepositoryTest {
    
    @Container
    static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5")
        .withoutAuthentication();
    
    @Autowired
    private ConsultantRepository repository;
    
    @Test
    void findBySkills_withMatchingSkills_returnsConsultants() {
        // given
        final Consultant consultant = createConsultantWithSkills("Java", "Spring");
        repository.save(consultant);
        
        // when
        final List<Consultant> result = repository.findBySkillsIn(Set.of("Java"), 10);
        
        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(consultant.getName());
    }
}
```

```java
@ExtendWith(MockitoExtension.class)
class ConsultantServiceTest {
    
    @Mock
    private ConsultantRepository consultantRepository;
    
    @InjectMocks
    private ConsultantService consultantService;
    
    @Test
    void findById_withValidId_returnsConsultant() {
        // given
        final Long id = 1L;
        final Consultant consultant = new Consultant();
        when(consultantRepository.findById(id)).thenReturn(Optional.of(consultant));
        
        // when
        final Optional<ConsultantResponse> result = consultantService.findById(id);
        
        // then
        assertThat(result).isPresent();
        verify(consultantRepository).findById(id);
    }
}
```

---

## Logging

### Setup

Use `@Slf4j` from Lombok — never create Logger instances manually.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class ConsultantService {
    // log is available automatically
}
```

### Log Levels

| Level | Usage |
|-------|-------|
| `DEBUG` | Development details, method entry/exit |
| `INFO` | Business events, API calls, successful operations |
| `WARN` | Recoverable issues, deprecated usage |
| `ERROR` | Failures, exceptions, unrecoverable issues |

### Format Template

```java
// Pattern: [ServiceName] - ACTION: key: {}, key: {}

// INFO - successful operations
log.info("[ConsultantService] - CREATED: consultantId: {}, email: {}", id, email);
log.info("[MatchingService] - MATCH_FOUND: projectId: {}, matchCount: {}", projectId, count);

// ERROR - failures
log.error("[ConsultantService] - FETCH_FAILED: consultantId: {}, error: {}", id, ex.getMessage());
log.error("[ProjectService] - ASSIGNMENT_FAILED: projectId: {}, reason: {}", projectId, reason);

// DEBUG - development
log.debug("[ConsultantService] - QUERY: skillNames: {}, limit: {}", skills, limit);
```

### Rules

1. **Use placeholders `{}`** — never string concatenation
2. **Include context** — IDs, counts, relevant identifiers
3. **No sensitive data** — never log passwords, tokens, PII
4. **Prefix with service name** — `[ServiceName]` for filtering
5. **Use consistent action names** — `CREATED`, `UPDATED`, `DELETED`, `FETCH_FAILED`

---

## Configuration

### application.yml Structure

```yaml
spring:
  application:
    name: staffing-service
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: ${NEO4J_PASSWORD}

server:
  port: 8080

logging:
  level:
    org.springframework.data.neo4j: DEBUG
```

### Environment Variables

| Variable | Description | Required |
|----------|-------------|----------|
| `NEO4J_URI` | Neo4j connection URI | Yes |
| `NEO4J_PASSWORD` | Database password | Yes |

---

## Git Conventions

### Branch Naming

- `feature/[ticket]-description` — new features
- `bugfix/[ticket]-description` — bug fixes
- `hotfix/[ticket]-description` — production fixes

### Commit Messages

```
type(scope): description

feat(consultant): add skill matching endpoint
fix(project): correct date validation logic
refactor(repository): optimize Cypher queries
docs(readme): update setup instructions
test(service): add matching service tests
```

---

## Frontend Conventions (if applicable)

| Element | Convention | Example |
|---------|------------|---------|
| CSS classes | kebab-case | `.consultant-card`, `.skill-badge` |
| TypeScript/React | Standard conventions | `ConsultantCard.tsx`, `useConsultants` |
| JSON keys | camelCase | `{ "firstName": "...", "skillLevel": 3 }` |

---

## Do NOT

### Architecture
- ❌ Use field injection (`@Autowired` on fields) — except in tests
- ❌ Return `null` from service methods
- ❌ Put business logic in controllers
- ❌ Catch generic `Exception` — be specific
- ❌ Hardcode configuration values
- ❌ Skip writing tests for new features

### Code Style
- ❌ Omit `final` on parameters and local variables
- ❌ Use magic numbers — define constants
- ❌ Use `var` — prefer explicit types
- ❌ Use `@Data` — prefer `@Getter`/`@Setter` or records
- ❌ Write comments — except cron, regex, TODOs, test sections
- ❌ Use string concatenation in logs — use `{}` placeholders

### Naming
- ❌ Use abbreviations (`consSvc`, `projRepo`, `mgr`)
- ❌ Use Norwegian names (`finnKonsulent`, `prosjektData`)
- ❌ Use snake_case or PascalCase in URLs
- ❌ Use non-descriptive names (`data`, `temp`, `x`)

### Neo4j
- ❌ Use raw Strings for Cypher — use `@Query` annotation
- ❌ Skip parameterized queries — never concatenate strings

### General
- ❌ Ignore validation — always use `@Valid`
- ❌ Log sensitive data — no passwords, tokens, PII