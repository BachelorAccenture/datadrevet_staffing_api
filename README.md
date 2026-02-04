# Data-Driven Staffing API Documentation

**Version:** 1.0  
**Base URL:** `http://localhost:8080/api/v1`

---

## Table of Contents

1. [Overview](#overview)
2. [Authentication](#authentication)
3. [Error Handling](#error-handling)
4. [Endpoints](#endpoints)
    - [Skills](#skills)
    - [Technologies](#technologies)
    - [Companies](#companies)
    - [Consultants](#consultants)
    - [Projects](#projects)

---

## Overview

The Data-Driven Staffing API is a RESTful service for managing consultants, their skills and technologies, companies, and project assignments. The system uses Neo4j as its graph database to enable efficient matching of consultants to projects based on skills, experience, and availability.

### Base URL

All endpoints are prefixed with:

```
http://localhost:8080/api/v1
```

### Content Type

All requests and responses use JSON:

```
Content-Type: application/json
```

### Proficiency Levels

The system uses four proficiency levels for skills and technologies:

| Level | Description |
|-------|-------------|
| `BEGINNER` | Basic understanding, limited practical experience |
| `INTERMEDIATE` | Solid understanding, can work independently |
| `ADVANCED` | Deep knowledge, can mentor others |
| `EXPERT` | Mastery level, recognized authority |

---

## Authentication

*Currently, the API does not require authentication. This section will be updated when authentication is implemented.*

---

## Error Handling

### Error Response Format

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Consultant not found with id: 123e4567-e89b-12d3-a456-426614174000",
  "timestamp": "2026-02-04T10:30:00"
}
```

### HTTP Status Codes

| Code | Description |
|------|-------------|
| `200 OK` | Request succeeded |
| `201 Created` | Resource created successfully |
| `204 No Content` | Resource deleted successfully |
| `400 Bad Request` | Invalid request (validation error) |
| `404 Not Found` | Resource not found |
| `500 Internal Server Error` | Server error |

---

## Endpoints

---

## Skills

Skills represent competencies that consultants can have (e.g., Project Management, System Architecture).

### Create Skill

Creates a new skill with optional synonyms.

**Endpoint:** `POST /skills`

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Skill name |
| `synonyms` | array | No | Alternative names for the skill |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Project Management",
    "synonyms": ["PM", "Prosjektledelse", "Project Lead"]
  }'
```

**Example Response (201 Created):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Project Management",
  "synonyms": ["PM", "Prosjektledelse", "Project Lead"]
}
```

---

### Get All Skills

Retrieves all skills in the system.

**Endpoint:** `GET /skills`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/skills
```

**Example Response (200 OK):**

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Project Management",
    "synonyms": ["PM", "Prosjektledelse"]
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440002",
    "name": "System Architecture",
    "synonyms": ["Solution Architecture", "Software Architecture"]
  }
]
```

---

### Get Skill by ID

Retrieves a specific skill by its UUID.

**Endpoint:** `GET /skills/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string (UUID) | Skill ID |

**Example Request:**

```bash
curl http://localhost:8080/api/v1/skills/550e8400-e29b-41d4-a716-446655440001
```

**Example Response (200 OK):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Project Management",
  "synonyms": ["PM", "Prosjektledelse"]
}
```

**Example Error Response (404 Not Found):**

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Skill not found with id: 550e8400-e29b-41d4-a716-446655440099",
  "timestamp": "2026-02-04T10:30:00"
}
```

---

### Search Skills

Searches for skills by name (case-insensitive partial match).

**Endpoint:** `GET /skills/search`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | Yes | Search term |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/skills/search?query=management"
```

**Example Response (200 OK):**

```json
[
  {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "name": "Project Management",
    "synonyms": ["PM", "Prosjektledelse"]
  },
  {
    "id": "550e8400-e29b-41d4-a716-446655440003",
    "name": "Change Management",
    "synonyms": []
  }
]
```

---

### Update Skill

Updates an existing skill.

**Endpoint:** `PUT /skills/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string (UUID) | Skill ID |

**Example Request:**

```bash
curl -X PUT http://localhost:8080/api/v1/skills/550e8400-e29b-41d4-a716-446655440001 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Project Management",
    "synonyms": ["PM", "Prosjektledelse", "Project Lead", "PMP"]
  }'
```

**Example Response (200 OK):**

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440001",
  "name": "Project Management",
  "synonyms": ["PM", "Prosjektledelse", "Project Lead", "PMP"]
}
```

---

### Delete Skill

Deletes a skill from the system.

**Endpoint:** `DELETE /skills/{id}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string (UUID) | Skill ID |

**Example Request:**

```bash
curl -X DELETE http://localhost:8080/api/v1/skills/550e8400-e29b-41d4-a716-446655440001
```

**Response:** `204 No Content` (empty body)

---

## Technologies

Technologies represent technical tools and frameworks (e.g., Java, React, Neo4j).

### Create Technology

Creates a new technology with optional synonyms.

**Endpoint:** `POST /technologies`

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Technology name |
| `synonyms` | array | No | Alternative names |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/v1/technologies \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Java",
    "synonyms": ["JDK", "Java SE", "Java EE"]
  }'
```

**Example Response (201 Created):**

```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "name": "Java",
  "synonyms": ["JDK", "Java SE", "Java EE"]
}
```

---

### Get All Technologies

**Endpoint:** `GET /technologies`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/technologies
```

**Example Response (200 OK):**

```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "name": "Java",
    "synonyms": ["JDK", "Java SE", "Java EE"]
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440002",
    "name": "React",
    "synonyms": ["ReactJS", "React.js"]
  }
]
```

---

### Get Technology by ID

**Endpoint:** `GET /technologies/{id}`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/technologies/660e8400-e29b-41d4-a716-446655440001
```

**Example Response (200 OK):**

```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "name": "Java",
  "synonyms": ["JDK", "Java SE", "Java EE"]
}
```

---

### Search Technologies

**Endpoint:** `GET /technologies/search`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | Yes | Search term |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/technologies/search?query=java"
```

