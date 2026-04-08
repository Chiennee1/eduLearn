

create database edulearn
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
  
use edulearn;

create table users (
    id             BIGINT          NOT NULL AUTO_INCREMENT,
    email          VARCHAR(255)    NOT NULL,
    password_hash  VARCHAR(255)    NOT NULL,
    full_name      VARCHAR(100)    NOT NULL,
    avatar_url     VARCHAR(500)    NULL,
    status         ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL DEFAULT 'ACTIVE',
    email_verified BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE roles (
    id          INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL COMMENT 'ADMIN | INSTRUCTOR | STUDENT',
    description VARCHAR(255) NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE user_roles (
    user_id     BIGINT    NOT NULL,
    role_id     INT       NOT NULL,
    assigned_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE refresh_tokens (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(500) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    revoked    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT uq_refresh_tokens_token UNIQUE (token),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_refresh_tokens_user (user_id, revoked),
    INDEX idx_refresh_tokens_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE password_reset_tokens (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    token      VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP    NOT NULL,
    used       BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT uq_password_reset_token UNIQUE (token),
    CONSTRAINT fk_password_reset_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_prt_user (user_id, used)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE user_profiles (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    phone      VARCHAR(20)  NULL,
    bio        TEXT         NULL,
    website    VARCHAR(255) NULL,
    headline   VARCHAR(200) NULL COMMENT 'Eg: Senior Java Developer',
    location   VARCHAR(100) NULL,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_user_profiles PRIMARY KEY (id),
    CONSTRAINT uq_user_profiles_user UNIQUE (user_id),
    CONSTRAINT fk_user_profiles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


--  (hỗ trợ self-reference cho subcategory)
CREATE TABLE categories (
    id        INT          NOT NULL AUTO_INCREMENT,
    name      VARCHAR(100) NOT NULL,
    slug      VARCHAR(100) NOT NULL COMMENT 'URL-friendly name',
    parent_id INT          NULL COMMENT 'NULL = root category',
    icon_url  VARCHAR(255) NULL,
    CONSTRAINT pk_categories PRIMARY KEY (id),
    CONSTRAINT uq_categories_name UNIQUE (name),
    CONSTRAINT uq_categories_slug UNIQUE (slug),
    CONSTRAINT fk_categories_parent FOREIGN KEY (parent_id) REFERENCES categories(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE courses (
    id             BIGINT          NOT NULL AUTO_INCREMENT,
    instructor_id  BIGINT          NOT NULL,
    title          VARCHAR(255)    NOT NULL,
    slug           VARCHAR(255)    NULL COMMENT 'URL-friendly title',
    description    TEXT            NULL,
    thumbnail_url  VARCHAR(500)    NULL,
    price          DECIMAL(10, 2)  NOT NULL DEFAULT 0.00,
    level          ENUM('BEGINNER', 'INTERMEDIATE', 'ADVANCED') NOT NULL DEFAULT 'BEGINNER',
    status         ENUM('DRAFT', 'PUBLISHED', 'ARCHIVED')       NOT NULL DEFAULT 'DRAFT',
    language       VARCHAR(10)     NOT NULL DEFAULT 'vi',
    duration_hours INT             NOT NULL DEFAULT 0,
    published_at   TIMESTAMP       NULL,
    created_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_courses PRIMARY KEY (id),
    CONSTRAINT uq_courses_slug UNIQUE (slug),
    CONSTRAINT fk_courses_instructor FOREIGN KEY (instructor_id) REFERENCES users(id),
    INDEX idx_courses_instructor (instructor_id),
    INDEX idx_courses_status_date (status, published_at DESC),
    INDEX idx_courses_price (price),
    FULLTEXT INDEX idx_courses_search (title, description)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE course_categories (
    course_id   BIGINT NOT NULL,
    category_id INT    NOT NULL,
    CONSTRAINT pk_course_categories PRIMARY KEY (course_id, category_id),
    CONSTRAINT fk_cc_course   FOREIGN KEY (course_id)   REFERENCES courses(id)    ON DELETE CASCADE,
    CONSTRAINT fk_cc_category FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    INDEX idx_cc_category (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE sections (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    course_id   BIGINT       NOT NULL,
    title       VARCHAR(255) NOT NULL,
    order_index INT          NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_sections PRIMARY KEY (id),
    CONSTRAINT fk_sections_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_sections_course (course_id, order_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lessons (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    section_id       BIGINT       NOT NULL,
    title            VARCHAR(255) NOT NULL,
    type             ENUM('VIDEO', 'TEXT', 'QUIZ', 'ASSIGNMENT') NOT NULL DEFAULT 'VIDEO',
    content_url      VARCHAR(500) NULL COMMENT 'Video URL or text content path',
    duration_seconds INT          NOT NULL DEFAULT 0,
    is_preview       BOOLEAN      NOT NULL DEFAULT FALSE COMMENT 'Free preview without enrollment',
    order_index      INT          NOT NULL DEFAULT 0,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_lessons PRIMARY KEY (id),
    CONSTRAINT fk_lessons_section FOREIGN KEY (section_id) REFERENCES sections(id) ON DELETE CASCADE,
    INDEX idx_lessons_section (section_id, order_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lesson_resources (
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    lesson_id     BIGINT       NOT NULL,
    name          VARCHAR(255) NOT NULL,
    file_url      VARCHAR(500) NOT NULL,
    resource_type ENUM('PDF', 'ZIP', 'LINK', 'CODE', 'OTHER') NOT NULL DEFAULT 'OTHER',
    CONSTRAINT pk_lesson_resources PRIMARY KEY (id),
    CONSTRAINT fk_lr_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    INDEX idx_lr_lesson (lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE course_requirements (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    course_id   BIGINT       NOT NULL,
    description VARCHAR(500) NOT NULL,
    order_index INT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_course_requirements PRIMARY KEY (id),
    CONSTRAINT fk_cr_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_cr_course (course_id, order_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE course_outcomes (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    course_id   BIGINT       NOT NULL,
    description VARCHAR(500) NOT NULL,
    order_index INT          NOT NULL DEFAULT 0,
    CONSTRAINT pk_course_outcomes PRIMARY KEY (id),
    CONSTRAINT fk_co_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_co_course (course_id, order_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE enrollments (
    id           BIGINT         NOT NULL AUTO_INCREMENT,
    user_id      BIGINT         NOT NULL,
    course_id    BIGINT         NOT NULL,
    status       ENUM('ACTIVE', 'COMPLETED', 'REFUNDED') NOT NULL DEFAULT 'ACTIVE',
    amount_paid  DECIMAL(10, 2) NOT NULL DEFAULT 0.00 COMMENT 'Price at time of purchase',
    enrolled_at  TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP      NULL,
    CONSTRAINT pk_enrollments PRIMARY KEY (id),
    CONSTRAINT uq_enrollments_user_course UNIQUE (user_id, course_id),
    CONSTRAINT fk_enrollments_user   FOREIGN KEY (user_id)   REFERENCES users(id),
    CONSTRAINT fk_enrollments_course FOREIGN KEY (course_id) REFERENCES courses(id),
    INDEX idx_enrollments_user   (user_id, status),
    INDEX idx_enrollments_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE lesson_progress (
    id              BIGINT    NOT NULL AUTO_INCREMENT,
    enrollment_id   BIGINT    NOT NULL,
    lesson_id       BIGINT    NOT NULL,
    completed       BOOLEAN   NOT NULL DEFAULT FALSE,
    watched_seconds INT       NOT NULL DEFAULT 0,
    last_accessed   TIMESTAMP NULL,
    CONSTRAINT pk_lesson_progress PRIMARY KEY (id),
    CONSTRAINT uq_lp_enrollment_lesson UNIQUE (enrollment_id, lesson_id),
    CONSTRAINT fk_lp_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id) ON DELETE CASCADE,
    CONSTRAINT fk_lp_lesson     FOREIGN KEY (lesson_id)     REFERENCES lessons(id),
    INDEX idx_lp_enrollment (enrollment_id, completed)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE course_certificates (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    enrollment_id    BIGINT       NOT NULL,
    certificate_code VARCHAR(50)  NOT NULL COMMENT 'UUID — publicly verifiable',
    pdf_url          VARCHAR(500) NULL,
    issued_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_course_certificates PRIMARY KEY (id),
    CONSTRAINT uq_cc_enrollment UNIQUE (enrollment_id),
    CONSTRAINT uq_cc_code       UNIQUE (certificate_code),
    CONSTRAINT fk_cc_enrollment FOREIGN KEY (enrollment_id) REFERENCES enrollments(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE quiz_attempts (
    id           BIGINT    NOT NULL AUTO_INCREMENT,
    user_id      BIGINT    NOT NULL,
    quiz_id      BIGINT    NOT NULL,
    score        INT       NOT NULL DEFAULT 0 COMMENT 'Percentage 0-100',
    passed       BOOLEAN   NOT NULL DEFAULT FALSE,
    started_at   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    submitted_at TIMESTAMP NULL,
    CONSTRAINT pk_quiz_attempts PRIMARY KEY (id),
    CONSTRAINT fk_qa_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_qa_user_quiz (user_id, quiz_id),
    INDEX idx_qa_quiz     (quiz_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE quiz_answers (
    id                 BIGINT NOT NULL AUTO_INCREMENT,
    attempt_id         BIGINT NOT NULL,
    question_id        BIGINT NOT NULL,
    selected_option_id BIGINT NULL COMMENT 'NULL if skipped',
    CONSTRAINT pk_quiz_answers PRIMARY KEY (id),
    CONSTRAINT fk_qa_attempt  FOREIGN KEY (attempt_id)         REFERENCES quiz_attempts(id) ON DELETE CASCADE,
    INDEX idx_quiz_answers_attempt (attempt_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE quizzes (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    lesson_id       BIGINT       NOT NULL,
    title           VARCHAR(255) NOT NULL,
    pass_score      INT          NOT NULL DEFAULT 70 COMMENT 'Minimum % to pass',
    time_limit_mins INT          NULL COMMENT 'NULL = no time limit',
    CONSTRAINT pk_quizzes PRIMARY KEY (id),
    CONSTRAINT fk_quizzes_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    INDEX idx_quizzes_lesson (lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE questions (
    id          BIGINT NOT NULL AUTO_INCREMENT,
    quiz_id     BIGINT NOT NULL,
    content     TEXT   NOT NULL,
    type        ENUM('SINGLE', 'MULTIPLE', 'TRUE_FALSE') NOT NULL DEFAULT 'SINGLE',
    points      INT    NOT NULL DEFAULT 10,
    order_index INT    NOT NULL DEFAULT 0,
    CONSTRAINT pk_questions PRIMARY KEY (id),
    CONSTRAINT fk_questions_quiz FOREIGN KEY (quiz_id) REFERENCES quizzes(id) ON DELETE CASCADE,
    INDEX idx_questions_quiz (quiz_id, order_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE answer_options (
    id          BIGINT  NOT NULL AUTO_INCREMENT,
    question_id BIGINT  NOT NULL,
    content     TEXT    NOT NULL,
    is_correct  BOOLEAN NOT NULL DEFAULT FALSE,
    order_index INT     NOT NULL DEFAULT 0,
    CONSTRAINT pk_answer_options PRIMARY KEY (id),
    CONSTRAINT fk_ao_question FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE,
    INDEX idx_ao_question (question_id, order_index)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

ALTER TABLE quiz_answers
    ADD CONSTRAINT fk_qa_question FOREIGN KEY (question_id) REFERENCES questions(id),
    ADD CONSTRAINT fk_qa_option FOREIGN KEY (selected_option_id) REFERENCES answer_options(id);

CREATE TABLE assignments (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    lesson_id    BIGINT       NOT NULL,
    title        VARCHAR(255) NOT NULL,
    instructions TEXT         NULL,
    max_score    INT          NOT NULL DEFAULT 100,
    due_days     INT          NULL COMMENT 'Days after enrollment; NULL = no due date',
    CONSTRAINT pk_assignments PRIMARY KEY (id),
    CONSTRAINT fk_assignments_lesson FOREIGN KEY (lesson_id) REFERENCES lessons(id) ON DELETE CASCADE,
    INDEX idx_assignments_lesson (lesson_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE reviews (
    id         BIGINT    NOT NULL AUTO_INCREMENT,
    user_id    BIGINT    NOT NULL,
    course_id  BIGINT    NOT NULL,
    rating     TINYINT   NOT NULL COMMENT '1-5 stars',
    content    TEXT      NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_reviews PRIMARY KEY (id),
    CONSTRAINT uq_reviews_user_course UNIQUE (user_id, course_id),
    CONSTRAINT chk_reviews_rating CHECK (rating BETWEEN 1 AND 5),
    CONSTRAINT fk_reviews_user   FOREIGN KEY (user_id)   REFERENCES users(id),
    CONSTRAINT fk_reviews_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE,
    INDEX idx_reviews_course (course_id, rating DESC),
    INDEX idx_reviews_user   (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE review_likes (
    user_id    BIGINT    NOT NULL,
    review_id  BIGINT    NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_review_likes PRIMARY KEY (user_id, review_id),
    CONSTRAINT fk_rl_user   FOREIGN KEY (user_id)   REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_rl_review FOREIGN KEY (review_id) REFERENCES reviews(id)  ON DELETE CASCADE,
    INDEX idx_rl_review (review_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE course_stats (
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    course_id         BIGINT         NOT NULL,
    total_enrollments INT            NOT NULL DEFAULT 0,
    total_reviews     INT            NOT NULL DEFAULT 0,
    avg_rating        DECIMAL(3, 2)  NOT NULL DEFAULT 0.00,
    total_completions INT            NOT NULL DEFAULT 0,
    updated_at        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_course_stats PRIMARY KEY (id),
    CONSTRAINT uq_course_stats_course UNIQUE (course_id),
    CONSTRAINT fk_course_stats_course FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE orders (
    id             BIGINT         NOT NULL AUTO_INCREMENT,
    user_id        BIGINT         NOT NULL,
    total_amount   DECIMAL(10, 2) NOT NULL,
    status         ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_method VARCHAR(50)    NULL COMMENT 'VNPAY | MOMO | FREE | STRIPE',
    transaction_id VARCHAR(255)   NULL COMMENT 'External payment gateway reference',
    created_at     TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_orders PRIMARY KEY (id),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_orders_user   (user_id, status),
    INDEX idx_orders_status (status, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 28. order_items
CREATE TABLE order_items (
    id        BIGINT         NOT NULL AUTO_INCREMENT,
    order_id  BIGINT         NOT NULL,
    course_id BIGINT         NOT NULL,
    price     DECIMAL(10, 2) NOT NULL COMMENT 'Snapshot price at purchase time',
    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_oi_order  FOREIGN KEY (order_id)  REFERENCES orders(id)  ON DELETE CASCADE,
    CONSTRAINT fk_oi_course FOREIGN KEY (course_id) REFERENCES courses(id),
    INDEX idx_oi_order  (order_id),
    INDEX idx_oi_course (course_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Lưu lịch sử hội thoại với Anthropic Claude API.
-- course_id nullable: chat tổng hợp hoặc gắn với khóa học cụ thể.
-- tokens_used để track cost và implement rate limiting.
CREATE TABLE chat_conversations (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    user_id    BIGINT       NOT NULL,
    course_id  BIGINT       NULL COMMENT 'NULL = general chat',
    title      VARCHAR(255) NULL COMMENT 'Auto-generated from first message',
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT pk_chat_conversations PRIMARY KEY (id),
    CONSTRAINT fk_cc_user   FOREIGN KEY (user_id)   REFERENCES users(id)    ON DELETE CASCADE,
    CONSTRAINT fk_cc_course_chat FOREIGN KEY (course_id) REFERENCES courses(id)  ON DELETE SET NULL,
    INDEX idx_cc_user (user_id, updated_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE chat_messages (
    id              BIGINT    NOT NULL AUTO_INCREMENT,
    conversation_id BIGINT    NOT NULL,
    role            ENUM('USER', 'ASSISTANT') NOT NULL,
    content         TEXT      NOT NULL,
    tokens_used     INT       NULL COMMENT 'Anthropic API token count for cost tracking',
    created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT pk_chat_messages PRIMARY KEY (id),
    CONSTRAINT fk_cm_conversation FOREIGN KEY (conversation_id) REFERENCES chat_conversations(id) ON DELETE CASCADE,
    INDEX idx_cm_conversation (conversation_id, created_at ASC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO roles (name, description) VALUES
    ('ADMIN',      'Quản trị viên hệ thống'),
    ('INSTRUCTOR', 'Giảng viên — tạo và quản lý khóa học'),
    ('STUDENT',    'Học viên — đăng ký và học khóa học');

INSERT INTO categories (name, slug, parent_id, icon_url) VALUES
    ('Lập trình',          'lap-trinh',          NULL, NULL),
    ('Thiết kế',           'thiet-ke',           NULL, NULL),
    ('Kinh doanh',         'kinh-doanh',         NULL, NULL),
    ('Marketing',          'marketing',          NULL, NULL),
    ('Khoa học dữ liệu',   'khoa-hoc-du-lieu',   NULL, NULL),
    ('Phát triển cá nhân', 'phat-trien-ca-nhan', NULL, NULL);
select * from categories;
-- Sub-categories (lập trình)
INSERT INTO categories (name, slug, parent_id) VALUES
    ('Java & Spring Boot', 'java-spring-boot', 1),
    ('Python',             'python',           1),
    ('JavaScript',         'javascript',       1),
    ('ReactJS',            'reactjs',          1),
    ('DevOps & Docker',    'devops-docker',    1),
    ('Mobile (Android)',   'android',          1);

-- View: thông tin đầy đủ của khóa học kèm stats
CREATE OR REPLACE VIEW v_course_detail AS
SELECT
    c.id,
    c.title,
    c.slug,
    c.price,
    c.level,
    c.status,
    c.language,
    c.duration_hours,
    c.published_at,
    u.full_name   AS instructor_name,
    u.avatar_url  AS instructor_avatar,
    up.headline   AS instructor_headline,
    COALESCE(cs.total_enrollments, 0) AS total_enrollments,
    COALESCE(cs.avg_rating,        0) AS avg_rating,
    COALESCE(cs.total_reviews,     0) AS total_reviews,
    COALESCE(cs.total_completions, 0) AS total_completions
FROM courses c
JOIN users        u  ON c.instructor_id = u.id
LEFT JOIN user_profiles up ON u.id = up.user_id
LEFT JOIN course_stats  cs ON c.id = cs.course_id;


-- View: tiến độ học của user trên một enrollment
CREATE OR REPLACE VIEW v_enrollment_progress AS
SELECT
    e.id          AS enrollment_id,
    e.user_id,
    e.course_id,
    e.status      AS enrollment_status,
    e.enrolled_at,
    COUNT(lp.id)                                  AS total_lessons,
    SUM(CASE WHEN lp.completed THEN 1 ELSE 0 END) AS completed_lessons,
    ROUND(
        SUM(CASE WHEN lp.completed THEN 1 ELSE 0 END) * 100.0
        / NULLIF(COUNT(lp.id), 0), 1
    )                                             AS progress_percent
FROM enrollments e
LEFT JOIN lesson_progress lp ON e.id = lp.enrollment_id
GROUP BY e.id, e.user_id, e.course_id, e.status, e.enrolled_at;
