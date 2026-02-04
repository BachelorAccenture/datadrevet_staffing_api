# CSV Data Loader Guide

## Oversikt

DataLoader leser automatisk CSV-filer fra `src/main/resources/data/` og fyller Neo4j-databasen med testdata når applikasjonen starter i `dev`-profil.

## CSV-filer

### 📁 consultants.csv (25 konsulenter)

**Format:**
```
name,email,role,yearsOfExperience,availability,wantsNewProject,openToRemote,openToRelocation,preferredRegions,skills,technologies
```

**Eksempel:**
```csv
Ola Nordmann,ola.nordmann@example.com,Senior Java Developer,8,true,true,true,false,"Oslo;Bergen","Java:EXPERT;Spring Framework:EXPERT","Neo4j:ADVANCED:4;PostgreSQL:EXPERT:7"
```

**Feltbeskrivelse:**
- `name`: Fullt navn på konsulenten
- `email`: E-postadresse (unik)
- `role`: Jobbstilling/rolle
- `yearsOfExperience`: Antall års erfaring (heltall)
- `availability`: Er tilgjengelig for nye oppdrag (true/false)
- `wantsNewProject`: Ønsker nytt prosjekt (true/false)
- `openToRemote`: Åpen for fjernarbeid (true/false)
- `openToRelocation`: Åpen for relokasjon (true/false)
- `preferredRegions`: Foretrukne regioner, separert med `;` (f.eks. "Oslo;Bergen")
- `skills`: Ferdigheter med nivå, format `SkillName:Level` separert med `;`
  - Nivåer: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
  - Eksempel: `"Java:EXPERT;Python:INTERMEDIATE;Agile:ADVANCED"`
- `technologies`: Teknologier med nivå og års erfaring, format `TechName:Level:YearsExp` separert med `;`
  - Eksempel: `"Docker:ADVANCED:3;PostgreSQL:EXPERT:7;Git:ADVANCED:5"`

---

### 📁 skills.csv (12 ferdigheter)

**Format:**
```
name,synonyms
```

**Eksempel:**
```csv
Java,Java SE;Java EE;JDK
Python,Python3;Python 3
DevOps,
```

**Feltbeskrivelse:**
- `name`: Navn på ferdigheten
- `synonyms`: Alternative navn separert med `;` (kan være tom)

---

### 📁 technologies.csv (12 teknologier)

**Format:**
```
name,synonyms
```

**Eksempel:**
```csv
Neo4j,Neo4j Graph Database
PostgreSQL,Postgres;PSQL
Redis,
```

**Feltbeskrivelse:**
- `name`: Navn på teknologien
- `synonyms`: Alternative navn separert med `;` (kan være tom)

---

### 📁 companies.csv (10 bedrifter)

**Format:**
```
name,field
```

**Eksempel:**
```csv
TechCorp AS,Software Development
Data Consulting Norge,Data Analytics
```

**Feltbeskrivelse:**
- `name`: Bedriftsnavn
- `field`: Bransje/felt

---

### 📁 projects.csv (10 prosjekter)

**Format:**
```
name,companyName,requirements,requiredSkills,requiredTechnologies
```

**Eksempel:**
```csv
E-Commerce Platform,TechCorp AS,"Migrate to microservices;Cloud deployment","Java:ADVANCED:true;Agile:INTERMEDIATE:false","Docker:INTERMEDIATE:true;AWS:INTERMEDIATE:true"
```

**Feltbeskrivelse:**
- `name`: Prosjektnavn
- `companyName`: Navn på bedriften som eier prosjektet (må matche navn i companies.csv)
- `requirements`: Liste over krav separert med `;`
- `requiredSkills`: Påkrevde ferdigheter, format `SkillName:MinLevel:IsMandatory` separert med `;`
  - MinLevel: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
  - IsMandatory: true = obligatorisk, false = valgfri
  - Eksempel: `"Java:ADVANCED:true;Agile:INTERMEDIATE:false"`
- `requiredTechnologies`: Påkrevde teknologier, format `TechName:MinLevel:IsMandatory` separert med `;`
  - Eksempel: `"Docker:INTERMEDIATE:true;Kubernetes:BEGINNER:false"`

---

## Hvordan legge til/endre data

### Legge til ny konsulent

1. Åpne `consultants.csv`
2. Legg til en ny linje med alle 11 felter
3. Pass på at:
   - E-postadressen er unik
   - Skills og technologies matcher navn i de respektive CSV-filene
   - Nivåer er skrevet i CAPS: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
   - Booleans er skrevet i lowercase: true/false
   - Lister er separert med `;` og omgitt av `"` hvis de inneholder komma

**Eksempel på ny konsulent:**
```csv
Nina Solberg,nina.solberg@example.com,Backend Developer,4,true,true,true,false,"Oslo","Python:ADVANCED;SQL:INTERMEDIATE","PostgreSQL:INTERMEDIATE:3;Docker:BEGINNER:1"
```

### Legge til ny skill/technology

1. Legg til i `skills.csv` eller `technologies.csv`
2. Bruk samme navn når du refererer til den i `consultants.csv` eller `projects.csv`

### Legge til nytt prosjekt

1. Sørg for at bedriften finnes i `companies.csv`
2. Legg til prosjekt i `projects.csv`
3. Skills og technologies må matche navn i de respektive filene

---

## Kjøre DataLoader

### Start applikasjonen

```powershell
# Start Neo4j først (Docker Compose)
docker-compose up -d

# Kjør Spring Boot
mvn spring-boot:run
```

### Logger