**Example Response (200 OK):**

```json
[
  {
    "id": "660e8400-e29b-41d4-a716-446655440001",
    "name": "Java",
    "synonyms": ["JDK", "Java SE", "Java EE"]
  },
  {
    "id": "660e8400-e29b-41d4-a716-446655440003",
    "name": "JavaScript",
    "synonyms": ["JS", "ECMAScript"]
  }
]
```

---

### Update Technology

**Endpoint:** `PUT /technologies/{id}`

**Example Request:**

```bash
curl -X PUT http://localhost:8080/api/v1/technologies/660e8400-e29b-41d4-a716-446655440001 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Java",
    "synonyms": ["JDK", "Java SE", "Java EE", "OpenJDK"]
  }'
```

---

### Delete Technology

**Endpoint:** `DELETE /technologies/{id}`

**Example Request:**

```bash
curl -X DELETE http://localhost:8080/api/v1/technologies/660e8400-e29b-41d4-a716-446655440001
```

**Response:** `204 No Content`

---

## Companies

Companies represent client organizations that own projects.

### Create Company

**Endpoint:** `POST /companies`

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Company name |
| `field` | string | No | Industry/sector |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/v1/companies \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Equinor",
    "field": "Energy"
  }'
```

**Example Response (201 Created):**

```json
{
  "id": "770e8400-e29b-41d4-a716-446655440001",
  "name": "Equinor",
  "field": "Energy"
}
```

---

### Get All Companies

**Endpoint:** `GET /companies`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/companies
```

**Example Response (200 OK):**

```json
[
  {
    "id": "770e8400-e29b-41d4-a716-446655440001",
    "name": "Equinor",
    "field": "Energy"
  },
  {
    "id": "770e8400-e29b-41d4-a716-446655440002",
    "name": "DNB",
    "field": "Finance"
  }
]
```

---

### Get Company by ID

**Endpoint:** `GET /companies/{id}`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/companies/770e8400-e29b-41d4-a716-446655440001
```

---

### Search Companies

**Endpoint:** `GET /companies/search`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `query` | string | Yes | Search term |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/companies/search?query=equi"
```

---

### Get Companies by Field

