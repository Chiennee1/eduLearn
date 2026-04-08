# EduLearn Spring Web - Full Technical README

Tai lieu ky thuat chi tiet cho backend `spring-web`.

- Quick start 1 trang: xem `README.md`
- Tai lieu nay: kien truc, module map, security, data model, config, test, deploy notes

## 1. Project scope

Backend REST API cho nen tang hoc truc tuyen, gom cac domain:

- Auth + JWT + Refresh token
- Course content management (category/course/section/lesson)
- Enrollment + learning progress + certificate
- Quiz engine + quiz history filter/pagination
- Review/rating + like/unlike + course stats
- Payment order checkout (instant completion flow)
- AI chatbot (conversation/message + Anthropic integration + SSE stream)
- Admin dashboard statistics

## 2. Tech stack

Tu `pom.xml`:

- Java 17
- Spring Boot `3.5.13`
- Spring Web, Validation, Security, Data JPA
- Spring Cache + Redis Data + Spring Session Redis
- Spring AOP
- Spring Mail
- JWT: `io.jsonwebtoken` (`jjwt`)
- MySQL runtime, H2 test
- Lombok
- Maven Wrapper (`mvnw`, `mvnw.cmd`)

## 3. High-level architecture

Codebase follow layered architecture:

- `controller`: HTTP entrypoint + request validation + response wrapper
- `service`: business logic, permission checks, transactions
- `repository`: Spring Data JPA access layer
- `entity`: domain model + JPA mapping
- `dto`: API request/response contracts
- `config/common/exception`: cross-cutting concerns

Cross-cutting features:

- Stateless JWT security via `JwtAuthFilter`
- Method-level authorization via `@PreAuthorize`
- Transaction boundary via `@Transactional`
- Redis cache for published course list
- AOP execution-time logging for service/controller methods
- Domain event + `@TransactionalEventListener(AFTER_COMMIT)` for enrollment welcome email

## 4. Source layout

Main source: `src/main/java/com/edulearn`

- `auth`
- `course`
- `quiz`
- `review`
- `payment`
- `chatbot`
- `admin`
- `config`
- `common`
- `exception`

Entry point:

- `src/main/java/com/edulearn/EduLearnApplication.java`

## 5. API conventions

- Base prefix: `/api/v1` (`Constants.API_V1_PREFIX`)
- Standard response envelope: `ApiResponse<T>`
- Pagination envelope: `PageResponse<T>`
- Validation/business errors handled by `GlobalExceptionHandler`

Response format (typical):

```json
{
  "success": true,
  "message": "...",
  "data": {},
  "timestamp": "2026-04-06T...Z"
}
```

## 6. Security model

### 6.1 Roles

Enum `RoleName`:

- `ADMIN`
- `INSTRUCTOR`
- `STUDENT`

### 6.2 AuthN/AuthZ

- JWT Bearer token for protected APIs
- Stateless session (`SessionCreationPolicy.STATELESS`)
- Public endpoints configured in `SecurityConfig`
- Fine-grained authorization in controllers/services via role + ownership checks

