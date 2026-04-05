# EduLearn Spring Web - Quick Start (VI/EN)

Tai lieu onboarding 1 trang cho dev moi. | One-page onboarding guide for new developers.

Xem tai lieu ky thuat chi tiet: `README_FULL.md`.

## 1) Prerequisites

- JDK 17
- MySQL 8+ (tao schema `edulearn`) | create `edulearn` schema
- Redis 6+
- (Tuy chon / Optional) SMTP local: MailHog/Mailpit

Ghi chu / Note:

- App dung `ddl-auto=validate`, can schema hop le truoc khi start. | App uses `ddl-auto=validate`, so schema must exist before startup.
- SQL schema da co tai `database/database_edulearn.sql`. | SQL schema is available at `database/database_edulearn.sql`.

## 2) Setup nhanh (env vars) | Quick env setup

Ban co the chay voi default values, hoac set cac bien chinh sau. | You can run with defaults, or set these key variables.

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET`
- `REDIS_HOST`, `REDIS_PORT`
- `APP_EMAIL_ENABLED` (default `false`)
- `ANTHROPIC_ENABLED`, `ANTHROPIC_API_KEY` (default chatbot mock mode)

PowerShell example:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/edulearn?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="root"
$env:JWT_SECRET="replace-with-your-strong-secret-at-least-32-bytes"
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
```

## 3) Run app

```powershell
cd E:\Profile\edulearn\spring-web
.\mvnw.cmd spring-boot:run
```

Run with dev profile:

```powershell
cd E:\Profile\edulearn\spring-web
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Default URL: `http://localhost:8080`

## 4) Run tests

```powershell
cd E:\Profile\edulearn\spring-web
.\mvnw.cmd test
```

Integration tests tieu bieu / Representative integration tests:

- `src/test/java/com/edulearn/auth/AuthControllerIntegrationTest.java`
- `src/test/java/com/edulearn/course/CoursePublishFlowIntegrationTest.java`
- `src/test/java/com/edulearn/course/EnrollmentLearningFlowIntegrationTest.java`
- `src/test/java/com/edulearn/quiz/QuizReviewIntegrationTest.java`
- `src/test/java/com/edulearn/chatbot/ChatControllerIntegrationTest.java`

## 5) Endpoint can biet ngay | Key endpoints

Public:

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `GET /api/v1/courses`
- `GET /api/v1/courses/{courseId}`
- `GET /api/v1/courses/{courseId}/sections`
- `GET /api/v1/sections/{sectionId}/lessons`
- `GET /api/v1/lessons/{lessonId}`

JWT required:

- `GET /api/v1/auth/me`
- `POST /api/v1/courses/{courseId}/enrollments`
- `POST /api/v1/quizzes/{quizId}/submit`
- `POST /api/v1/chat/ask`
- `GET /api/v1/admin/dashboard`

Health:

- `GET /actuator/health`

## 6) Role matrix (tom tat) | Role matrix (summary)

- `ADMIN`: full access + `GET /api/v1/admin/dashboard`
- `INSTRUCTOR`: CRUD course content + publish/archive + quiz management
- `STUDENT`: enroll, learning progress, quiz, review, order, chatbot

## 7) Troubleshooting nhanh | Quick troubleshooting

- DB validate startup error:
  - Chay schema trong `database/database_edulearn.sql`. | Run schema from `database/database_edulearn.sql`.
- Redis connection error:
  - Kiem tra Redis service va `REDIS_HOST`/`REDIS_PORT`. | Check Redis service and `REDIS_HOST`/`REDIS_PORT`.
- 401/403 on protected API:
  - Login lai de lay access token moi; gui `Authorization: Bearer <token>`.
  - Verify token role matches endpoint permission.
- Claude API khong chay that / Claude not called:
  - Set `ANTHROPIC_ENABLED=true` and `ANTHROPIC_API_KEY`.

---

Thu tu de chay nhanh / Fastest path:
**Import DB schema -> Start Redis -> Run app -> Login -> Call module APIs**.