**Endpoint:** `GET /companies/by-field`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `field` | string | Yes | Industry/sector |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/companies/by-field?field=Energy"
```

**Example Response (200 OK):**

```json
[
  {
    "id": "770e8400-e29b-41d4-a716-446655440001",
    "name": "Equinor",
    "field": "Energy"
  }
]
```

---

### Update Company

**Endpoint:** `PUT /companies/{id}`

**Example Request:**

```bash
curl -X PUT http://localhost:8080/api/v1/companies/770e8400-e29b-41d4-a716-446655440001 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Equinor ASA",
    "field": "Energy"
  }'
```

---

### Delete Company

**Endpoint:** `DELETE /companies/{id}`

**Example Request:**

```bash
curl -X DELETE http://localhost:8080/api/v1/companies/770e8400-e29b-41d4-a716-446655440001
```

**Response:** `204 No Content`

---

## Consultants

Consultants are the core entities representing people with skills and technologies.

### Create Consultant

**Endpoint:** `POST /consultants`

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Full name |
| `email` | string | Yes | Email address (must be valid) |
| `role` | string | No | Job title/role |
| `yearsOfExperience` | integer | No | Total years of experience |
| `availability` | boolean | No | Currently available for projects |
| `wantsNewProject` | boolean | No | Seeking new project assignment |
| `openToRelocation` | boolean | No | Willing to relocate |
| `openToRemote` | boolean | No | Open to remote work |
| `preferredRegions` | array | No | Preferred work locations |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/v1/consultants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hugo Harnæs",
    "email": "hugo.harnaes@accenture.com",
    "role": "Software Developer",
    "yearsOfExperience": 3,
    "availability": true,
    "wantsNewProject": true,
    "openToRelocation": false,
    "openToRemote": true,
    "preferredRegions": ["Oslo", "Bergen"]
  }'
```

**Example Response (201 Created):**

```json
{
  "id": "880e8400-e29b-41d4-a716-446655440001",
  "name": "Hugo Harnæs",
  "email": "hugo.harnaes@accenture.com",
  "role": "Software Developer",
  "yearsOfExperience": 3,
  "availability": true,
  "wantsNewProject": true,
  "openToRelocation": false,
  "openToRemote": true,
  "preferredRegions": ["Oslo", "Bergen"],
  "skills": [],
  "technologies": []
}
```

---

### Get All Consultants

**Endpoint:** `GET /consultants`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/consultants
```

---

### Get Consultant by ID

**Endpoint:** `GET /consultants/{id}`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/consultants/880e8400-e29b-41d4-a716-446655440001
```

**Example Response (200 OK):**

```json
{
  "id": "880e8400-e29b-41d4-a716-446655440001",
  "name": "Hugo Harnæs",
  "email": "hugo.harnaes@accenture.com",
  "role": "Software Developer",
  "yearsOfExperience": 3,
  "availability": true,
  "wantsNewProject": true,
  "openToRelocation": false,
  "openToRemote": true,
  "preferredRegions": ["Oslo", "Bergen"],
  "skills": [
    {
      "skillId": "550e8400-e29b-41d4-a716-446655440001",
      "skillName": "Project Management",
      "level": "INTERMEDIATE"
    }
  ],
  "technologies": [
    {
      "technologyId": "660e8400-e29b-41d4-a716-446655440001",
      "technologyName": "Java",
      "level": "ADVANCED",
      "yearsExperience": 3
    }
  ]
}
```

---

### Get Consultant by Email

**Endpoint:** `GET /consultants/by-email`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `email` | string | Yes | Email address |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/consultants/by-email?email=hugo.harnaes@accenture.com"
```

---

### Get Available Consultants

Returns consultants where `availability = true`.

**Endpoint:** `GET /consultants/available`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/consultants/available
```

---

### Get Consultants Wanting New Project

Returns consultants where `wantsNewProject = true`.

**Endpoint:** `GET /consultants/wanting-new-project`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/consultants/wanting-new-project
```

---

### Get Consultants by Skills

Returns consultants that have any of the specified skills.

**Endpoint:** `GET /consultants/by-skills`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `skillNames` | array | Yes | List of skill names |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/consultants/by-skills?skillNames=Project%20Management&skillNames=System%20Architecture"
```