### 6.3 Public endpoints (from current `SecurityConfig`)

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /actuator/health`
- GET APIs for category/course/section/lesson paths

Note: ngoai allow-list tren, endpoint can auth token hop le.

## 7. Module map (controllers + endpoint groups)

### 7.1 Auth (`auth`)

Controller: `src/main/java/com/edulearn/auth/controller/AuthController.java`

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `POST /api/v1/auth/logout`
- `GET /api/v1/auth/me`

### 7.2 Course content (`course`)

Controllers:

- `CategoryController`
- `CourseController`
- `SectionController`
- `LessonController`

Key groups:

- Category CRUD: `/api/v1/categories`
- Course CRUD + publish/archive: `/api/v1/courses`
- Section CRUD under course: `/api/v1/courses/{courseId}/sections`
- Lesson CRUD: `/api/v1/sections/{sectionId}/lessons`, `/api/v1/lessons/{lessonId}`

Authorization summary:

- GET public
- Write operations require `INSTRUCTOR` or `ADMIN`
- Service layer enforces ownership for instructor-owned resources

### 7.3 Enrollment & learning (`course`)

Controllers:

- `EnrollmentController`
- `LearningDashboardController`

Endpoints:

- Enroll: `POST /api/v1/courses/{courseId}/enrollments`
- My enrollments: `GET /api/v1/enrollments/me`
- Progress list: `GET /api/v1/enrollments/{enrollmentId}/progress`
- Progress update: `PATCH /api/v1/enrollments/{enrollmentId}/lessons/{lessonId}/progress`
- Certificate: `GET /api/v1/enrollments/{enrollmentId}/certificate`
- Dashboard (facade): `GET /api/v1/learning/dashboard`

Authorization summary:

- Student-only endpoints (`hasRole('STUDENT')`)
- Enrollment flow creates progress records in same transaction
- Enrollment triggers application event for welcome email

### 7.4 Quiz (`quiz`)

Controller: `src/main/java/com/edulearn/quiz/controller/QuizController.java`

- Create/update/delete quiz: instructor/admin
- Update/delete question and option: instructor/admin
- Submit quiz: student
- Quiz history: student + filters + pagination/sort whitelist
- Get quiz by id: available for learning flow

Key feature notes:

- Auto-grade on submit
- Attempt + answer persistence
- History supports spec filter (`quizId`, `passed`, date range)
- Sort allow-list validated in controller

### 7.5 Review (`review`)

Controller: `src/main/java/com/edulearn/review/controller/ReviewController.java`

- List reviews by course (public, with pagination/sort)
- Create/update own review (student)
- Delete own review (student)
- Like/unlike review (student)

Key feature notes:

- Transactional review write flow
- Course stats recalculation (`avg_rating`, `total_reviews`) via service layer
- Sort allow-list validated in controller

### 7.6 Payment (`payment`)

Controller: `src/main/java/com/edulearn/payment/controller/OrderController.java`

- Checkout order + enrollment: `POST /api/v1/courses/{courseId}/orders`
- My orders: `GET /api/v1/orders/me`

Key feature notes:

- `PaymentOrderService.checkoutEnrollment(...)` wraps order + payment-complete + enroll
- Uses transaction boundary in service

### 7.7 Admin (`admin`)

Controller: `src/main/java/com/edulearn/admin/controller/AdminDashboardController.java`

- `GET /api/v1/admin/dashboard` (`ADMIN` only)

Statistics include users, roles, courses, enrollments, orders, reviews, revenue.

### 7.8 Chatbot (`chatbot`)

Controller: `src/main/java/com/edulearn/chatbot/controller/ChatController.java`

- Ask: `POST /api/v1/chat/ask`
- Conversation list: `GET /api/v1/chat/conversations`
- Conversation messages: `GET /api/v1/chat/conversations/{conversationId}/messages`
- Streaming: `GET /api/v1/chat/stream` (SSE)

Key feature notes:

- Stores conversation + messages in DB
- Supports optional course-context prompt
- Ownership checks on conversation access
- Rate limit per user window (in-memory)
- Anthropic client supports timeout and error mapping

## 8. Data model overview

SQL schema file:

- `database/database_edulearn.sql`

Core tables used by current implemented modules include:

- Auth/User: `users`, `roles`, `user_roles`, `refresh_tokens`
- Course: `categories`, `courses`, `course_categories`, `sections`, `lessons`
- Learning: `enrollments`, `lesson_progress`, `course_certificates`
- Quiz: `quizzes`, `questions`, `answer_options`, `quiz_attempts`, `quiz_answers`
- Review: `reviews`, `review_likes`, `course_stats`
- Payment: `orders`, `order_items`
- Chatbot: `chat_conversations`, `chat_messages`

Schema file also contains some tables/views that can be future-ready depending on code usage (example: `assignments`, `lesson_resources`, views).

## 9. Caching, performance, observability

### 9.1 Redis cache

- Enabled globally via `@EnableCaching`
- Cache config class: `src/main/java/com/edulearn/config/RedisCacheConfig.java`
- Main cache keyspace: `courses:published`
- TTL: 5 minutes
- `CourseService.getPublishedCourses()` -> `@Cacheable`
- Course write/publish/archive/delete -> `@CacheEvict`

### 9.2 Execution-time logging (AOP)

- Aspect: `src/main/java/com/edulearn/common/aop/ExecutionTimeLoggingAspect.java`
- Logs execution time for service/controller methods
- Config via `app.logging.execution.*`

## 10. Configuration and profiles

Files:

- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-prod.yml`
- `src/test/resources/application-test.yml`

