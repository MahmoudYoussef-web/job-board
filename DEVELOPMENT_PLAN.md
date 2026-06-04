# Job Board — خطة التطوير (Junior Level)

> مشروع تخرج — مش شغل حقيقي.
> لا Docker, لا Redis, لا Elasticsearch, لا CI/CD, لا Monitoring.
> مجرد **Business Logic + Architecture** قوية تثبت فهمك.

---

## الفلسفة

**القاعدة:** أي حاجة Junior عادي يسويها — أسويها. أي حاجة تحتاج DevOps أو Senior Engineer — أتخطاها.

| ما يهم | ما يهمش |
|--------|---------|
| Entity Design | Docker |
| Business Logic | Redis |
| State Machines | Elasticsearch |
| Algorithms (Matching) | Kubernetes |
| Clean Architecture | Monitoring |
| | CI/CD pipelines |
| | Rate Limiting |
| | Flyway migrations |
| | Production hardening |

---

## الـ Phases (بالترتيب)

```
Phase 1 : Resume Parsing ⭐⭐⭐
Phase 2 : Smart Matching + Ranking ⭐⭐⭐
Phase 3 : ATS Workflow ⭐⭐⭐
Phase 4 : Interview Management ⭐⭐
Phase 5 : Company Profiles ⭐⭐
Phase 6 : Skill Gap Analysis ⭐
```

---

# Phase 1: Resume Parsing

> استخراج Skills, Education, Experience من الـ PDF.
> لا AI. لا API. مجرد **PDFBox + Regex + Skill Dictionary**.

**الملفات:**

| الملف | نوعه |
|-------|------|
| `entity/ResumeProfile.java` | Entity جديد |
| `repository/ResumeProfileRepository.java` | Repository |
| `service/ResumeParserService.java` | Core Logic |
| `service/SkillDictionaryService.java` | Utility |
| `controller/ResumeController.java` | Controller |
| `resources/skills/skills-dictionary.json` | Dictionary |

## 1.1 Skill Dictionary

`src/main/resources/skills/skills-dictionary.json`

مهارات مصنفة مع synonyms. لكل مهارة اسم عرض وتصنيف ومُرادفات.

مثال:

```json
{
  "java": {
    "displayName": "Java",
    "category": "BACKEND",
    "synonyms": ["java", "core java", "java se", "java ee", "java 8", "java 11", "java 17", "j2ee"]
  },
  "spring-boot": {
    "displayName": "Spring Boot",
    "category": "BACKEND",
    "synonyms": ["spring boot", "springboot", "spring framework", "spring mvc", "spring data jpa", "spring security"]
  },
  "mysql": {
    "displayName": "MySQL",
    "category": "DATABASE",
    "synonyms": ["mysql", "my sql", "sql", "relational database", "rdbms", "database"]
  },
  "docker": {
    "displayName": "Docker",
    "category": "DEVOPS",
    "synonyms": ["docker", "container", "containerization", "docker compose"]
  },
  "react": {
    "displayName": "React",
    "category": "FRONTEND",
    "synonyms": ["react", "reactjs", "react.js", "react js", "react native"]
  },
  "mongodb": {
    "displayName": "MongoDB",
    "category": "DATABASE",
    "synonyms": ["mongodb", "mongo db", "mongo", "nosql", "no sql"]
  },
  "aws": {
    "displayName": "AWS",
    "category": "CLOUD",
    "synonyms": ["aws", "amazon web services", "ec2", "s3", "lambda", "cloud"]
  },
  "python": {
    "displayName": "Python",
    "category": "BACKEND",
    "synonyms": ["python", "python3", "python 3", "django", "flask"]
  },
  "javascript": {
    "displayName": "JavaScript",
    "category": "FRONTEND",
    "synonyms": ["javascript", "js", "ecmascript", "es6", "es2015"]
  },
  "typescript": {
    "displayName": "TypeScript",
    "category": "FRONTEND",
    "synonyms": ["typescript", "ts", "type script"]
  },
  "postgresql": {
    "displayName": "PostgreSQL",
    "category": "DATABASE",
    "synonyms": ["postgresql", "postgres", "postgre", "pg"]
  }
}
```

## 1.2 SkillDictionaryService

يقرأ الـ JSON file مرة عند بداية التطبيق ويخزن في Map.

```
load() → Map<String, SkillEntry>
getDisplayName(skillKey) → String
getSynonyms(skillKey) → List<String>
expandSkills(Set<String>) → Set<String>  // يرجع المهارات + كل synonyms
```