**Example Response (200 OK):**

```json
[
  {
    "id": "880e8400-e29b-41d4-a716-446655440001",
    "name": "Hugo Harnæs",
    "email": "hugo.harnaes@accenture.com",
    "skills": [
      {
        "skillId": "550e8400-e29b-41d4-a716-446655440001",
        "skillName": "Project Management",
        "level": "INTERMEDIATE"
      }
    ],
    "technologies": []
  }
]
```

---

### Get Consultants by Technologies

Returns consultants that know any of the specified technologies.

**Endpoint:** `GET /consultants/by-technologies`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `technologyNames` | array | Yes | List of technology names |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/consultants/by-technologies?technologyNames=Java&technologyNames=React"
```

---

### Get Available Consultants with Minimum Experience

Returns available consultants with at least the specified years of experience.

**Endpoint:** `GET /consultants/available-with-experience`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `minYears` | integer | Yes | Minimum years of experience |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/consultants/available-with-experience?minYears=5"
```

---

### Update Consultant

**Endpoint:** `PUT /consultants/{id}`

**Example Request:**

```bash
curl -X PUT http://localhost:8080/api/v1/consultants/880e8400-e29b-41d4-a716-446655440001 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Hugo Harnæs",
    "email": "hugo.harnaes@accenture.com",
    "role": "Senior Software Developer",
    "yearsOfExperience": 4,
    "availability": true,
    "wantsNewProject": false,
    "openToRelocation": false,
    "openToRemote": true,
    "preferredRegions": ["Oslo"]
  }'
```

---

### Delete Consultant

**Endpoint:** `DELETE /consultants/{id}`

**Example Request:**

```bash
curl -X DELETE http://localhost:8080/api/v1/consultants/880e8400-e29b-41d4-a716-446655440001
```

**Response:** `204 No Content`

---

### Add Skill to Consultant

Adds a skill relationship (HAS_SKILL) between a consultant and a skill.

**Endpoint:** `POST /consultants/{id}/skills`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string (UUID) | Consultant ID |

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `skillId` | string | Yes | Skill UUID |
| `level` | string | Yes | Proficiency level |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/v1/consultants/880e8400-e29b-41d4-a716-446655440001/skills \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "550e8400-e29b-41d4-a716-446655440001",
    "level": "ADVANCED"
  }'
```

**Example Response (200 OK):**

```json
{
  "id": "880e8400-e29b-41d4-a716-446655440001",
  "name": "Hugo Harnæs",
  "skills": [
    {
      "skillId": "550e8400-e29b-41d4-a716-446655440001",
      "skillName": "Project Management",
      "level": "ADVANCED"
    }
  ],
  "technologies": []
}
```

---

### Add Technology to Consultant

Adds a technology relationship (KNOWS) between a consultant and a technology.

**Endpoint:** `POST /consultants/{id}/technologies`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string (UUID) | Consultant ID |

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `technologyId` | string | Yes | Technology UUID |
| `level` | string | Yes | Proficiency level |
| `yearsExperience` | integer | No | Years using this technology |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/v1/consultants/880e8400-e29b-41d4-a716-446655440001/technologies \
  -H "Content-Type: application/json" \
  -d '{
    "technologyId": "660e8400-e29b-41d4-a716-446655440001",
    "level": "EXPERT",
    "yearsExperience": 5
  }'
```

**Example Response (200 OK):**

```json
{
  "id": "880e8400-e29b-41d4-a716-446655440001",
  "name": "Hugo Harnæs",
  "skills": [],
  "technologies": [
    {
      "technologyId": "660e8400-e29b-41d4-a716-446655440001",
      "technologyName": "Java",
      "level": "EXPERT",
      "yearsExperience": 5
    }
  ]
}
```

---

## Projects

Projects represent client engagements that require specific skills and technologies.

### Create Project

**Endpoint:** `POST /projects`

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `name` | string | Yes | Project name |
| `requirements` | array | No | List of requirements |
| `date` | string | No | Start date (ISO 8601) |
| `companyId` | string | No | Company UUID to assign |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Digital Transformation Platform",
    "requirements": ["Cloud migration", "API development", "Security compliance"],
    "date": "2026-03-01T09:00:00",
    "companyId": "770e8400-e29b-41d4-a716-446655440001"
  }'
