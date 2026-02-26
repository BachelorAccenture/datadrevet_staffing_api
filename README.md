# Data-Driven Staffing API Documentation

**Version:** 2.1
**Base URL:** `http://localhost:8080/api/v1`
**Content-Type:** `application/json`
**Spring Boot:** 3.5.11
**Java:** 21

---

## Table of Contents

1. [Overview](#overview)
2. [Getting Started](#getting-started)
3. [Error Handling](#error-handling)
4. [Skills](#skills)
5. [Companies](#companies)
6. [Consultants](#consultants)
7. [Projects](#projects)
8. [Data Model](#data-model)
9. [Endpoint Summary](#endpoint-summary)

---

## Overview

The Data-Driven Staffing API is a RESTful service for managing consultants, their skills, companies, and project assignments in a consulting firm. Built on **Neo4j** (graph database) and **Spring Boot**, it enables efficient matching of consultants to projects based on skills, experience, and availability.

### Technology Stack

- **Spring Boot 3.5.11** - Application framework
- **Java 21** - Programming language
- **Neo4j 5** - Graph database
- **Spring Data Neo4j 7.x** - Database integration
- **Spring Security** - Security framework
- **OAuth2 Client** - Authentication support
- **Testcontainers** - Integration testing
- **Lombok** - Code generation

### Key Concepts

- **Skills** represent competencies such as "Java", "React", or "DevOps". Each skill can have synonyms (e.g., "JS" for "JavaScript").
- **Companies** are client organizations that own projects (e.g., "Equinor", "DNB").
- **Consultants** are people with skills who are assigned to projects. Each consultant tracks availability, experience, and preferred work setup.
- **Projects** are client engagements owned by a company, with required skills and defined roles.
- **Relationships** connect these entities: Consultants have skills (`HAS_SKILL`), are assigned to projects (`ASSIGNED_TO`), and projects require skills (`REQUIRES_SKILL`) and belong to companies (`OWNED_BY`).

### ID Format

All entities use **UUID strings** as their identifier (e.g., `"550e8400-e29b-41d4-a716-446655440001"`).

---

## Getting Started

### Prerequisites

- Java 21
- Docker (for Neo4j)
- Maven 3.9+

### 1. Start Neo4j

```bash
docker-compose up -d
```

This starts Neo4j on `bolt://localhost:7687` with credentials `neo4j/password`.

### 2. Run the Application

```bash
./mvnw spring-boot:run
```

The application starts on `http://localhost:8080`.

### 3. Configuration

The application can be configured via `src/main/resources/application.yml`:

```yaml
spring:
  neo4j:
    uri: bolt://localhost:7687
    authentication:
      username: neo4j
      password: password

scoring:
  skill-weight: 10    # Weight for skill matches in search
  role-weight: 5      # Weight for role matches in search
  company-weight: 5   # Weight for company matches in search
```

### 4. Verify

```bash
curl http://localhost:8080/api/v1/skills
```

You should see a JSON array of skills loaded from the sample data.

---

## Error Handling

All errors return a consistent JSON structure:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Consultant not found with id: 550e8400-...",
  "timestamp": "2026-02-16T10:30:00"
}
```

### Status Codes

| Code | Meaning |
|------|---------|
| `200 OK` | Request succeeded |
| `201 Created` | Resource created |
| `204 No Content` | Resource deleted |
| `400 Bad Request` | Validation error or invalid input |
| `404 Not Found` | Resource not found |
| `500 Internal Server Error` | Unexpected server error |

---

## Skills

Skills represent competencies that consultants can have and projects can require.

### Create Skill

**`POST /api/v1/skills`**

Creates a new skill with optional synonyms. Synonyms allow flexible matching (e.g., searching "JS" can find "JavaScript").

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Skill name |
| `synonyms` | string[] | No | Alternative names for the skill |

**Example:**

```bash
curl -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Project Management",
    "synonyms": ["PM", "Prosjektledelse"]
  }'
```

**Response `201 Created`:**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Project Management",
  "synonyms": ["PM", "Prosjektledelse"]
}
```

### Get All Skills

**`GET /api/v1/skills`**

Returns all skills in the system.

```bash
curl http://localhost:8080/api/v1/skills
```

**Response `200 OK`:**

```json
[
  {
    "id": "550e8400-...",
    "name": "Java",
    "synonyms": ["Java SE", "Java EE", "JDK"]
  },
  {
    "id": "550e8401-...",
    "name": "React",
    "synonyms": ["ReactJS", "React.js"]
  }
]
```

### Get Skill by ID

**`GET /api/v1/skills/{id}`**

```bash
curl http://localhost:8080/api/v1/skills/550e8400-e29b-41d4-a716-446655440001
```

Returns `404` if the skill does not exist.

### Search Skills

**`GET /api/v1/skills/search?query={query}`**

Case-insensitive partial match on skill name.

```bash
curl "http://localhost:8080/api/v1/skills/search?query=java"
```

This returns all skills whose name contains "java" (e.g., "Java", "JavaScript").

### Update Skill

**`PUT /api/v1/skills/{id}`**

Replaces the skill's name and synonyms.

```bash
curl -X PUT http://localhost:8080/api/v1/skills/550e8400-... \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Java",
    "synonyms": ["Java SE", "Java EE", "JDK", "OpenJDK"]
  }'
```

### Delete Skill

**`DELETE /api/v1/skills/{id}`**

```bash
curl -X DELETE http://localhost:8080/api/v1/skills/550e8400-...
```

**Response:** `204 No Content`

---

## Companies

Companies represent client organizations that own projects.

### Create Company

**`POST /api/v1/companies`**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Company name |
| `field` | string | No | Industry/sector (e.g., "Energy", "Finance") |

```bash
curl -X POST http://localhost:8080/api/v1/companies \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Equinor",
    "field": "Energy"
  }'
```

**Response `201 Created`:**

```json
{
  "id": "770e8400-...",
  "name": "Equinor",
  "field": "Energy"
}
```

### Get All Companies

**`GET /api/v1/companies`**

### Get Company by ID

**`GET /api/v1/companies/{id}`**

### Search Companies

**`GET /api/v1/companies/search?query={query}`**

Case-insensitive partial match on company name.

```bash
curl "http://localhost:8080/api/v1/companies/search?query=equi"
```

### Get Companies by Field

**`GET /api/v1/companies/by-field?field={field}`**

Returns all companies in a given industry/sector.

```bash
curl "http://localhost:8080/api/v1/companies/by-field?field=IT-konsulent"
```

### Update Company

**`PUT /api/v1/companies/{id}`**

```bash
curl -X PUT http://localhost:8080/api/v1/companies/770e8400-... \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Equinor ASA",
    "field": "Energi og Olje"
  }'
```

### Delete Company

**`DELETE /api/v1/companies/{id}`**

**Response:** `204 No Content`

---

## Consultants

Consultants are the core entities: people with skills who get assigned to projects.

### Create Consultant

**`POST /api/v1/consultants`**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Full name |
| `email` | string | Yes | Valid email address |
| `yearsOfExperience` | integer | No | Total years of professional experience |
| `availability` | boolean | No | Currently available for new assignments (default: false) |
| `wantsNewProject` | boolean | No | Actively seeking a new project (default: false) |
| `openToRelocation` | boolean | No | Willing to relocate (default: false) |
| `openToRemote` | boolean | No | Open to remote work (default: false) |
| `preferredRegions` | string[] | No | Preferred work locations (e.g., ["Oslo", "Bergen"]) |

**Example:**

```bash
curl -X POST http://localhost:8080/api/v1/consultants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ola Nordmann",
    "email": "ola.nordmann@example.com",
    "yearsOfExperience": 8,
    "availability": true,
    "wantsNewProject": true,
    "openToRelocation": false,
    "openToRemote": true,
    "preferredRegions": ["Oslo", "Bergen"]
  }'
```

**Response `201 Created`:**

```json
{
  "id": "880e8400-...",
  "name": "Ola Nordmann",
  "email": "ola.nordmann@example.com",
  "yearsOfExperience": 8,
  "availability": true,
  "wantsNewProject": true,
  "openToRelocation": false,
  "openToRemote": true,
  "preferredRegions": ["Oslo", "Bergen"],
  "skills": [],
  "projectAssignments": []
}
```

### Get All Consultants

**`GET /api/v1/consultants`**

Returns all consultants with their skills and project assignments.

### Get Consultant by ID

**`GET /api/v1/consultants/{id}`**

**Response `200 OK`:**

```json
{
  "id": "880e8400-...",
  "name": "Ola Nordmann",
  "email": "ola.nordmann@example.com",
  "yearsOfExperience": 8,
  "availability": true,
  "wantsNewProject": true,
  "openToRelocation": false,
  "openToRemote": true,
  "preferredRegions": ["Oslo", "Bergen"],
  "skills": [
    {
      "skillId": "550e8400-...",
      "skillName": "Java",
      "skillYearsOfExperience": 8
    },
    {
      "skillId": "550e8401-...",
      "skillName": "Spring Framework",
      "skillYearsOfExperience": 8
    }
  ],
  "projectAssignments": [
    {
      "projectId": "990e8400-...",
      "projectName": "Modernisering av nettbank",
      "role": "Senior Backend Developer",
      "allocationPercent": null,
      "isActive": false
    }
  ]
}
```

### Get Consultant by Email

**`GET /api/v1/consultants/by-email?email={email}`**

```bash
curl "http://localhost:8080/api/v1/consultants/by-email?email=ola.nordmann@example.com"
```

### Get Available Consultants

**`GET /api/v1/consultants/available`**

Returns all consultants where `availability = true`.

### Get Consultants Wanting New Project

**`GET /api/v1/consultants/wanting-new-project`**

Returns all consultants where `wantsNewProject = true`.

### Get Consultants by Skills

**`GET /api/v1/consultants/by-skills?skillNames={skill1}&skillNames={skill2}`**

Returns consultants that have any of the specified skills.

```bash
curl "http://localhost:8080/api/v1/consultants/by-skills?skillNames=Java&skillNames=React"
```

### Get Available Consultants with Minimum Experience

**`GET /api/v1/consultants/available-with-experience?minYears={years}`**

Returns available consultants with at least the given years of experience.

```bash
curl "http://localhost:8080/api/v1/consultants/available-with-experience?minYears=5"
```

### Search Consultants (Advanced)

**`GET /api/v1/consultants/search`**

This is the most powerful endpoint. It combines multiple filters in a single query with weighted scoring. All parameters are optional; only the ones you provide are applied.

**Query Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `skillNames` | string[] | Filter by skill names (consultant must have at least one). Scored with skill-weight (default: 10) |
| `roles` | string[] | Filter by role titles (partial match, case-insensitive, matches roles on project assignments). Scored with role-weight (default: 5) |
| `availability` | boolean | Filter by availability status |
| `wantsNewProject` | boolean | Filter by "wants new project" status |
| `openToRemote` | boolean | Filter by remote work preference |
| `previousCompanies` | string[] | Filter by companies the consultant has worked with (via project assignments). Scored with company-weight (default: 5) |
| `startDate` | string | ISO 8601 date-time (e.g., "2026-03-01T09:00:00"). Returns only consultants available at this date (excludes those with active assignments ending after this date) |

**Note:** Results are ranked by a weighted score: `(matched skills × 10) + (matched roles × 5) + (matched companies × 5)`. These weights can be configured in `application.yml`.

**Example: Find available Java developers open to remote work:**

```bash
curl "http://localhost:8080/api/v1/consultants/search?skillNames=Java&availability=true&openToRemote=true"
```

**Example: Find consultants with DNB experience:**

```bash
curl "http://localhost:8080/api/v1/consultants/search?previousCompanies=DNB"
```

**Example: Find available Tech Lead consultants with React and Node.js skills:**

```bash
curl "http://localhost:8080/api/v1/consultants/search?roles=Tech%20Lead&skillNames=React&skillNames=Node.js&availability=true"
```

**Response `200 OK`:** Returns the same consultant structure as Get Consultant by ID, including all skills and project assignments for each matched consultant.

### Update Consultant

**`PUT /api/v1/consultants/{id}`**

Updates the consultant's basic information. Does not affect skills or project assignments.

```bash
curl -X PUT http://localhost:8080/api/v1/consultants/880e8400-... \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ola Nordmann",
    "email": "ola.nordmann@example.com",
    "yearsOfExperience": 9,
    "availability": false,
    "wantsNewProject": false,
    "openToRelocation": false,
    "openToRemote": true,
    "preferredRegions": ["Oslo"]
  }'
```

### Delete Consultant

**`DELETE /api/v1/consultants/{id}`**

**Response:** `204 No Content`

### Add Skill to Consultant

**`POST /api/v1/consultants/{id}/skills`**

Creates a `HAS_SKILL` relationship between the consultant and an existing skill.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `skillId` | string | Yes | UUID of the skill to add |
| `skillYearsOfExperience` | integer | No | Years of experience with this specific skill |

**Example:**

```bash
curl -X POST http://localhost:8080/api/v1/consultants/880e8400-.../skills \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "550e8400-e29b-41d4-a716-446655440001",
    "skillYearsOfExperience": 5
  }'
```

**Response `200 OK`:** Returns the full consultant object with the newly added skill.

### Assign Consultant to Project

**`POST /api/v1/consultants/{id}/projects`**

Creates an `ASSIGNED_TO` relationship between the consultant and an existing project.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `projectId` | string | Yes | UUID of the project to assign |
| `role` | string | No | Consultant's role on the project (e.g., "Tech Lead", "Senior Backend Developer") |
| `allocationPercent` | integer | No | Percentage of time allocated (0-100) |
| `isActive` | boolean | No | Whether this is a current assignment (default: false) |
| `startDate` | string | No | Assignment start date (ISO 8601 format) |
| `endDate` | string | No | Assignment end date (ISO 8601 format) |

**Example:**

```bash
curl -X POST http://localhost:8080/api/v1/consultants/880e8400-.../projects \
  -H "Content-Type: application/json" \
  -d '{
    "projectId": "990e8400-e29b-41d4-a716-446655440001",
    "role": "Senior Backend Developer",
    "allocationPercent": 100,
    "isActive": true,
    "startDate": "2026-03-01T09:00:00",
    "endDate": "2026-09-01T17:00:00"
  }'
```

**Response `200 OK`:** Returns the full consultant object with the newly added project assignment.

### Deactivate Project Assignment

**`PATCH /api/v1/consultants/{id}/projects/{projectId}/deactivate`**

Sets `isActive = false` for a consultant's project assignment.

```bash
curl -X PATCH http://localhost:8080/api/v1/consultants/880e8400-.../projects/990e8400-.../deactivate
```

**Response `200 OK`:** Returns the full consultant object with the updated assignment.

### Remove Project Assignment

**`DELETE /api/v1/consultants/{id}/projects/{projectId}`**

Completely removes the `ASSIGNED_TO` relationship between a consultant and a project.

```bash
curl -X DELETE http://localhost:8080/api/v1/consultants/880e8400-.../projects/990e8400-...
```

**Response `200 OK`:** Returns the full consultant object without the removed assignment.

---

## Projects

Projects represent client engagements that require specific skills and roles.

### Create Project

**`POST /api/v1/projects`**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Project name |
| `requirements` | string[] | No | List of requirement descriptions |
| `startDate` | string | No | Project start date in ISO 8601 format (e.g., `"2026-03-01T09:00:00"`) |
| `endDate` | string | No | Project end date in ISO 8601 format (e.g., `"2026-12-31T17:00:00"`) |
| `companyId` | string | No | UUID of the company to assign. If provided, the project is immediately linked to this company |

**Example:**

```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Digital Transformation Platform",
    "requirements": ["Cloud migration", "API development", "Security compliance"],
    "startDate": "2026-03-01T09:00:00",
    "endDate": "2026-12-31T17:00:00",
    "companyId": "770e8400-e29b-41d4-a716-446655440001"
  }'
```

**Response `201 Created`:**

```json
{
  "id": "990e8400-...",
  "name": "Digital Transformation Platform",
  "requirements": ["Cloud migration", "API development", "Security compliance"],
  "startDate": "2026-03-01T09:00:00",
  "endDate": "2026-12-31T17:00:00",
  "company": {
    "id": "770e8400-...",
    "name": "Equinor",
    "field": "Energy"
  },
  "requiredSkills": [],
  "roles": {}
}
```

### Get All Projects

**`GET /api/v1/projects`**

### Get Project by ID

**`GET /api/v1/projects/{id}`**

**Response `200 OK`:**

```json
{
  "id": "990e8400-...",
  "name": "Modernisering av nettbank",
  "requirements": ["Bygge ny nettbanklosning", "Implementere sanntids transaksjoner"],
  "startDate": "2026-02-16T09:00:00",
  "endDate": "2026-08-31T17:00:00",
  "company": {
    "id": "770e8400-...",
    "name": "DNB",
    "field": "Bank og Finans"
  },
  "requiredSkills": [
    {
      "skillId": "550e8400-...",
      "skillName": "Java",
      "minYearsOfExperience": 5,
      "isMandatory": true
    },
    {
      "skillId": "550e8401-...",
      "skillName": "React",
      "minYearsOfExperience": 3,
      "isMandatory": true
    }
  ],
  "roles": {
    "Tech Lead": 1,
    "Senior Backend Developer": 2,
    "Senior Frontend Developer": 1,
    "DevOps Engineer": 1
  }
}
```

### Get Project by Name

**`GET /api/v1/projects/by-name?name={name}`**

```bash
curl "http://localhost:8080/api/v1/projects/by-name?name=Modernisering%20av%20nettbank"
```

### Get Projects by Company

**`GET /api/v1/projects/by-company/{companyId}`**

Returns all projects owned by the specified company.

```bash
curl http://localhost:8080/api/v1/projects/by-company/770e8400-...
```

### Get Projects by Required Skills

**`GET /api/v1/projects/by-required-skills?skillNames={skill1}&skillNames={skill2}`**

Returns projects that require any of the specified skills.

```bash
curl "http://localhost:8080/api/v1/projects/by-required-skills?skillNames=Java&skillNames=Kubernetes"
```

### Update Project

**`PUT /api/v1/projects/{id}`**

Updates project name, requirements, start date, and end date. Does not modify company assignment or required skills.

```bash
curl -X PUT http://localhost:8080/api/v1/projects/990e8400-... \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Digital Transformation Platform v2",
    "requirements": ["Cloud migration", "API development", "CI/CD"],
    "startDate": "2026-04-01T09:00:00",
    "endDate": "2027-01-31T17:00:00"
  }'
```

### Delete Project

**`DELETE /api/v1/projects/{id}`**

**Response:** `204 No Content`

### Assign Company to Project

**`POST /api/v1/projects/{id}/company/{companyId}`**

Links a project to a company (creates the `OWNED_BY` relationship).

```bash
curl -X POST http://localhost:8080/api/v1/projects/990e8400-.../company/770e8400-...
```

### Add Required Skill to Project

**`POST /api/v1/projects/{id}/required-skills`**

Adds a skill requirement (`REQUIRES_SKILL` relationship) to a project.

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `skillId` | string | Yes | UUID of the skill |
| `minYearsOfExperience` | integer | Yes | Minimum years of experience needed (must be 0 or greater) |
| `isMandatory` | boolean | No | Whether this skill is mandatory (default: false) |

**Example:**

```bash
curl -X POST http://localhost:8080/api/v1/projects/990e8400-.../required-skills \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "550e8400-e29b-41d4-a716-446655440001",
    "minYearsOfExperience": 3,
    "isMandatory": true
  }'
```

**Response `200 OK`:** Returns the full project object with the newly added required skill.

---

## Data Model

The graph database uses the following structure:

```
(Consultant)-[:HAS_SKILL {skillYearsOfExperience}]->(Skill)
(Consultant)-[:ASSIGNED_TO {role, isActive, allocationPercent}]->(Project)
(Project)-[:OWNED_BY]->(Company)
(Project)-[:REQUIRES_SKILL {minYearsOfExperience, isMandatory}]->(Skill)
```

### Relationship Properties

**HAS_SKILL** (Consultant to Skill):

| Property | Type | Description |
|----------|------|-------------|
| `skillYearsOfExperience` | integer | Years of experience the consultant has with this skill |

**ASSIGNED_TO** (Consultant to Project):

| Property | Type | Description |
|----------|------|-------------|
| `role` | string | The consultant's role on this project (e.g., "Tech Lead") |
| `isActive` | boolean | Whether this is a current (true) or past (false) assignment |
| `allocationPercent` | integer | Percentage of time allocated (e.g., 100) |
| `startDate` | date | Assignment start date |
| `endDate` | date | Assignment end date |

**REQUIRES_SKILL** (Project to Skill):

| Property | Type | Description |
|----------|------|-------------|
| `minYearsOfExperience` | integer | Minimum years needed for this skill |
| `isMandatory` | boolean | Whether this skill is mandatory for the project |

---

## Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| | **Skills** | |
| POST | `/api/v1/skills` | Create skill |
| GET | `/api/v1/skills` | Get all skills |
| GET | `/api/v1/skills/{id}` | Get skill by ID |
| GET | `/api/v1/skills/search?query=` | Search skills by name |
| PUT | `/api/v1/skills/{id}` | Update skill |
| DELETE | `/api/v1/skills/{id}` | Delete skill |
| | **Companies** | |
| POST | `/api/v1/companies` | Create company |
| GET | `/api/v1/companies` | Get all companies |
| GET | `/api/v1/companies/{id}` | Get company by ID |
| GET | `/api/v1/companies/search?query=` | Search companies by name |
| GET | `/api/v1/companies/by-field?field=` | Get companies by industry |
| PUT | `/api/v1/companies/{id}` | Update company |
| DELETE | `/api/v1/companies/{id}` | Delete company |
| | **Consultants** | |
| POST | `/api/v1/consultants` | Create consultant |
| GET | `/api/v1/consultants` | Get all consultants |
| GET | `/api/v1/consultants/{id}` | Get consultant by ID |
| GET | `/api/v1/consultants/by-email?email=` | Get consultant by email |
| GET | `/api/v1/consultants/search` | Advanced multi-filter search |
| GET | `/api/v1/consultants/available` | Get available consultants |
| GET | `/api/v1/consultants/wanting-new-project` | Get consultants seeking projects |
| GET | `/api/v1/consultants/by-skills?skillNames=` | Get consultants by skills |
| GET | `/api/v1/consultants/available-with-experience?minYears=` | Get available with min experience |
| PUT | `/api/v1/consultants/{id}` | Update consultant |
| DELETE | `/api/v1/consultants/{id}` | Delete consultant |
| POST | `/api/v1/consultants/{id}/skills` | Add skill to consultant |
| POST | `/api/v1/consultants/{id}/projects` | Assign consultant to project |
| PATCH | `/api/v1/consultants/{id}/projects/{projectId}/deactivate` | Deactivate project assignment |
| DELETE | `/api/v1/consultants/{id}/projects/{projectId}` | Remove project assignment |
| | **Projects** | |
| POST | `/api/v1/projects` | Create project |
| GET | `/api/v1/projects` | Get all projects |
| GET | `/api/v1/projects/{id}` | Get project by ID |
| GET | `/api/v1/projects/by-name?name=` | Get project by name |
| GET | `/api/v1/projects/by-company/{companyId}` | Get projects by company |
| GET | `/api/v1/projects/by-required-skills?skillNames=` | Get projects by required skills |
| PUT | `/api/v1/projects/{id}` | Update project |
| DELETE | `/api/v1/projects/{id}` | Delete project |
| POST | `/api/v1/projects/{id}/company/{companyId}` | Assign company to project |
| POST | `/api/v1/projects/{id}/required-skills` | Add required skill to project |

---

## Typical Workflow

Here is a step-by-step example of setting up data and using the search:

```bash
# 1. Create a skill
SKILL_ID=$(curl -s -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -d '{"name": "Java", "synonyms": ["JDK"]}' | jq -r '.id')

# 2. Create a company
COMPANY_ID=$(curl -s -X POST http://localhost:8080/api/v1/companies \
  -H "Content-Type: application/json" \
  -d '{"name": "DNB", "field": "Finance"}' | jq -r '.id')

# 3. Create a consultant
CONSULTANT_ID=$(curl -s -X POST http://localhost:8080/api/v1/consultants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ola Nordmann",
    "email": "ola@example.com",
    "yearsOfExperience": 8,
    "availability": true,
    "wantsNewProject": true,
    "openToRemote": true
  }' | jq -r '.id')

# 4. Add skill to consultant
curl -X POST "http://localhost:8080/api/v1/consultants/${CONSULTANT_ID}/skills" \
  -H "Content-Type: application/json" \
  -d "{\"skillId\": \"${SKILL_ID}\", \"skillYearsOfExperience\": 8}"

# 5. Create a project with the company
PROJECT_ID=$(curl -s -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Nettbank Modernisering\",
    \"requirements\": [\"Modern frontend\", \"Security\"],
    \"startDate\": \"2026-03-01T09:00:00\",
    \"endDate\": \"2026-09-30T17:00:00\",
    \"companyId\": \"${COMPANY_ID}\"
  }" | jq -r '.id')

# 6. Add required skill to project
curl -X POST "http://localhost:8080/api/v1/projects/${PROJECT_ID}/required-skills" \
  -H "Content-Type: application/json" \
  -d "{\"skillId\": \"${SKILL_ID}\", \"minYearsOfExperience\": 5, \"isMandatory\": true}"

# 7. Search for matching consultants
curl "http://localhost:8080/api/v1/consultants/search?skillNames=Java&availability=true&openToRemote=true"
```

---

## Recent Changes (v2.1)

- Updated to Spring Boot 3.5.11 and Java 21
- Added support for project start and end dates
- Added consultant-to-project assignment management endpoints
- Enhanced search with weighted scoring system
- Added startDate and endDate to project assignments
- Improved availability filtering based on project end dates

---

*Documentation for Data-Driven Staffing API v2.1*