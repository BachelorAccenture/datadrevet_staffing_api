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
├── model/               # Neo4j node entities
│   └── relationship/    # Relationship entities
├── repository/          # Neo4j repositories
├── service/             # Business logic
│   └── impl/            # Service implementations (optional)
└── util/                # Utility classes
```

---

## Coding Conventions

### General Rules

1. **Use constructor injection** — never field injection with `@Autowired`
2. **Use Java records** for DTOs and immutable data
3. **Use `Optional`** for nullable return types, never return `null`
4. **Validate input** at controller level with `@Valid`
5. **Keep controllers thin** — delegate logic to services
6. **Use Lombok sparingly** — prefer records; use `@Slf4j` for logging

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
| DTO Request | `[Action][Entity]Request` | `CreateConsultantRequest` |
| DTO Response | `[Entity]Response` | `ConsultantResponse` |
| Exception | `[Description]Exception` | `ResourceNotFoundException` |

### Code Style

```java
// ✅ DO: Constructor injection with final fields
@Service
public class ConsultantService {
    private final ConsultantRepository consultantRepository;
    
    public ConsultantService(ConsultantRepository consultantRepository) {
        this.consultantRepository = consultantRepository;
    }
}

// ✅ DO: Use records for DTOs
public record CreateConsultantRequest(
    @NotBlank String name,
    @Email String email,
    @NotEmpty Set<String> skills
) {}

// ✅ DO: Return Optional for nullable queries
public Optional<Consultant> findByEmail(String email);

// ❌ DON'T: Field injection
@Autowired
private ConsultantRepository repo;

// ❌ DON'T: Return null
public Consultant findById(Long id) { return null; }
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
// Success response
public record ApiResponse<T>(
    boolean success,
    T data,
    String message
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }
}

// Error response
public record ErrorResponse(
    int status,
    String error,
    String message,
    LocalDateTime timestamp
) {}
```

### Controller Example

```java
@RestController
@RequestMapping("/api/v1/consultants")
public class ConsultantController {
    
    private final ConsultantService consultantService;
    
    public ConsultantController(ConsultantService consultantService) {
        this.consultantService = consultantService;
    }
    
    @PostMapping
    public ResponseEntity<ConsultantResponse> create(
            @Valid @RequestBody CreateConsultantRequest request) {
        var consultant = consultantService.create(request);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(consultant);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ConsultantResponse> getById(@PathVariable Long id) {
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
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(404).body(new ErrorResponse(
            404, "Not Found", ex.getMessage(), LocalDateTime.now()
        ));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
            .map(e -> e.getField() + ": " + e.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return ResponseEntity.status(400).body(new ErrorResponse(
            400, "Validation Error", message, LocalDateTime.now()
        ));
    }
}
```

---

## Testing Rules

### Test Structure

```
src/test/java/
├── integration/         # Full integration tests
├── repository/          # Repository tests with @DataNeo4jTest
├── service/             # Service unit tests
└── controller/          # Controller tests with @WebMvcTest
```

### Conventions

1. **Use descriptive test names**: `shouldReturnConsultant_whenValidIdProvided()`
2. **Use Testcontainers** for Neo4j integration tests
3. **Mock external dependencies** in unit tests
4. **Follow AAA pattern**: Arrange, Act, Assert

```java
@DataNeo4jTest
@Testcontainers
class ConsultantRepositoryTest {
    
    @Container
    static Neo4jContainer<?> neo4j = new Neo4jContainer<>("neo4j:5")
        .withoutAuthentication();
    
    @Test
    void shouldFindConsultantsBySkills() {
        // Arrange
        var consultant = createConsultantWithSkills("Java", "Spring");
        
        // Act
        var result = repository.findBySkillsIn(Set.of("Java"), 10);
        
        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo(consultant.getName());
    }
}
```

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
- ❌ Use field injection (`@Autowired` on fields)
- ❌ Return `null` from service methods
- ❌ Put business logic in controllers
- ❌ Catch generic `Exception` — be specific
- ❌ Hardcode configuration values
- ❌ Skip writing tests for new features

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
- ❌ Use `var` when type is not obvious from context