```

**Example Response (201 Created):**

```json
{
  "id": "990e8400-e29b-41d4-a716-446655440001",
  "name": "Digital Transformation Platform",
  "requirements": ["Cloud migration", "API development", "Security compliance"],
  "date": "2026-03-01T09:00:00",
  "company": {
    "id": "770e8400-e29b-41d4-a716-446655440001",
    "name": "Equinor",
    "field": "Energy"
  },
  "requiredSkills": [],
  "requiredTechnologies": []
}
```

---

### Get All Projects

**Endpoint:** `GET /projects`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/projects
```

---

### Get Project by ID

**Endpoint:** `GET /projects/{id}`

**Example Request:**

```bash
curl http://localhost:8080/api/v1/projects/990e8400-e29b-41d4-a716-446655440001
```

**Example Response (200 OK):**

```json
{
  "id": "990e8400-e29b-41d4-a716-446655440001",
  "name": "Digital Transformation Platform",
  "requirements": ["Cloud migration", "API development"],
  "date": "2026-03-01T09:00:00",
  "company": {
    "id": "770e8400-e29b-41d4-a716-446655440001",
    "name": "Equinor",
    "field": "Energy"
  },
  "requiredSkills": [
    {
      "skillId": "550e8400-e29b-41d4-a716-446655440001",
      "skillName": "Project Management",
      "minLevel": "INTERMEDIATE",
      "isMandatory": true
    }
  ],
  "requiredTechnologies": [
    {
      "technologyId": "660e8400-e29b-41d4-a716-446655440001",
      "technologyName": "Java",
      "minLevel": "ADVANCED",
      "isMandatory": true
    }
  ]
}
```

---

### Get Project by Name

**Endpoint:** `GET /projects/by-name`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `name` | string | Yes | Project name |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/projects/by-name?name=Digital%20Transformation%20Platform"
```

---

### Get Projects by Company

Returns all projects owned by a specific company.

**Endpoint:** `GET /projects/by-company/{companyId}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `companyId` | string (UUID) | Company ID |

**Example Request:**

```bash
curl http://localhost:8080/api/v1/projects/by-company/770e8400-e29b-41d4-a716-446655440001
```

---

### Get Projects by Required Skills

Returns projects that require any of the specified skills.

**Endpoint:** `GET /projects/by-required-skills`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `skillNames` | array | Yes | List of skill names |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/projects/by-required-skills?skillNames=Project%20Management"
```

---

### Get Projects by Required Technologies

Returns projects that require any of the specified technologies.

**Endpoint:** `GET /projects/by-required-technologies`

**Query Parameters:**

| Parameter | Type | Required | Description |
|-----------|------|----------|-------------|
| `technologyNames` | array | Yes | List of technology names |

**Example Request:**

```bash
curl "http://localhost:8080/api/v1/projects/by-required-technologies?technologyNames=Java&technologyNames=Spring%20Boot"
```

---

### Update Project

**Endpoint:** `PUT /projects/{id}`

**Example Request:**

```bash
curl -X PUT http://localhost:8080/api/v1/projects/990e8400-e29b-41d4-a716-446655440001 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Digital Transformation Platform v2",
    "requirements": ["Cloud migration", "API development", "Security compliance", "CI/CD"],
    "date": "2026-04-01T09:00:00"
  }'
```

---

### Delete Project

**Endpoint:** `DELETE /projects/{id}`

**Example Request:**

```bash
curl -X DELETE http://localhost:8080/api/v1/projects/990e8400-e29b-41d4-a716-446655440001
```

**Response:** `204 No Content`

---

### Assign Company to Project

Assigns a company as the owner of a project.

**Endpoint:** `POST /projects/{id}/company/{companyId}`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string (UUID) | Project ID |
| `companyId` | string (UUID) | Company ID |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/v1/projects/990e8400-e29b-41d4-a716-446655440001/company/770e8400-e29b-41d4-a716-446655440001
```

---

### Add Required Skill to Project

Adds a skill requirement (REQUIRES_SKILL) to a project.

