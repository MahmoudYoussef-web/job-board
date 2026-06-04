# 💼 Smart Recruitment Management System

[![Java](https://img.shields.io/badge/Java-21-%23ED8B00?logo=openjdk)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.3-%236DB33F?logo=springboot)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8-%234479A1?logo=mysql)](https://www.mysql.com/)
[![JWT](https://img.shields.io/badge/Auth-JWT_+_Refresh-%23000000?logo=jsonwebtokens)](https://jwt.io/)
[![PDFBox](https://img.shields.io/badge/Resume-PDFBox_2.0.30-%23FF0000?logo=adobe)](https://pdfbox.apache.org/)
[![Swagger](https://img.shields.io/badge/API-Swagger_UI-%2385EA2D?logo=swagger)](https://swagger.io/)
[![Tests](https://img.shields.io/badge/Build-Passing-brightgreen)]()

A production-grade **Smart Recruitment Management System** connecting employers with candidates. Built with **Spring Boot 3.2.3** and **Java 21** — features resume parsing with PDFBox, a rule-based matching engine with candidate ranking, a 10-state ATS pipeline, interview management, and skill gap analysis with importance levels.

> 🏗️ **Architecture:** Layered monolithic design (Controller → Service → Repository → DB) with JWT-secured REST API, role-based access control (EMPLOYER / CANDIDATE), centralized exception handling, and DTO-based data transfer.

---

## 🏗 System Architecture

```mermaid
flowchart LR
    classDef client fill:#e3f2fd,stroke:#1565c0,stroke-width:2px
    classDef security fill:#ffebee,stroke:#c62828,stroke-width:2px
    classDef api fill:#e0f2f1,stroke:#00695c,stroke-width:2px
    classDef service fill:#e3f2fd,stroke:#1565c0,stroke-width:2px
    classDef repo fill:#e8f5e9,stroke:#2e7d32,stroke-width:2px
    classDef db fill:#fff8e1,stroke:#f57f17,stroke-width:2px

    Client(["Candidates & Employers"])
    Client:::client

    subgraph Security["Security Layer"]
        JWT["JWT Authentication"]
        ROLE["Role Authorization"]
        CORS["CORS Configuration"]
    end

    subgraph API["API Layer (Controllers)"]
        AUTH["Auth"]
        JOBCTRL["Job"]
        APPCTRL["Application"]
        RESUMECTRL["Resume"]
        COMPANYCTRL["Company"]
        INTERVCTRL["Interview"]
        SKILLCTRL["Skill-Gap"]
        DASHCTRL["Dashboard"]
    end

    subgraph Services["Service Layer (Business Logic)"]
        AUTHSVC["Auth Service"]
        JOBSVC["Job Service"]
        APPSVC["Application Service"]
        RESUMESVC["Resume Parser"]
        SKILLDICT["Skill Dictionary"]
        MATCHSVC["Matching Engine"]
        TRANSITIONS["Status Validator"]
        COMPSVC["Company Service"]
        INTERVSVC["Interview Service"]
        DASHSVC["Dashboard Service"]
    end

    subgraph Repos["Repository Layer (Data Access)"]
        USERREPO["User Repo"]
        JOBREPO["Job Repo"]
        APPREPO["Application Repo"]
        RESUMEREPO["Resume Repo"]
        COMPANYREPO["Company Repo"]
        INTERVREPO["Interview Repo"]
        HISTREPO["History Repo"]
    end

    DB[("MySQL 8")]
    DB:::db

    Client --> JWT
    JWT --> ROLE
    ROLE --> CORS

    CORS --> AUTH
    CORS --> JOBCTRL
    CORS --> APPCTRL
    CORS --> RESUMECTRL
    CORS --> COMPANYCTRL
    CORS --> INTERVCTRL
    CORS --> SKILLCTRL
    CORS --> DASHCTRL

    AUTH --> AUTHSVC
    JOBCTRL --> JOBSVC
    APPCTRL --> APPSVC
    RESUMECTRL --> RESUMESVC
    COMPANYCTRL --> COMPSVC
    INTERVCTRL --> INTERVSVC
    SKILLCTRL --> MATCHSVC
    DASHCTRL --> DASHSVC

    RESUMESVC --> SKILLDICT
    MATCHSVC --> SKILLDICT
    APPSVC --> MATCHSVC
    APPSVC --> TRANSITIONS

    AUTHSVC --> USERREPO
    JOBSVC --> JOBREPO
    APPSVC --> APPREPO
    RESUMESVC --> RESUMEREPO
    COMPSVC --> COMPANYREPO
    INTERVSVC --> INTERVREPO
    DASHSVC --> APPREPO
    DASHSVC --> JOBREPO
    DASHSVC --> INTERVREPO
    APPSVC --> HISTREPO

    USERREPO --> DB
    JOBREPO --> DB
    APPREPO --> DB
    RESUMEREPO --> DB
    COMPANYREPO --> DB
    INTERVREPO --> DB
    HISTREPO --> DB

    class Client client
    class JWT,ROLE,CORS security
    class AUTH,JOBCTRL,APPCTRL,RESUMECTRL,COMPANYCTRL,INTERVCTRL,SKILLCTRL,DASHCTRL api
    class AUTHSVC,JOBSVC,APPSVC,RESUMESVC,SKILLDICT,MATCHSVC,TRANSITIONS,COMPSVC,INTERVSVC,DASHSVC service
    class USERREPO,JOBREPO,APPREPO,RESUMEREPO,COMPANYREPO,INTERVREPO,HISTREPO repo
    class DB db
```

---

## 📊 Layer Legend

| Layer | Color | Responsibility | Key Components |
|-------|-------|---------------|----------------|
| 👥 Client | Blue | End users interacting via HTTP | Mobile App, Web Browser, Swagger UI |
| 🔒 Security | Red | Authentication & authorization | JWT Filter, Role Check, CORS |
| 🌐 API | Teal | RESTful endpoint exposure | 9 Controllers |
| ⚙️ Service | Blue | Core business logic & orchestration | Matching Engine, ATS Workflow, PDF Parser |
| 💾 Repository | Green | Data access via Spring Data JPA | 7 Repositories with custom queries |
| 🗄️ Database | Yellow | Persistent storage | MySQL 8 with indexes & constraints |

---

## 🔄 Application Lifecycle

```mermaid
sequenceDiagram
    participant C as 👤 Candidate
    participant UI as 🌐 HTTP Client
    participant A as 🎮 ApplicationController
    participant S as ⚙️ ApplicationService
    participant R as 📄 ResumeParser
    participant M as 🧠 Matching Engine
    participant DB as 🗄️ Database

    Note over C,DB: Step 1: Apply for a Job

    C->>UI: POST /api/jobs/{jobId}/apply
    UI->>A: Forward Request
    A->>S: createApplication()

    Note over S: Step 2: Validate

    S->>S: Check job is OPEN
    S->>S: Check no duplicate application
    S->>S: Check resume exists

    Note over S: Step 3: Parse Resume

    S->>R: extractSkills(jobRequirements)
    S->>R: extractSkills(resumeText)
    R-->>S: candidateSkills + jobSkills

    Note over S: Step 4: Match & Score

    S->>M: analyze(jobSkills, candidateSkills)
    M->>M: Expand synonyms via Dictionary
    M->>M: Calculate match score
    M->>M: Determine importance level
    M-->>S: AnalysisResult (score, matchLevel, missingSkills + importance)

    Note over S: Step 5: Persist

    S->>DB: Save Application (with score)
    DB-->>S: ✅ Application saved

    S-->>A: ApplicationResponse
    A-->>UI: 201 Created
    UI-->>C: Application Submitted (Score: 82%)
```

### ATS State Machine

```mermaid
flowchart TD
    PENDING -->|Approve| APPROVED
    PENDING -->|Reject| REJECTED
    APPROVED -->|Schedule| INTERVIEW
    APPROVED -->|Reject| REJECTED
    INTERVIEW -->|Pass| OFFER
    INTERVIEW -->|Fail| REJECTED
    OFFER -->|Accept| HIRED
    OFFER -->|Decline| REJECTED

    classDef pending fill:#ffeaa7,stroke:#fdcb6e
    classDef active fill:#74b9ff,stroke:#0984e3
    classDef success fill:#55efc4,stroke:#00b894
    classDef fail fill:#ff7675,stroke:#d63031
    classDef terminal fill:#dfe6e9,stroke:#636e72

    class PENDING pending
    class APPROVED,INTERVIEW,OFFER active
    class HIRED success
    class REJECTED fail
```

---

## 🗄️ Database Schema

### Tables Overview

| Table | Key Columns | Purpose |
|-------|-------------|---------|
| `users` | email (unique), password, full_name, role, resume_url | User accounts (EMPLOYER / CANDIDATE) |
| `jobs` | title, description, requirements, location, salary, status, employer_id, company_id | Job postings |
| `applications` | job_id + candidate_id (unique), status, score, match_level, missing_skills | Job applications |
| `resume_profiles` | candidate_id, full_name, email, phone, skills, education, experience, raw_text | Parsed CV data |
| `companies` | name, industry, size, description, website, location, owner_id | Company profiles |
| `interviews` | application_id, candidate_id, interviewer_id, scheduled_at, status, type, rating | Interview scheduling |
| `application_histories` | application_id, from_status, to_status, notes, changed_by | Audit trail |

### Indexes

| Table | Index | Purpose |
|-------|-------|---------|
| `applications` | `job_id + candidate_id` | Unique constraint (no duplicate apply) |
| `applications` | `status` | Filter applications by status |
| `applications` | `applied_at` | Sort by date |
| `applications` | `application_score` | Candidate ranking |

---

## 🧩 Core Modules

### 1. 📄 Resume Intelligence

```
CV Upload → PDFBox Extract → Regex Parsing → Skill Dictionary → ResumeProfile
```

- Apache PDFBox 2.0.30 for text extraction
- Regex patterns for email, phone, education, experience
- Skill Dictionary (JSON) with 45+ skills across 7 categories
- Synonym resolution (e.g., "core java" → "Java")

### 2. 🎯 Smart Matching + Candidate Ranking

- Rule-based matching engine (no ML/LLM)
- Skill dictionary expansion (canonical + synonyms)
- Score = `(matched / required) × 100`
- Match levels: HIGH (≥80%), MEDIUM (≥50%), LOW (<50%)
- Candidates sorted by score descending

### 3. 🔄 ATS Workflow (10-State Machine)

```
PENDING → SCREENING → SHORTLISTED → TECHNICAL_REVIEW → INTERVIEW
    ↓          ↓            ↓               ↓               ↓
REJECTED ←─────┴────────────┴───────────────┴───────────────┘
                                      ↓
                              HR_INTERVIEW → OFFER → HIRED
                                   ↓          ↓
                               REJECTED ←────┘
                               
WITHDRAWN (terminal)
```

- Every transition validated by `StatusTransitionValidator`
- Full audit trail in `application_histories` table
- Employer notes captured per transition

### 4. 📊 Employer Dashboard

Returns aggregated analytics for the employer:

```json
{
  "openJobs": 8,
  "totalApplications": 142,
  "scheduledInterviews": 17,
  "hiredCount": 3,
  "topCandidates": [
    { "candidateName": "Ahmed", "jobTitle": "Backend Developer", "score": 92 }
  ]
}
```

### 5. 🏢 Company Profiles

- Separate `companies` entity (decoupled from User)
- Company size enum: STARTUP, SMALL, MEDIUM, LARGE, ENTERPRISE
- Verification support for trusted employers

### 6. 🗓️ Interview Management

| Feature | Description |
|---------|-------------|
| Schedule | Select application, type (TECHNICAL/HR/ONSITE), platform |
| Feedback | Rating 1-5, result (PASSED/FAILED/ON_HOLD), notes |
| Lifecycle | SCHEDULED → COMPLETED / CANCELLED / NO_SHOW |
| Security | Only the assigned interviewer can give feedback |

### 7. 📈 Skill Gap Analysis (with Importance)

```
matchedSkills:   ["Java", "Spring Boot", "MySQL"]       ← موجود
missingSkills:   ["Docker" (HIGH), "AWS" (HIGH)]         ← ناقص مع الأهمية
extraSkills:     ["Python"]                               ← إضافي
```

- Importance derived from skill category:
  - **HIGH:** BACKEND, DATABASE, DEVOPS, CLOUD, ARCHITECTURE
  - **MEDIUM:** FRONTEND, AI, TESTING
  - **LOW:** PROCESS, OTHER

---

## 🛡️ Security

### Auth Flow

```
Client                     Backend                        DB
  │                          │                           │
  │  POST /api/auth/login    │                           │
  │  { email, password }     │                           │
  │ ────────────────────────▶│                           │
  │                          │  Find user → BCrypt check │
  │                          │  Generate JWT + Refresh   │
  │ ◀────────────────────────│                           │
  │ { accessToken, refreshToken }                        │
  │                          │                           │
  │  POST /api/jobs          │                           │
  │  Authorization: Bearer   │                           │
  │ ────────────────────────▶│                           │
  │                          │  JwtAuthenticationFilter: │
  │                          │  1. Extract JWT           │
  │                          │  2. Validate signature    │
  │                          │  3. Extract roles         │
  │                          │  4. @PreAuthorize check   │
  │ ◀────────────────────────│                           │
```

### Security Components

| Component | Type | Responsibility |
|-----------|------|----------------|
| `JwtTokenProvider` | Utility | Generate, parse, validate JWT tokens |
| `JwtAuthenticationFilter` | OncePerRequestFilter | Extract JWT from `Authorization: Bearer` |
| `JwtAuthenticationEntryPoint` | AuthenticationEntryPoint | Return 401 JSON for unauthenticated |
| `UserDetailsServiceImpl` | UserDetailsService | Load user by email |

### Roles

| Role | Permissions |
|------|-------------|
| 🟢 `CANDIDATE` | Own profile, apply to jobs, upload CV, skill gap, own applications |
| 🔵 `EMPLOYER` | Create/manage jobs, review applicants, interview management, dashboard |

---

## 📡 API Endpoints

### 🔓 Public

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user (CANDIDATE or EMPLOYER) |
| POST | `/api/auth/login` | Login → JWT + refresh token |
| POST | `/api/auth/refresh` | Refresh expired access token |
| GET | `/api/jobs` | Search open jobs (paginated, filterable) |
| GET | `/api/jobs/{id}` | Get job details |
| GET | `/api/companies` | Search company profiles |
| GET | `/api/companies/{id}` | Get company details |

### 🔐 Authenticated — CANDIDATE

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users/me` | Get profile |
| PUT | `/api/users/me` | Update profile |
| POST | `/api/files/resume` | Upload CV |
| GET | `/api/resumes/me` | Get parsed resume |
| POST | `/api/resumes/me/refresh` | Re-parse CV |
| POST | `/api/jobs/{jobId}/apply` | Apply to job |
| GET | `/api/applications/me` | My applications |
| GET | `/api/applications/me/{id}` | Application detail |
| GET | `/api/skill-gap?jobId=X` | Skill gap analysis |
| GET | `/api/interviews/mine` | My interviews |

### 🔐 Authenticated — EMPLOYER

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/jobs` | Create job |
| PUT | `/api/jobs/{id}` | Update job |
| PATCH | `/api/jobs/{id}/close` | Close job |
| DELETE | `/api/jobs/{id}` | Delete job |
| GET | `/api/jobs/mine` | My jobs |
| GET | `/api/jobs/{jobId}/applications` | Ranked applicants |
| GET | `/api/applications` | All applications |
| PATCH | `/api/applications/{id}/status` | Update status |
| GET | `/api/dashboard/employer` | Analytics dashboard |
| POST | `/api/companies` | Create company |
| PUT | `/api/companies/{id}` | Update company |
| POST | `/api/interviews` | Schedule interview |
| GET | `/api/interviews?applicationId=X` | Interviews for application |
| PATCH | `/api/interviews/{id}/feedback` | Give feedback |
| PATCH | `/api/interviews/{id}/cancel` | Cancel interview |

---

## ⚙️ Matching Algorithm

```
Input:  jobSkills = {Java, Spring Boot, Docker, AWS, MySQL}
        candidateSkills = {Java, Spring Boot, Python, MySQL}

1. Expand via Skill Dictionary (add synonyms)
2. Match = {Java, Spring Boot, MySQL}               → 3 skills
3. Missing = {Docker (HIGH), AWS (HIGH)}             → 2 skills
4. Score = (3 × 100) / 5 = 60%                      → MATCH Level: MEDIUM
5. Extra = {Python}                                  → candidate advantage
```

> 🔒 **Rule-based engine** — no ML, no LLM dependencies. Pure dictionary matching + weighted scoring with importance levels per skill category.

---

## 🚀 Quick Start

### Prerequisites
- Java 21, MySQL 8, Maven (or `mvnw`)

### Database
```sql
CREATE DATABASE job_board;
CREATE USER 'dev_user'@'localhost' IDENTIFIED BY 'Dev@2026#';
GRANT ALL ON job_board.* TO 'dev_user'@'localhost';
```

### Configure
Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/job_board
spring.datasource.username=dev_user
spring.datasource.password=Dev@2026#
```

### Run
```bash
./mvnw spring-boot:run
```
Server → `http://localhost:8080`  
Swagger → `http://localhost:8080/swagger-ui/index.html`

### Tests
```bash
./mvnw test
```

---

## 📁 Project Structure

```
src/main/java/com/jobboard/
├── config/          # Security / JWT configuration
├── controller/      # 9 REST controllers
├── dto/             # 20+ request/response DTOs
├── entity/          # 7 JPA entities
├── enums/           # 10 enums
├── exception/       # 7 exceptions + GlobalExceptionHandler
├── repository/      # 8 Spring Data JPA repos
├── security/        # JWT, auth filter, entry point
├── service/         # 14 services + 2 mappers
└── specification/   # JPA specification for job search
```

---

## 🧪 Testing

| Layer | Strategy |
|-------|----------|
| 🎮 Controller | @WebMvcTest + MockMvc |
| ⚙️ Service | Mockito + JUnit 5 |
| 📦 Repository | @DataJpaTest (H2) |
| 🏗️ Context | @SpringBootTest |

---

## 🔧 Configuration

| Variable | Default | Description |
|----------|---------|-------------|
| `DB_URL` | `jdbc:mysql://localhost:3306/job_board` | JDBC URL |
| `DB_USERNAME` | `root` | Database user |
| `DB_PASSWORD` | `password` | Database password |
| `JWT_SECRET` | Base64-encoded key | JWT signing secret |
| `JWT_EXPIRATION` | `86400000` (24h) | Access token expiry |
| `JWT_REFRESH_EXPIRATION` | `604800000` (7d) | Refresh token expiry |

---

## 🧠 Engineering Highlights

| Practice | Implementation |
|----------|----------------|
| 🏗️ Layered architecture | Controller → Service → Repository → DB |
| 🔒 Security | JWT stateless auth + role-based method security |
| 📦 DTO pattern | Request/Response DTOs — no entity exposure |
| 🚦 State machine | 10-status ATS workflow with validated transitions |
| 📊 Ranking | Score-based candidate ordering with `NULLS LAST` |
| 🔍 Smart matching | Rule-based engine with synonym expansion |
| 📈 Skill importance | Category-derived weighting (HIGH/MEDIUM/LOW) |
| 🧾 Audit trail | Full application history tracking |
| 🗄️ Soft delete | `@Where` for logical deletion support |
| ⚠️ Centralized errors | `@RestControllerAdvice` global handler |

---

## 👤 Author

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/MahmoudYoussef-web">
        <img src="https://github.com/MahmoudYoussef-web.png" width="100" height="100" style="border-radius:50%;"/>
      </a>
    </td>
    <td>
      <b>Mahmoud Youssef</b><br/>
      <sub>Backend Engineer — Spring Boot · Java · REST APIs</sub><br/><br/>
      <a href="https://github.com/MahmoudYoussef-web">
        <img src="https://img.shields.io/badge/GitHub-MahmoudYoussef--web-181717?style=for-the-badge&logo=github"/>
      </a>
      &nbsp;
      <a href="https://www.linkedin.com/in/mahmoud-youssef-dev/">
        <img src="https://img.shields.io/badge/LinkedIn-mahmoud--youssef--dev-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white"/>
      </a>
    </td>
  </tr>
</table>