## 1.3 ResumeProfile Entity

```java
@Entity
@Table(name = "resume_profiles")
public class ResumeProfile {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private User candidate;

    @Column(name = "full_name", length = 150)
    private String fullName;

    @Column(length = 255)
    private String email;

    @Column(length = 30)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String skills;          // "java,spring-boot,mysql"

    @Column(columnDefinition = "TEXT")
    private String education;       // JSON array

    @Column(columnDefinition = "TEXT")
    private String experience;      // JSON array

    @Column(columnDefinition = "TEXT")
    private String projects;        // JSON array

    @Column(name = "raw_text", columnDefinition = "MEDIUMTEXT")
    private String rawText;         // النص الكامل من PDF

    @Column(name = "file_url", length = 512)
    private String fileUrl;

    @Column(name = "is_primary")
    private boolean isPrimary;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
```

## 1.4 ResumeParserService

```
parseAndSave(userId, filePath) → ResumeProfile
  1. استخراج النص من PDF (PDFBox)
  2. استخراج الإيميل (Regex)
  3. استخراج التلفون (Regex)
  4. استخراج المهارات (مقارنة مع Skill Dictionary)
  5. استخراج الخبرة (أنماط سنوات)
  6. استخراج التعليم (Bachelor, Master, ...)
  7. تخزين كل شيء في ResumeProfile
  8. رجوع الـ entity
```

## 1.5 تحديث Event Listener

الحين بعد رفع الـ CV، يستمع للحدث ويحلل.

```
ResumeUploadedEvent → ResumeUploadedListener → ResumeParserService.parseAndSave()
```

## 1.6 إزالة الشرط الخاطئ من ApplicationService

حذف السطرين:

```java
if (cv == null || cv.getScore() < 40) {
    throw new BusinessException("CV score too low");
}
```

التقديم مفتوح للجميع بغض النظر عن الـ CV Score.

**الوقت التقريبي:** 5 أيام

---

# Phase 2: Smart Matching + Candidate Ranking

> Match Score حقيقي + ترتيب المتقدمين. الـ Skill Dictionary يرفع دقة المطابقة من 50% لـ 90%.

**الملفات:**

| الملف | نوعه |
|-------|------|
| `service/SkillGapAnalysisService.java` | Core Logic |
| `dto/response/MatchResult.java` | Response DTO |

## 2.1 SkillGapAnalysisService

```
analyze(jobSkills, candidateSkills) → MatchResult

Algorithm:
  1. Expand jobSkills باستخدام الـ Dictionary
  2. Expand candidateSkills باستخدام الـ Dictionary
  3. matched = تقاطع المجموعتين
  4. missing = jobSkills - candidateSkills
  5. extra = candidateSkills - jobSkills
  6. score = (matched.size() * 100) / jobSkills.size()
  7. matchLevel = score >= 80 ? HIGH : score >= 50 ? MEDIUM : LOW
```

## 2.2 تحديث ApplicationService.apply()

بعد تخزين الـ application:

```java
ResumeProfile resume = resumeProfileRepository
    .findTopByCandidateIdAndIsPrimaryTrue(candidateId)
    .orElse(null);

if (resume != null) {
    Set<String> jobSkills = extractSkillsFromJob(job.getRequirements());
    Set<String> candidateSkills = parseSkills(resume.getSkills());

    MatchResult result = skillGapAnalysis.analyze(jobSkills, candidateSkills);

    application.setApplicationScore(result.getScore());
    application.setMatchLevel(result.getMatchLevel());
    application.setMissingSkills(String.join(",", result.getMissingSkills()));
}
```

## 2.3 Ranking في Employer View

لما صاحب العمل يشوف المتقدمين لوظيفة، يكون الترتيب تنازلي حسب الـ score.

`ApplicationRepository`:

```java
@Query("SELECT a FROM Application a WHERE a.job.id = :jobId ORDER BY a.applicationScore DESC NULLS LAST")
Page<Application> findByJobIdOrderByScoreDesc(@Param("jobId") Long jobId, Pageable pageable);
```

## 2.4 حقل missingSkills في Application

```java
@Column(name = "missing_skills", columnDefinition = "TEXT")
private String missingSkills;
```

**الوقت التقريبي:** 3 أيام

---

# Phase 3: ATS Workflow

> State Machine بـ 9 حالات مع History Tracking.

**الملفات:**