**Endpoint:** `POST /projects/{id}/required-skills`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string (UUID) | Project ID |

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `skillId` | string | Yes | Skill UUID |
| `minLevel` | string | Yes | Minimum proficiency level |
| `isMandatory` | boolean | No | Whether skill is mandatory (default: false) |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/v1/projects/990e8400-e29b-41d4-a716-446655440001/required-skills \
  -H "Content-Type: application/json" \
  -d '{
    "skillId": "550e8400-e29b-41d4-a716-446655440001",
    "minLevel": "INTERMEDIATE",
    "isMandatory": true
  }'
```

**Example Response (200 OK):**

```json
{
  "id": "990e8400-e29b-41d4-a716-446655440001",
  "name": "Digital Transformation Platform",
  "requiredSkills": [
    {
      "skillId": "550e8400-e29b-41d4-a716-446655440001",
      "skillName": "Project Management",
      "minLevel": "INTERMEDIATE",
      "isMandatory": true
    }
  ],
  "requiredTechnologies": []
}
```

---

### Add Required Technology to Project

Adds a technology requirement (REQUIRES_TECHNOLOGY) to a project.

**Endpoint:** `POST /projects/{id}/required-technologies`

**Path Parameters:**

| Parameter | Type | Description |
|-----------|------|-------------|
| `id` | string (UUID) | Project ID |

**Request Body:**

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `technologyId` | string | Yes | Technology UUID |
| `minLevel` | string | Yes | Minimum proficiency level |
| `isMandatory` | boolean | No | Whether technology is mandatory (default: false) |

**Example Request:**

```bash
curl -X POST http://localhost:8080/api/v1/projects/990e8400-e29b-41d4-a716-446655440001/required-technologies \
  -H "Content-Type: application/json" \
  -d '{
    "technologyId": "660e8400-e29b-41d4-a716-446655440001",
    "minLevel": "ADVANCED",
    "isMandatory": true
  }'
```

**Example Response (200 OK):**

```json
{
  "id": "990e8400-e29b-41d4-a716-446655440001",
  "name": "Digital Transformation Platform",
  "requiredSkills": [],
  "requiredTechnologies": [
    {
      "technologyId": "660e8400-e29b-41d4-a716-446655440001",
      "technologyName": "Java",
      "minLevel": "ADVANCED",
      "isMandatory": true
    }
  ]
}
```

---

## Quick Start Guide

### Step 1: Start the Application

```bash
# Start Neo4j
docker-compose up -d

# Run the application
./mvnw spring-boot:run
```

### Step 2: Create Base Data

```bash
# Create Skills
curl -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -d '{"name": "Project Management", "synonyms": ["PM"]}'

curl -X POST http://localhost:8080/api/v1/skills \
  -H "Content-Type: application/json" \
  -d '{"name": "System Architecture", "synonyms": ["Solution Architecture"]}'

# Create Technologies
curl -X POST http://localhost:8080/api/v1/technologies \
  -H "Content-Type: application/json" \
  -d '{"name": "Java", "synonyms": ["JDK", "Java SE"]}'

curl -X POST http://localhost:8080/api/v1/technologies \
  -H "Content-Type: application/json" \
  -d '{"name": "Spring Boot", "synonyms": ["Spring Framework"]}'

# Create a Company
curl -X POST http://localhost:8080/api/v1/companies \
  -H "Content-Type: application/json" \
  -d '{"name": "Equinor", "field": "Energy"}'
```

### Step 3: Get IDs

```bash
# Get all skills and note the IDs
curl http://localhost:8080/api/v1/skills | jq

# Get all technologies and note the IDs
curl http://localhost:8080/api/v1/technologies | jq

# Get all companies and note the IDs
curl http://localhost:8080/api/v1/companies | jq
```

### Step 4: Create Consultant with Skills

```bash
# Create consultant
curl -X POST http://localhost:8080/api/v1/consultants \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test Consultant",
    "email": "test@example.com",
    "role": "Developer",
    "yearsOfExperience": 5,
    "availability": true
  }'

# Add skill (replace IDs with actual values)
curl -X POST http://localhost:8080/api/v1/consultants/{CONSULTANT_ID}/skills \
  -H "Content-Type: application/json" \
  -d '{"skillId": "{SKILL_ID}", "level": "ADVANCED"}'

