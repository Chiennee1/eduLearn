# EduLearn Backend (Spring Boot) - Quick Start (VI/EN)

Backend REST API for an e-learning platform with auth, course management, enrollment, quiz, review, payment, chatbot, and admin analytics.

Backend REST API cho nen tang hoc truc tuyen, bao gom auth, quan ly khoa hoc, enrollment, quiz, review, payment, chatbot, va admin analytics.

> Full technical documentation: `README_FULL.md`

## Table of Contents

- [Project Overview](#project-overview)
- [Core Features](#core-features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Quick Start](#quick-start)
- [Run Tests](#run-tests)
- [API Snapshot](#api-snapshot)
- [Role Matrix](#role-matrix)
- [Troubleshooting](#troubleshooting)
- [Documentation](#documentation)

## Project Overview

- VI: Du an `spring-web` xay dung theo layered architecture (`controller -> service -> repository -> entity`).
- EN: `spring-web` follows a layered architecture (`controller -> service -> repository -> entity`).
- VI: API base path hien tai la `/api/v1`.
- EN: Current API base path is `/api/v1`.

## Core Features

- VI: Auth + JWT + refresh token + role-based authorization (`ADMIN`, `INSTRUCTOR`, `STUDENT`).
- EN: Auth + JWT + refresh token + role-based authorization (`ADMIN`, `INSTRUCTOR`, `STUDENT`).
- VI: Ho tro dang nhap social OAuth2 (Google/GitHub), callback tra access token + refresh token ve Frontend.
- EN: Supports OAuth2 social login (Google/GitHub) with callback that returns access + refresh tokens to Frontend.
- VI: Course module day du (`category/course/section/lesson`) + publish flow.
- EN: Full course module (`category/course/section/lesson`) + publish flow.
- VI: Enrollment + learning progress + certificate generation.
- EN: Enrollment + learning progress + certificate generation.
- VI: Quiz engine (question/option), auto-grade, quiz history filter.
- EN: Quiz engine (question/option), auto-grade, quiz history filtering.
- VI: Review/rating + like/unlike + course stats update.
- EN: Review/rating + like/unlike + course stats recalculation.
- VI: Payment order flow, Redis caching, chatbot (Anthropic integration + conversation history), admin dashboard.
- EN: Payment order flow, Redis caching, chatbot (Anthropic integration + conversation history), admin dashboard.

## Tech Stack

- Java 17
- Spring Boot 3.5.13
- Spring Web, Validation, Security, Data JPA
- Spring Cache + Redis + Spring Session Redis
- Spring AOP, Spring Mail
- MySQL (runtime), H2 (test)
- Maven Wrapper (`mvnw`, `mvnw.cmd`)

## Project Structure

Main source: `src/main/java/com/edulearn`

- `auth` - authentication and token flows
- `course` - categories, courses, sections, lessons, enrollment, learning progress
- `quiz` - quiz management, submission, history
- `review` - rating/review and likes
- `payment` - order and checkout flow
- `chatbot` - AI conversation and messaging
- `admin` - admin dashboard/statistics
- `config`, `common`, `exception` - cross-cutting concerns

Entry point:

- `src/main/java/com/edulearn/EduLearnApplication.java`

## Quick Start

### 1) Prerequisites

- JDK 17
- MySQL 8+ (schema `edulearn`)
- Redis 6+
- Optional: MailHog/Mailpit for local SMTP

Note:

- VI: Ung dung dang dung `ddl-auto=validate`, can schema dung truoc khi startup.
- EN: App uses `ddl-auto=validate`, so schema must be valid before startup.
- SQL schema file: `database/database_edulearn.sql`

### 2) Prepare database

```sql
CREATE DATABASE edulearn CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE edulearn;
SOURCE database/database_edulearn.sql;
```

### 3) Configure environment variables (minimum)

- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`
- `JWT_SECRET`
- `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`
- `GITHUB_CLIENT_ID`, `GITHUB_CLIENT_SECRET`
- Optional callback targets: `APP_OAUTH2_REDIRECT_URI`, `APP_OAUTH2_FAILURE_URI`
- Optional Redis settings (only when enabling Redis cache/session): `REDIS_HOST`, `REDIS_PORT`, `CACHE_TYPE=redis`, `SPRING_SESSION_STORE_TYPE=redis`
- Optional: `APP_EMAIL_ENABLED`, `ANTHROPIC_ENABLED`, `ANTHROPIC_API_KEY`

PowerShell example:

```powershell
$env:DB_URL="jdbc:mysql://localhost:3306/edulearn?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=Asia/Ho_Chi_Minh"
$env:DB_USERNAME="root"
$env:DB_PASSWORD="root"
$env:JWT_SECRET="replace-with-your-strong-secret-at-least-32-bytes"
$env:GOOGLE_CLIENT_ID="your-google-client-id"
$env:GOOGLE_CLIENT_SECRET="your-google-client-secret"
$env:GITHUB_CLIENT_ID="your-github-client-id"
$env:GITHUB_CLIENT_SECRET="your-github-client-secret"
$env:APP_OAUTH2_REDIRECT_URI="http://localhost:5173/auth/social/callback"
```

Defaults in this project now:

- `CACHE_TYPE=simple` (no Redis required for cache)
- `SPRING_SESSION_STORE_TYPE=none` (OAuth2 session stored in container memory)

If you want full Redis-backed cache + session:

```powershell
$env:REDIS_HOST="localhost"
$env:REDIS_PORT="6379"
$env:CACHE_TYPE="redis"
$env:SPRING_SESSION_STORE_TYPE="redis"
```

### 4) Run application

```powershell
cd E:\Profile\edulearn\spring-web
.\mvnw.cmd spring-boot:run
```

Run with `dev` profile:

```powershell
cd E:\Profile\edulearn\spring-web
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.profiles=dev"
```

Default URL: `http://localhost:8080`

## Run Tests

```powershell
cd E:\Profile\edulearn\spring-web
.\mvnw.cmd test
```

Representative integration tests:

- `src/test/java/com/edulearn/auth/AuthControllerIntegrationTest.java`
- `src/test/java/com/edulearn/course/CoursePublishFlowIntegrationTest.java`
- `src/test/java/com/edulearn/course/EnrollmentLearningFlowIntegrationTest.java`
- `src/test/java/com/edulearn/quiz/QuizReviewIntegrationTest.java`
- `src/test/java/com/edulearn/chatbot/ChatControllerIntegrationTest.java`

## API Snapshot

Public endpoints:

- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/refresh`
- `GET /api/v1/courses`
- `GET /api/v1/courses/{courseId}`
- `GET /api/v1/courses/{courseId}/sections`
- `GET /api/v1/sections/{sectionId}/lessons`
- `GET /api/v1/lessons/{lessonId}`
- `GET /oauth2/authorization/google`
- `GET /oauth2/authorization/github`

JWT required (examples):

- `GET /api/v1/auth/me`
- `POST /api/v1/courses/{courseId}/enrollments`
- `POST /api/v1/quizzes/{quizId}/submit`
- `POST /api/v1/chat/ask`
- `GET /api/v1/admin/dashboard`

Health check:

- `GET /actuator/health`

## Role Matrix

- `ADMIN`: full platform access + admin dashboard
- `INSTRUCTOR`: manage own course content + publish/archive + quiz management
- `STUDENT`: enroll, track progress, submit quiz, write review, use chatbot

## Troubleshooting

- DB validation error on startup:
  - VI: Import lai schema tu `database/database_edulearn.sql`.
  - EN: Re-import schema from `database/database_edulearn.sql`.
- Redis connection error:
  - VI: Kiem tra Redis service va `REDIS_HOST`/`REDIS_PORT`.
  - EN: Verify Redis service and `REDIS_HOST`/`REDIS_PORT`.
- `401/403` on protected APIs:
  - VI: Dang nhap lai, gui `Authorization: Bearer <token>`, va kiem tra role.
  - EN: Re-login, send `Authorization: Bearer <token>`, and verify role permissions.
- Claude API not called:
  - VI: Bat `ANTHROPIC_ENABLED=true` va cung cap `ANTHROPIC_API_KEY`.
  - EN: Set `ANTHROPIC_ENABLED=true` and provide `ANTHROPIC_API_KEY`.

## Documentation

- Quick start: `README.md`
- Full technical guide: `README_FULL.md`
- SQL schema: `database/database_edulearn.sql`

---

Fast path:
**Import DB schema -> Start Redis -> Run app -> Login -> Call APIs**