Du vil se følgende i loggen:
```
[DataLoader] - Starting data initialization from CSV files...
[DataLoader] - Loaded 12 skills
[DataLoader] - Loaded 12 technologies
[DataLoader] - Loaded 10 companies
[DataLoader] - Loaded 25 consultants
[DataLoader] - Loaded 10 projects
[DataLoader] - Data initialization completed successfully!
```

### Hvis data allerede eksisterer

DataLoader hopper over initialisering hvis databasen allerede inneholder data:
```
[DataLoader] - Database already contains data. Skipping initialization.
```

For å laste data på nytt, slett all data fra Neo4j:
```cypher
MATCH (n) DETACH DELETE n
```

---

## Statistikk over dummydata

### Konsulenter (25 stk)

**Roller:**
- 5 Full Stack Developers
- 4 Backend Developers
- 3 Frontend Developers
- 3 DevOps Engineers
- 2 Senior Java Developers
- 2 Cloud Architects/Developers
- 1 Data Engineer
- 1 Data Scientist
- 1 Machine Learning Engineer
- 1 UX Designer
- 1 Security Engineer
- 1 .NET Developer

**Erfaring:**
- Gjennomsnitt: 6.8 år
- Min: 3 år
- Maks: 12 år

**Tilgjengelighet:**
- 19 tilgjengelige (76%)
- 6 opptatt (24%)

**Ønsker nytt prosjekt:**
- 15 ønsker nytt prosjekt (60%)
- 10 ønsker ikke (40%)

**Geografisk fordeling (preferanser):**
- Oslo: 17 konsulenter
- Bergen: 7 konsulenter
- Trondheim: 6 konsulenter
- Stavanger: 4 konsulenter
- Andre: 8 konsulenter

### Mest populære skills (blant konsulenter):
1. Agile - 15 konsulenter
2. Python - 11 konsulenter
3. JavaScript - 9 konsulenter
4. Java - 7 konsulenter
5. React - 7 konsulenter
6. SQL - 8 konsulenter

### Mest brukte teknologier (blant konsulenter):
1. Git - 16 konsulenter
2. Docker - 16 konsulenter
3. PostgreSQL - 11 konsulenter
4. AWS - 9 konsulenter
5. Kubernetes - 5 konsulenter

### Prosjekter (10 stk)

**Bedrifter:**
- TechCorp AS: 1 prosjekt
- Data Consulting Norge: 1 prosjekt
- Cloud Solutions AS: 1 prosjekt
- Finance IT Solutions: 1 prosjekt
- HealthTech Innovations: 1 prosjekt
- Nordic Software Group: 1 prosjekt
- Digital Transformation AS: 1 prosjekt
- Smart Systems Norway: 1 prosjekt
- Consulting Partners: 1 prosjekt
- Innovation Labs: 1 prosjekt

**Mest etterspurte skills i prosjekter:**
1. Python - 5 prosjekter
2. React - 4 prosjekter
3. Java - 3 prosjekter
4. SQL - 3 prosjekter

**Mest etterspurte teknologier:**
1. Docker - 6 prosjekter
2. AWS - 5 prosjekter
3. PostgreSQL - 4 prosjekter
4. MongoDB - 3 prosjekter

---

## Feilsøking

### Feil: "Failed to load data from CSV files"

**Årsaker:**
1. CSV-fil mangler eller er i feil mappe
2. Feil format i CSV (feil antall kolonner)
3. Ugyldig verdi (f.eks. tekst i stedet for tall)
4. Skill/Technology navn matcher ikke mellom filer

**Løsning:**
- Sjekk at alle CSV-filer finnes i `src/main/resources/data/`
- Valider format på hver linje
- Sjekk logger for detaljer om feilen

### Feil: "Skill not found"

**Løsning:**
- Sørg for at skill/technology er definert i `skills.csv` eller `technologies.csv` først
- Navnene må matche nøyaktig (case-sensitive)

### Tom database etter oppstart

**Løsning:**
- Sjekk at `spring.profiles.active=dev` er satt i `application.properties`
- Sjekk logger for feilmeldinger
- Verifiser at Neo4j kjører og er tilgjengelig

---

## Tips

1. **Bruk tekstbehandler med CSV-støtte** (f.eks. Excel, LibreOffice, VSCode med CSV-utvidelse)
2. **Pass på encoding** - bruk UTF-8 for norske tegn (æ, ø, å)
3. **Test med få rader først** - legg til en konsulent av gangen for å finne feil
4. **Backup CSV-filer** før store endringer
5. **Bruk konsistente navn** - skill/technology-navn må være identiske på tvers av filer

---

## Eksempel: Legg til en ny konsulent med komplett profil

```csv
Liv Johansen,liv.johansen@example.com,Senior Cloud Developer,9,true,true,true,true,"Oslo;Bergen;Trondheim","Python:EXPERT;DevOps:EXPERT;Agile:ADVANCED;SQL:ADVANCED","AWS:EXPERT:8;Azure:ADVANCED:6;Docker:EXPERT:7;Kubernetes:EXPERT:6;Git:EXPERT:9;Jenkins:ADVANCED:5"
```

Denne konsulenten:
- Heter Liv Johansen
- Er Senior Cloud Developer med 9 års erfaring
- Er tilgjengelig og ønsker nytt prosjekt
- Åpen for både remote og relokasjon
- Prefererer Oslo, Bergen eller Trondheim
- Har 4 skills: Python (EXPERT), DevOps (EXPERT), Agile (ADVANCED), SQL (ADVANCED)
- Kan 6 teknologier med ulik erfaring