| الملف | نوعه |
|-------|------|
| `service/StatusTransitionValidator.java` | Validator |
| `entity/ApplicationHistory.java` | Entity |
| `repository/ApplicationHistoryRepository.java` | Repository |

## 3.1 ApplicationStatus الجديد

```
PENDING → SCREENING, REJECTED
SCREENING → SHORTLISTED, TECHNICAL_REVIEW, REJECTED
SHORTLISTED → TECHNICAL_REVIEW, INTERVIEW, REJECTED
TECHNICAL_REVIEW → INTERVIEW, HR_INTERVIEW, REJECTED
INTERVIEW → HR_INTERVIEW, OFFER, REJECTED
HR_INTERVIEW → OFFER, REJECTED
OFFER → HIRED, REJECTED
HIRED → (terminal)
REJECTED → (terminal)
WITHDRAWN → (terminal)
```

كل enum يحمل `Set<String> allowedTransitions` وطريقة `canTransitionTo()`.

## 3.2 StatusTransitionValidator

```java
public void validate(ApplicationStatus current, ApplicationStatus target) {
    if (!current.canTransitionTo(target)) {
        throw new InvalidStatusTransitionException(
            "Cannot transition from " + current + " to " + target);
    }
}
```

## 3.3 ApplicationHistory

لتتبع كل تغيير status:

```java
@Entity
@Table(name = "application_histories")
public class ApplicationHistory {
    @Id private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id")
    private Application application;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 20)
    private ApplicationStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 20)
    private ApplicationStatus toStatus;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "changed_by")
    private Long changedBy;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

## 3.4 تحديث ApplicationService.updateStatus()

```
oldStatus → validate(oldStatus → newStatus)
app.setStatus(newStatus)
historyRepository.save(ApplicationHistory(fromStatus, toStatus, notes, changedBy))
```

**الوقت التقريبي:** 3 أيام

---

# Phase 4: Interview Management

> Domain جديد بالكامل مع Business Logic.

**الملفات:**

| الملف | نوعه |
|-------|------|
| `entity/Interview.java` | Entity |
| `enums/InterviewStatus.java` | Enum |
| `enums/InterviewType.java` | Enum |
| `repository/InterviewRepository.java` | Repository |
| `service/InterviewService.java` | Service |
| `controller/InterviewController.java` | Controller |
| `dto/request/ScheduleInterviewRequest.java` | Request |
| `dto/request/InterviewFeedbackRequest.java` | Request |
| `dto/response/InterviewResponse.java` | Response |

## 4.1 Interview Entity

```
id, application, candidate, interviewer
scheduledAt, durationMinutes
meetingLink, meetingPlatform (ZOOM/TEAMS/GOOGLE_MEET/IN_PERSON/PHONE)
location (للحضوري)
status (SCHEDULED/COMPLETED/CANCELLED/RESCHEDULED/NO_SHOW)
type (TECHNICAL/HR/ONSITE/PHONE_SCREEN/FINAL)
feedback (TEXT)
rating (1-5)
result (PASSED/FAILED/ON_HOLD)
```

## 4.2 Business Logic

```
scheduleInterview:
  -application موجود؟
  -صاحب العمل يملك الوظيفة؟
  -الطلب مو PENDING ولا REJECTED؟

  → Interview(SCHEDULED)

giveFeedback:
  -المقابلة موجودة؟
  -حالتها SCHEDULED؟
  → COMPLETED + feedback + rating + result

cancelInterview:
  -المقابلة موجودة؟
  -حالتها SCHEDULED؟
  → CANCELLED