Important properties:

- DB: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- JWT: `JWT_SECRET`, `JWT_ACCESS_MINUTES`, `JWT_REFRESH_DAYS`, `JWT_ISSUER`
- Redis: `REDIS_HOST`, `REDIS_PORT`, `REDIS_TIMEOUT`
- Mail: `APP_EMAIL_ENABLED`, `APP_EMAIL_FROM`, `MAIL_*`
- Anthropic: `ANTHROPIC_ENABLED`, `ANTHROPIC_API_KEY`, `ANTHROPIC_MODEL`, timeout settings
- Chat rate limit: `CHAT_RATE_LIMIT_MAX_REQUESTS`, `CHAT_RATE_LIMIT_WINDOW_SECONDS`
- Execution log: `EXECUTION_LOG_ENABLED`, `EXECUTION_LOG_WARN_THRESHOLD_MS`

Profile notes:

- `dev`: SQL/security logs more verbose
- `prod`: quieter log levels
- `test`: H2 in-memory, cache simple, anthropic/email disabled

## 11. Local setup and run

### 11.1 Prepare DB and infra

1. Start MySQL and create/import schema:

```sql
SOURCE database/database_edulearn.sql;
```

2. Start Redis on `localhost:6379` (or update env vars)

### 11.2 Run application

```powershell
cd E:\Profile\edulearn\spring-web
.\mvnw.cmd spring-boot:run
```

Run with `dev` profile:

```powershell
cd E:\Profile\edulearn\spring-web
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

### 11.3 Build package

```powershell
cd E:\Profile\edulearn\spring-web
.\mvnw.cmd clean package -DskipTests
```

## 12. Testing strategy

Run all tests:

```powershell
cd E:\Profile\edulearn\spring-web
.\mvnw.cmd test
```

Representative integration suites:

- `src/test/java/com/edulearn/auth/AuthControllerIntegrationTest.java`
- `src/test/java/com/edulearn/course/CoursePublishFlowIntegrationTest.java`
- `src/test/java/com/edulearn/course/EnrollmentLearningFlowIntegrationTest.java`
- `src/test/java/com/edulearn/course/CourseCachingIntegrationTest.java`
- `src/test/java/com/edulearn/payment/PaymentOrderAndAdminDashboardIntegrationTest.java`
- `src/test/java/com/edulearn/quiz/QuizReviewIntegrationTest.java`
- `src/test/java/com/edulearn/chatbot/ChatControllerIntegrationTest.java`

## 13. Deployment notes

Minimum runtime dependencies:

- Java 17 runtime
- MySQL with schema aligned to entities
- Redis service
- Optional SMTP service

Production hardening checklist (recommended):

- Set strong `JWT_SECRET` and rotate periodically
- Restrict CORS origins in `SecurityConfig`
- Externalize secrets via environment/secret manager
- Add centralized logging + metrics/monitoring
- Add DB migration tool (Flyway/Liquibase) for schema versioning

## 14. Known assumptions

- Tai lieu nay duoc tong hop tu source code hien tai trong workspace.
- Neu endpoint/business rule thay doi, cap nhat dong bo tai lieu trong `README.md` va `README_FULL.md`.