# Add technology (replace IDs with actual values)
curl -X POST http://localhost:8080/api/v1/consultants/{CONSULTANT_ID}/technologies \
  -H "Content-Type: application/json" \
  -d '{"technologyId": "{TECH_ID}", "level": "EXPERT", "yearsExperience": 5}'
```

### Step 5: Create Project with Requirements

```bash
# Create project (replace COMPANY_ID)
curl -X POST http://localhost:8080/api/v1/projects \
  -H "Content-Type: application/json" \
  -d '{
    "name": "New Project",
    "requirements": ["Requirement 1"],
    "companyId": "{COMPANY_ID}"
  }'

# Add required skill (replace IDs)
curl -X POST http://localhost:8080/api/v1/projects/{PROJECT_ID}/required-skills \
  -H "Content-Type: application/json" \
  -d '{"skillId": "{SKILL_ID}", "minLevel": "INTERMEDIATE", "isMandatory": true}'

# Add required technology (replace IDs)
curl -X POST http://localhost:8080/api/v1/projects/{PROJECT_ID}/required-technologies \
  -H "Content-Type: application/json" \
  -d '{"technologyId": "{TECH_ID}", "minLevel": "ADVANCED", "isMandatory": true}'
```

---

## Endpoint Summary

| Method | Endpoint | Description |
|--------|----------|-------------|
| **Skills** | | |
| POST | `/skills` | Create skill |
| GET | `/skills` | Get all skills |
| GET | `/skills/{id}` | Get skill by ID |
| GET | `/skills/search?query=` | Search skills |
| PUT | `/skills/{id}` | Update skill |
| DELETE | `/skills/{id}` | Delete skill |
| **Technologies** | | |
| POST | `/technologies` | Create technology |
| GET | `/technologies` | Get all technologies |
| GET | `/technologies/{id}` | Get technology by ID |
| GET | `/technologies/search?query=` | Search technologies |
| PUT | `/technologies/{id}` | Update technology |
| DELETE | `/technologies/{id}` | Delete technology |
| **Companies** | | |
| POST | `/companies` | Create company |
| GET | `/companies` | Get all companies |
| GET | `/companies/{id}` | Get company by ID |
| GET | `/companies/search?query=` | Search companies |
| GET | `/companies/by-field?field=` | Get by industry |
| PUT | `/companies/{id}` | Update company |
| DELETE | `/companies/{id}` | Delete company |
| **Consultants** | | |
| POST | `/consultants` | Create consultant |
| GET | `/consultants` | Get all consultants |
| GET | `/consultants/{id}` | Get consultant by ID |
| GET | `/consultants/by-email?email=` | Get by email |
| GET | `/consultants/available` | Get available |
| GET | `/consultants/wanting-new-project` | Get seeking projects |
| GET | `/consultants/by-skills?skillNames=` | Get by skills |
| GET | `/consultants/by-technologies?technologyNames=` | Get by technologies |
| GET | `/consultants/available-with-experience?minYears=` | Get available with experience |
| PUT | `/consultants/{id}` | Update consultant |
| DELETE | `/consultants/{id}` | Delete consultant |
| POST | `/consultants/{id}/skills` | Add skill |
| POST | `/consultants/{id}/technologies` | Add technology |
| **Projects** | | |
| POST | `/projects` | Create project |
| GET | `/projects` | Get all projects |
| GET | `/projects/{id}` | Get project by ID |
| GET | `/projects/by-name?name=` | Get by name |
| GET | `/projects/by-company/{companyId}` | Get by company |
| GET | `/projects/by-required-skills?skillNames=` | Get by required skills |
| GET | `/projects/by-required-technologies?technologyNames=` | Get by required technologies |
| PUT | `/projects/{id}` | Update project |
| DELETE | `/projects/{id}` | Delete project |
| POST | `/projects/{id}/company/{companyId}` | Assign company |
| POST | `/projects/{id}/required-skills` | Add required skill |
| POST | `/projects/{id}/required-technologies` | Add required technology |

---

*Documentation generated for Data-Driven Staffing API v1.0*