```

## 4.3 Endpoints

```
POST   /api/interviews                        → جدولة
GET    /api/interviews/mine                   → مقابلاتي (مرشح)
GET    /api/interviews?applicationId=X        → مقابلات طلب (صاحب عمل)
PUT    /api/interviews/{id}                   → تحديث موعد/لينك
PATCH  /api/interviews/{id}/feedback          → إضافة تقييم
PATCH  /api/interviews/{id}/cancel            → إلغاء
```

**الوقت التقريبي:** 3 أيام

---

# Phase 5: Company Profiles

> فصل Company عن User. كل شركة لها Entity مستقل.

**الملفات:**

| الملف | نوعه |
|-------|------|
| `entity/Company.java` | Entity |
| `enums/CompanySize.java` | Enum |
| `repository/CompanyRepository.java` | Repository |
| `service/CompanyService.java` | Service |
| `controller/CompanyController.java` | Controller |
| `dto/request/CompanyRequest.java` | Request |
| `dto/response/CompanyResponse.java` | Response |

## 5.1 Company Entity

```
id, name, industry, size (STARTUP/SMALL/MEDIUM/LARGE/ENTERPRISE)
description, website, logoUrl, location, foundedYear
isVerified, owner (User), createdAt, updatedAt
```

## 5.2 ربط مع Job

`Job.java`:
```
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "company_id")
private Company company;
```

الـ `employer` يبقى موجود (الـ User). الـ `company` هي المؤسسة.

## 5.3 Endpoints

```
GET  /api/companies           → عام (بحث)
GET  /api/companies/{id}      → عام (تفاصيل)
POST /api/companies           → EMPLOYER (إنشاء)
PUT  /api/companies/{id}      → Owner (تحديث)
GET  /api/companies/{id}/jobs → عام (وظائف الشركة)
```

**الوقت التقريبي:** 2 أيام

---

# Phase 6: Skill Gap Analysis

> نقاط ضعف المرشح + توصيات تعلم.

**الملفات:**

| الملف | نوعه |
|-------|------|
| `service/SkillGapService.java` | Service |
| `controller/SkillGapController.java` | Controller |
| `dto/response/SkillGapResponse.java` | Response |

## 6.1 SkillGapService

```
analyzeGap(candidateId, jobId) → SkillGapResponse

1. جيب ResumeProfile حق المرشح
2. جيب Job
3. استخرج المهارات من الوظيفة
4. استخرج المهارات من الـ CV
5. missing = jobSkills - candidateSkills
6. لكل مهارة ناقصة، حدد learning path:
   - "Docker" → "Docker Fundamentals"
   - "AWS" → "AWS Cloud Practitioner Essentials"
   - "Kafka" → "Apache Kafka Basics"
7. أرجع النتيجة
```

الـ Learning Path مجرد Map ثابتة في الـ Service:

```java
private static final Map<String, String> LEARNING_RESOURCES = Map.of(
    "docker", "Docker Fundamentals",
    "aws", "AWS Cloud Practitioner Essentials",
    "kafka", "Apache Kafka Basics",
    "kubernetes", "Kubernetes for Beginners",
    "react", "React - The Complete Guide",
    "spring-boot", "Spring Boot & Spring Cloud",
    ...
);
```

## 6.2 Endpoint

```
GET /api/skill-gap?jobId=5
```

لما المرشح يشوف وظيفة ويضغط "Skill Gap"، يشوف وش ينقصه عشان يتأهل.

**الوقت التقريبي:** يومين

---

# الجدول الزمني

```
Phase 1 : Resume Parsing         →  5 أيام  ⭐⭐⭐
Phase 2 : Smart Matching + Ranking →  3 أيام  ⭐⭐⭐
Phase 3 : ATS Workflow           →  3 أيام  ⭐⭐⭐
Phase 4 : Interview Management   →  3 أيام  ⭐⭐
Phase 5 : Company Profiles       →  2 أيام  ⭐⭐
Phase 6 : Skill Gap Analysis     →  2 أيام  ⭐
                               ─────────
                       Total ≈ 18 يوم
```

**18 يوم دوام كامل** — أو 5-6 أسابيع إذا تذاكر和她 تشتغل Part-Time.

---

# وش تستفيد في المقابلة؟

## الـ 3 Phases الأساسية (1 + 2 + 3) كافية تقول:

```
"I built a recruitment platform with:
- Resume parsing engine that extracts skills and experience
- Skill-based matching algorithm to rank candidates
- Full ATS pipeline with state machine and history tracking"
```

## لو كملت كل الـ 6:

```
"I built a recruitment platform with:
- Resume parsing engine
- Smart matching with skill gap analysis
- Multi-stage ATS workflow with state machine
- Interview management lifecycle
- Company profiles and job association
- Automated candidate ranking"
```

---

# ملاحظات مهمة للـ Junior

1. **لا تقارن نفسك بـ LinkedIn.** أنت تبني مشروع تخرج. الـ Business Logic هو الهدف.
2. **ما يحتاج AI.** Algorithm يكفي.
3. **كل Phase = Commit منفصل.** GitHub يوريك تطور.
4. **جرب كل Phase بـ Postman أو Swagger.** قبل لا تنتقل للي بعدها.
5. **Skill Dictionary هو أثمن ملف.** كلما كان أكبر، كان الـ Matching أذكى.
6. **لا تزود حاجة Senior.** احفظ الكود بسيط، مفهوم، ومباشر.
