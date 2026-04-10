USE edulearn;

INSERT INTO users (id, email, password_hash, full_name, avatar_url, status, email_verified) VALUES
-- ADMIN
(1,  'admin@edulearn.vn',       '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Admin EduLearn',     'https://api.dicebear.com/7.x/avataaars/svg?seed=admin',       'ACTIVE', TRUE),
(2,  'superadmin@edulearn.vn',  '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Super Admin',        'https://api.dicebear.com/7.x/avataaars/svg?seed=superadmin',  'ACTIVE', TRUE),

-- INSTRUCTOR
(3,  'nguyen.van.hung@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Nguyễn Văn Hùng',   'https://api.dicebear.com/7.x/avataaars/svg?seed=hung',      'ACTIVE', TRUE),
(4,  'tran.thi.lan@gmail.com',   '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Trần Thị Lan',      'https://api.dicebear.com/7.x/avataaars/svg?seed=lan',       'ACTIVE', TRUE),
(5,  'le.minh.duc@gmail.com',    '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Lê Minh Đức',       'https://api.dicebear.com/7.x/avataaars/svg?seed=duc',       'ACTIVE', TRUE),
(6,  'pham.quoc.bao@gmail.com',  '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Phạm Quốc Bảo',    'https://api.dicebear.com/7.x/avataaars/svg?seed=bao',       'ACTIVE', TRUE),

-- STUDENT
(7,  'student01@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Hoàng Văn An',    'https://api.dicebear.com/7.x/avataaars/svg?seed=an',     'ACTIVE', TRUE),
(8,  'student02@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Lý Thị Bình',     'https://api.dicebear.com/7.x/avataaars/svg?seed=binh',   'ACTIVE', TRUE),
(9,  'student03@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Đặng Văn Cường',  'https://api.dicebear.com/7.x/avataaars/svg?seed=cuong',  'ACTIVE', TRUE),
(10, 'student04@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Bùi Thị Dung',    'https://api.dicebear.com/7.x/avataaars/svg?seed=dung',   'ACTIVE', TRUE),
(11, 'student05@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Vũ Minh Hoàng',   'https://api.dicebear.com/7.x/avataaars/svg?seed=hoang',  'ACTIVE', TRUE),
(12, 'student06@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Đinh Thị Hoa',    'https://api.dicebear.com/7.x/avataaars/svg?seed=hoa',    'ACTIVE', TRUE),
(13, 'student07@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Cao Văn Khánh',   'https://api.dicebear.com/7.x/avataaars/svg?seed=khanh',  'ACTIVE', TRUE),
(14, 'student08@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Mai Thị Linh',    'https://api.dicebear.com/7.x/avataaars/svg?seed=linh',   'ACTIVE', TRUE),
(15, 'student09@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Trương Văn Nam',  'https://api.dicebear.com/7.x/avataaars/svg?seed=nam',    'ACTIVE', TRUE),
(16, 'student10@gmail.com', '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'Ngô Thị Phương',  'https://api.dicebear.com/7.x/avataaars/svg?seed=phuong', 'ACTIVE', TRUE),
(17, 'banned01@gmail.com',  '$2b$10$1aQv5v5DF6/2cWolDDpYyuS2s0aHJU.CWy28yBDDaltVpLGG2W1nC', 'User Bị Khóa',    NULL,                                                     'BANNED', FALSE);


-- ============================================================
-- 2. USER_ROLES
-- ============================================================
INSERT INTO user_roles (user_id, role_id) VALUES
(1,  1), -- admin@edulearn.vn   -> ADMIN
(2,  1), -- superadmin          -> ADMIN
(3,  2), -- nguyen.van.hung     -> INSTRUCTOR
(4,  2), -- tran.thi.lan        -> INSTRUCTOR
(5,  2), -- le.minh.duc         -> INSTRUCTOR
(6,  2), -- pham.quoc.bao       -> INSTRUCTOR
(7,  3), -- student01           -> STUDENT
(8,  3),
(9,  3),
(10, 3),
(11, 3),
(12, 3),
(13, 3),
(14, 3),
(15, 3),
(16, 3),
(17, 3); -- banned user         -> STUDENT


-- ============================================================
-- 3. USER_PROFILES
-- ============================================================
INSERT INTO user_profiles (user_id, phone, bio, website, headline, location) VALUES
(3,  '0901234567', 'Kỹ sư phần mềm với 8 năm kinh nghiệm Java, Spring Boot. Từng làm việc tại FPT Software và Vingroup.', 'https://github.com/nguyenvanhung', 'Senior Java Developer | Spring Boot Expert', 'Hà Nội'),
(4,  '0912345678', 'Frontend Developer và UI/UX Designer với 6 năm kinh nghiệm. Yêu thích ReactJS và thiết kế giao diện đẹp.', 'https://tranthilan.dev', 'Frontend Developer | ReactJS & UI/UX', 'TP.HCM'),
(5,  '0923456789', 'Chuyên gia Data Science, Machine Learning. Có bằng Tiến sĩ Khoa học Máy tính. Nghiên cứu sinh tại Đại học Bách Khoa.', NULL, 'Data Scientist | ML Engineer | PhD', 'Đà Nẵng'),
(6,  '0934567890', 'DevOps Engineer với kinh nghiệm triển khai hệ thống quy mô lớn. Chuyên về Docker, Kubernetes, CI/CD.', 'https://linkedin.com/in/phamquocbao', 'DevOps Engineer | Cloud Architect', 'Hà Nội'),
(7,  '0945678901', 'Sinh viên năm 3 Đại học Bách Khoa, đam mê lập trình Java và muốn trở thành Backend Developer.', NULL, 'Sinh viên CNTT | Đam mê Java', 'Hà Nội'),
(8,  '0956789012', 'Fresher Frontend, đang học ReactJS để ứng tuyển việc làm.', NULL, 'Fresher Frontend Developer', 'TP.HCM'),
(9,  '0967890123', 'Backend developer 2 năm kinh nghiệm, muốn nâng cao kỹ năng Spring Boot và Microservices.', NULL, 'Backend Developer | Java Enthusiast', 'Hà Nội'),
(10, '0978901234', 'Học thiết kế đồ họa, muốn chuyển sang UI/UX design.', NULL, 'UI/UX Design Student', 'Cần Thơ'),
(11, '0989012345', 'Tester muốn học lập trình để hiểu hơn về hệ thống.', NULL, 'QA Engineer | Học lập trình', 'TP.HCM'),
(12, '0990123456', 'Marketing manager muốn học Digital Marketing và Data Analytics.', NULL, 'Marketing Manager | Learning Data', 'Hà Nội');


-- ============================================================
-- 4. CATEGORIES 
-- ============================================================
-- Sub-categories cho Thiết kế (id=2)
INSERT INTO categories (name, slug, parent_id) VALUES
('UI/UX Design',        'ui-ux-design',      2),
('Đồ họa Photoshop',    'do-hoa-photoshop',  2),
('Figma',               'figma',             2);

-- Sub-categories cho Marketing (id=4)
INSERT INTO categories (name, slug, parent_id) VALUES
('SEO & Content',         'seo-content',         4),
('Facebook Ads',          'facebook-ads',         4),
('Google Ads',            'google-ads',           4);

-- Sub-categories cho Khoa học dữ liệu (id=5)
INSERT INTO categories (name, slug, parent_id) VALUES
('Python cho Data',       'python-for-data',     5),
('Machine Learning',      'machine-learning',    5),
('SQL & Database',        'sql-database',        5);


-- ============================================================
-- 5. COURSES 
-- ============================================================
INSERT INTO courses (id, instructor_id, title, slug, description, thumbnail_url, price, level, status, language, duration_hours, published_at) VALUES

(1, 3, 'Java Spring Boot từ Zero đến Hero',
 'java-spring-boot-zero-to-hero',
 'Khóa học Java Spring Boot toàn diện từ cơ bản đến nâng cao. Bạn sẽ học cách xây dựng RESTful API, tích hợp JWT Authentication, làm việc với JPA/Hibernate, Redis Cache, và deploy ứng dụng thực tế. Phù hợp cho sinh viên và developer muốn làm Backend Java.',
 'https://picsum.photos/seed/java-spring/800/450',
 299000.00, 'BEGINNER', 'PUBLISHED', 'vi', 42,
 '2024-09-01 08:00:00'),

(2, 3, 'Microservices với Spring Cloud',
 'microservices-spring-cloud',
 'Học cách thiết kế và xây dựng hệ thống Microservices chuyên nghiệp với Spring Cloud, Eureka, API Gateway, Feign Client, và Circuit Breaker. Dành cho developer đã biết Spring Boot cơ bản.',
 'https://picsum.photos/seed/microservices/800/450',
 499000.00, 'ADVANCED', 'PUBLISHED', 'vi', 38,
 '2024-10-15 08:00:00'),

(3, 4, 'ReactJS & TypeScript — Xây dựng Web hiện đại',
 'reactjs-typescript-web-hien-dai',
 'Khóa học ReactJS kết hợp TypeScript từ cơ bản đến thực chiến. Học cách xây dựng Single Page Application, quản lý state với Zustand, gọi API với React Query, và deploy lên Vercel.',
 'https://picsum.photos/seed/reactjs/800/450',
 349000.00, 'INTERMEDIATE', 'PUBLISHED', 'vi', 35,
 '2024-08-20 08:00:00'),

(4, 5, 'Machine Learning với Python — Thực chiến',
 'machine-learning-python-thuc-chien',
 'Khóa học Machine Learning từ lý thuyết đến thực hành với Python, Scikit-learn, Pandas, NumPy. Bạn sẽ xây dựng các mô hình dự đoán thực tế và hiểu bản chất các thuật toán ML.',
 'https://picsum.photos/seed/ml-python/800/450',
 449000.00, 'INTERMEDIATE', 'PUBLISHED', 'vi', 48,
 '2024-07-10 08:00:00'),

(5, 6, 'Docker & Kubernetes cho Developer',
 'docker-kubernetes-developer',
 'Học Docker từ container cơ bản đến orchestration với Kubernetes. Bao gồm: Dockerfile, Docker Compose, Kubernetes Deployment, Service, Ingress, và CI/CD pipeline hoàn chỉnh.',
 'https://picsum.photos/seed/docker-k8s/800/450',
 379000.00, 'INTERMEDIATE', 'PUBLISHED', 'vi', 30,
 '2024-11-01 08:00:00'),

(6, 4, 'UI/UX Design với Figma — Từ cơ bản đến chuyên nghiệp',
 'ui-ux-figma-co-ban-den-chuyen-nghiep',
 'Học thiết kế UI/UX từ nguyên tắc cơ bản đến thực hành với Figma. Bao gồm: wireframing, prototyping, design system, và cách làm việc với lập trình viên.',
 'https://picsum.photos/seed/figma-ux/800/450',
 199000.00, 'BEGINNER', 'PUBLISHED', 'vi', 20,
 '2024-06-15 08:00:00'),

(7, 3, 'Lập trình Hướng đối tượng với Java',
 'lap-trinh-huong-doi-tuong-java',
 'Nắm vững nền tảng OOP với Java: Class, Object, Inheritance, Polymorphism, Abstraction, Interface, Collections Framework và Exception Handling. Khóa học bắt buộc trước khi học Spring Boot.',
 'https://picsum.photos/seed/java-oop/800/450',
 0.00, 'BEGINNER', 'PUBLISHED', 'vi', 15,
 '2024-05-01 08:00:00'),

(8, 5, 'SQL & Database Design nâng cao',
 'sql-database-design-nang-cao',
 'Học SQL từ cơ bản đến nâng cao: SELECT, JOIN, Subquery, Stored Procedure, Index, Transaction, và thiết kế cơ sở dữ liệu chuẩn hóa. Thực hành với MySQL và PostgreSQL.',
 'https://picsum.photos/seed/sql-db/800/450',
 149000.00, 'BEGINNER', 'PUBLISHED', 'vi', 18,
 '2024-04-20 08:00:00');


-- ============================================================
-- 6. COURSE_CATEGORIES
-- ============================================================
INSERT INTO course_categories (course_id, category_id) VALUES
(1, 1), (1, 7),   -- Java Spring Boot -> Lập trình, Java & Spring Boot
(2, 1), (2, 7),   -- Microservices -> Lập trình, Java & Spring Boot
(3, 1), (3, 10),  -- ReactJS -> Lập trình, ReactJS
(4, 5), (4, 20),  -- ML Python -> Khoa học dữ liệu, Python cho Data, Machine Learning
(5, 1), (5, 11),  -- Docker -> Lập trình, DevOps & Docker
(6, 2), (6, 13),  -- Figma -> Thiết kế, Figma
(7, 1), (7, 7),   -- Java OOP -> Lập trình, Java & Spring Boot
(8, 5), (8, 22);  -- SQL -> Khoa học dữ liệu, SQL & Database


-- ============================================================
-- 7. COURSE_REQUIREMENTS
-- ============================================================
INSERT INTO course_requirements (course_id, description, order_index) VALUES
-- Course 1: Java Spring Boot
(1, 'Biết lập trình Java cơ bản (biến, điều kiện, vòng lặp)', 1),
(1, 'Hiểu khái niệm OOP (Class, Object, Inheritance)', 2),
(1, 'Cài đặt được JDK 17 và IntelliJ IDEA', 3),
(1, 'Có kiến thức cơ bản về SQL', 4),
-- Course 2: Microservices
(2, 'Hoàn thành khóa Java Spring Boot hoặc có kinh nghiệm tương đương', 1),
(2, 'Hiểu về REST API và HTTP', 2),
(2, 'Biết sử dụng Maven/Gradle', 3),
-- Course 3: ReactJS
(3, 'Biết HTML, CSS cơ bản', 1),
(3, 'Hiểu JavaScript ES6+ (Arrow function, Promise, async/await)', 2),
(3, 'Cài Node.js và npm', 3),
-- Course 7: Java OOP (free)
(7, 'Cài đặt được JDK và IDE', 1),
(7, 'Không cần kiến thức lập trình trước', 2);


-- ============================================================
-- 8. COURSE_OUTCOMES
-- ============================================================
INSERT INTO course_outcomes (course_id, description, order_index) VALUES
-- Course 1: Java Spring Boot
(1, 'Xây dựng RESTful API hoàn chỉnh với Spring Boot 3', 1),
(1, 'Triển khai JWT Authentication & Authorization', 2),
(1, 'Làm việc với JPA/Hibernate và MySQL', 3),
(1, 'Cache dữ liệu với Redis', 4),
(1, 'Viết Unit Test và Integration Test', 5),
(1, 'Deploy ứng dụng với Docker', 6),
-- Course 3: ReactJS
(3, 'Xây dựng Single Page Application với React 18', 1),
(3, 'Quản lý state với Zustand và React Query', 2),
(3, 'Sử dụng TypeScript trong dự án React', 3),
(3, 'Tích hợp API với Axios và xử lý Authentication', 4),
-- Course 7: Java OOP
(7, 'Hiểu và áp dụng 4 tính chất OOP', 1),
(7, 'Sử dụng Collections Framework thành thạo', 2),
(7, 'Xử lý Exception đúng cách', 3);


-- ============================================================
-- 9. SECTIONS
-- ============================================================
INSERT INTO sections (id, course_id, title, order_index) VALUES
-- Course 1: Java Spring Boot (5 sections)
(1,  1, 'Giới thiệu & Cài đặt môi trường',                1),
(2,  1, 'RESTful API với Spring MVC',                      2),
(3,  1, 'Spring Data JPA & Database',                      3),
(4,  1, 'Spring Security & JWT Authentication',            4),
(5,  1, 'Redis Cache & Performance',                       5),

-- Course 2: Microservices (3 sections)
(6,  2, 'Giới thiệu Microservices Architecture',           1),
(7,  2, 'Spring Cloud & Service Discovery',               2),
(8,  2, 'API Gateway & Load Balancing',                    3),

-- Course 3: ReactJS (4 sections)
(9,  3, 'React Fundamentals & JSX',                       1),
(10, 3, 'Hooks & State Management',                       2),
(11, 3, 'TypeScript với React',                           3),
(12, 3, 'Dự án thực tế: Todo App nâng cao',               4),

-- Course 4: ML Python (3 sections)
(13, 4, 'Python cho Data Science',                        1),
(14, 4, 'Các thuật toán Machine Learning',                2),
(15, 4, 'Dự án thực tế: Dự đoán giá nhà',                3),

-- Course 5: Docker (3 sections)
(16, 5, 'Docker Fundamentals',                            1),
(17, 5, 'Docker Compose & Multi-container',               2),
(18, 5, 'Kubernetes Basics',                              3),

-- Course 6: Figma (2 sections)
(19, 6, 'Nguyên tắc thiết kế UI/UX',                     1),
(20, 6, 'Thực hành với Figma',                            2),

-- Course 7: Java OOP (2 sections)
(21, 7, 'Nền tảng OOP với Java',                          1),
(22, 7, 'Collections & Exception Handling',               2),

-- Course 8: SQL (2 sections)
(23, 8, 'SQL cơ bản đến nâng cao',                        1),
(24, 8, 'Database Design & Optimization',                 2);


-- ============================================================
-- 10. LESSONS
-- ============================================================
INSERT INTO lessons (id, section_id, title, type, content_url, duration_seconds, is_preview, order_index) VALUES
-- Section 1: Giới thiệu Spring Boot
(1,  1, 'Giới thiệu khóa học và lộ trình học',           'VIDEO', 'https://www.youtube.com/watch?v=demo1',  480,  TRUE,  1),
(2,  1, 'Cài đặt JDK 17, IntelliJ IDEA, Maven',          'VIDEO', 'https://www.youtube.com/watch?v=demo2',  720,  TRUE,  2),
(3,  1, 'Tạo project Spring Boot đầu tiên',               'VIDEO', 'https://www.youtube.com/watch?v=demo3',  900,  FALSE, 3),
(4,  1, 'Hiểu cấu trúc project Spring Boot',             'TEXT',  NULL,                                      600,  FALSE, 4),

-- Section 2: RESTful API
(5,  2, 'HTTP Methods và REST conventions',               'VIDEO', 'https://www.youtube.com/watch?v=demo5',  840,  TRUE,  1),
(6,  2, 'Tạo Controller và xử lý Request',               'VIDEO', 'https://www.youtube.com/watch?v=demo6', 1200,  FALSE, 2),
(7,  2, 'Request Mapping, PathVariable, RequestParam',    'VIDEO', 'https://www.youtube.com/watch?v=demo7',  960,  FALSE, 3),
(8,  2, 'ResponseEntity và HTTP Status Codes',            'VIDEO', 'https://www.youtube.com/watch?v=demo8',  780,  FALSE, 4),
(9,  2, 'DTO Pattern — tách Entity và Response',          'VIDEO', 'https://www.youtube.com/watch?v=demo9',  900,  FALSE, 5),
(10, 2, 'GlobalExceptionHandler',                        'VIDEO', 'https://www.youtube.com/watch?v=demo10', 1080, FALSE, 6),
(11, 2, 'Quiz: RESTful API',                             'QUIZ',  NULL,                                        0,  FALSE, 7),

-- Section 3: JPA & Database
(12, 3, 'JPA và Hibernate là gì?',                       'VIDEO', 'https://www.youtube.com/watch?v=demo12',  720, TRUE,  1),
(13, 3, 'Entity, Repository, Service pattern',           'VIDEO', 'https://www.youtube.com/watch?v=demo13', 1080, FALSE, 2),
(14, 3, 'Custom Query với @Query JPQL',                  'VIDEO', 'https://www.youtube.com/watch?v=demo14',  960, FALSE, 3),
(15, 3, 'Pagination và Sorting',                         'VIDEO', 'https://www.youtube.com/watch?v=demo15',  840, FALSE, 4),
(16, 3, 'Transaction Management',                        'VIDEO', 'https://www.youtube.com/watch?v=demo16',  900, FALSE, 5),

-- Section 4: Spring Security & JWT
(17, 4, 'Spring Security cơ bản',                        'VIDEO', 'https://www.youtube.com/watch?v=demo17',  780, TRUE,  1),
(18, 4, 'JWT là gì và hoạt động như thế nào',            'VIDEO', 'https://www.youtube.com/watch?v=demo18',  660, TRUE,  2),
(19, 4, 'Implement JWT Authentication',                  'VIDEO', 'https://www.youtube.com/watch?v=demo19', 1440, FALSE, 3),
(20, 4, 'Role-based Authorization với @PreAuthorize',    'VIDEO', 'https://www.youtube.com/watch?v=demo20', 1200, FALSE, 4),
(21, 4, 'Refresh Token và bảo mật nâng cao',             'VIDEO', 'https://www.youtube.com/watch?v=demo21', 1080, FALSE, 5),
(22, 4, 'Quiz: Spring Security & JWT',                   'QUIZ',  NULL,                                        0, FALSE, 6),

-- Section 5: Redis Cache
(23, 5, 'Redis là gì? Cài đặt và kết nối',               'VIDEO', 'https://www.youtube.com/watch?v=demo23',  600, TRUE,  1),
(24, 5, '@Cacheable, @CacheEvict, @CachePut',            'VIDEO', 'https://www.youtube.com/watch?v=demo24',  960, FALSE, 2),
(25, 5, 'Chiến lược cache cho production',               'VIDEO', 'https://www.youtube.com/watch?v=demo25',  780, FALSE, 3),

-- Section 6-8: Microservices
(26, 6, 'Monolith vs Microservices',                     'VIDEO', 'https://www.youtube.com/watch?v=demo26',  720, TRUE,  1),
(27, 6, 'Thiết kế domain boundaries',                   'VIDEO', 'https://www.youtube.com/watch?v=demo27',  900, FALSE, 2),
(28, 7, 'Eureka Service Discovery',                      'VIDEO', 'https://www.youtube.com/watch?v=demo28', 1080, FALSE, 1),
(29, 7, 'Feign Client gọi service khác',                 'VIDEO', 'https://www.youtube.com/watch?v=demo29',  960, FALSE, 2),
(30, 8, 'Spring Cloud Gateway',                          'VIDEO', 'https://www.youtube.com/watch?v=demo30', 1200, FALSE, 1),

-- Section 9-12: ReactJS
(31, 9,  'JSX và Component cơ bản',                      'VIDEO', 'https://www.youtube.com/watch?v=demo31',  780, TRUE,  1),
(32, 9,  'Props và Children',                            'VIDEO', 'https://www.youtube.com/watch?v=demo32',  660, TRUE,  2),
(33, 10, 'useState và useEffect',                        'VIDEO', 'https://www.youtube.com/watch?v=demo33',  900, FALSE, 1),
(34, 10, 'Custom Hooks',                                 'VIDEO', 'https://www.youtube.com/watch?v=demo34',  780, FALSE, 2),
(35, 10, 'Zustand State Management',                     'VIDEO', 'https://www.youtube.com/watch?v=demo35', 1080, FALSE, 3),
(36, 11, 'TypeScript cơ bản cho React',                  'VIDEO', 'https://www.youtube.com/watch?v=demo36',  840, FALSE, 1),
(37, 11, 'Interface, Type, Generic trong React',         'VIDEO', 'https://www.youtube.com/watch?v=demo37',  900, FALSE, 2),
(38, 12, 'Xây dựng Todo App hoàn chỉnh',                 'VIDEO', 'https://www.youtube.com/watch?v=demo38', 1800, FALSE, 1),

-- Section 13-15: ML Python
(39, 13, 'NumPy và Pandas cơ bản',                       'VIDEO', 'https://www.youtube.com/watch?v=demo39', 1200, TRUE,  1),
(40, 13, 'Data Visualization với Matplotlib',            'VIDEO', 'https://www.youtube.com/watch?v=demo40',  900, FALSE, 2),
(41, 14, 'Linear Regression',                            'VIDEO', 'https://www.youtube.com/watch?v=demo41', 1080, FALSE, 1),
(42, 14, 'Decision Tree & Random Forest',                'VIDEO', 'https://www.youtube.com/watch?v=demo42', 1200, FALSE, 2),
(43, 15, 'Thu thập và xử lý dữ liệu nhà',               'VIDEO', 'https://www.youtube.com/watch?v=demo43', 1440, FALSE, 1),
(44, 15, 'Train và evaluate mô hình',                   'VIDEO', 'https://www.youtube.com/watch?v=demo44', 1200, FALSE, 2),

-- Section 16-18: Docker
(45, 16, 'Docker là gì? Container vs VM',                'VIDEO', 'https://www.youtube.com/watch?v=demo45',  720, TRUE,  1),
(46, 16, 'Dockerfile và build image',                    'VIDEO', 'https://www.youtube.com/watch?v=demo46',  960, FALSE, 2),
(47, 17, 'Docker Compose cho ứng dụng đa container',     'VIDEO', 'https://www.youtube.com/watch?v=demo47', 1200, FALSE, 1),
(48, 18, 'Kubernetes Pods, Deployments, Services',       'VIDEO', 'https://www.youtube.com/watch?v=demo48', 1440, FALSE, 1),

-- Section 19-20: Figma
(49, 19, 'Color theory và Typography trong UI',          'VIDEO', 'https://www.youtube.com/watch?v=demo49',  780, TRUE,  1),
(50, 19, 'Spacing, Layout và Grid system',               'VIDEO', 'https://www.youtube.com/watch?v=demo50',  720, FALSE, 2),
(51, 20, 'Giao diện với Figma: Auto Layout',             'VIDEO', 'https://www.youtube.com/watch?v=demo51',  960, FALSE, 1),
(52, 20, 'Tạo prototype và handoff cho developer',       'VIDEO', 'https://www.youtube.com/watch?v=demo52',  840, FALSE, 2),

-- Section 21-22: Java OOP
(53, 21, 'Class và Object trong Java',                   'VIDEO', 'https://www.youtube.com/watch?v=demo53',  780, TRUE,  1),
(54, 21, 'Encapsulation và Access Modifier',             'VIDEO', 'https://www.youtube.com/watch?v=demo54',  660, TRUE,  2),
(55, 21, 'Inheritance và Polymorphism',                  'VIDEO', 'https://www.youtube.com/watch?v=demo55',  900, FALSE, 3),
(56, 21, 'Abstract Class và Interface',                  'VIDEO', 'https://www.youtube.com/watch?v=demo56',  840, FALSE, 4),
(57, 22, 'ArrayList, HashMap, LinkedList',               'VIDEO', 'https://www.youtube.com/watch?v=demo57',  780, FALSE, 1),
(58, 22, 'Exception Handling — try/catch/finally',       'VIDEO', 'https://www.youtube.com/watch?v=demo58',  720, FALSE, 2),

-- Section 23-24: SQL
(59, 23, 'SELECT, WHERE, ORDER BY, LIMIT',               'VIDEO', 'https://www.youtube.com/watch?v=demo59',  900, TRUE,  1),
(60, 23, 'JOIN: INNER, LEFT, RIGHT, FULL',               'VIDEO', 'https://www.youtube.com/watch?v=demo60', 1080, TRUE,  2),
(61, 23, 'Subquery và CTE',                              'VIDEO', 'https://www.youtube.com/watch?v=demo61',  960, FALSE, 3),
(62, 24, 'Thiết kế CSDL chuẩn 3NF',                     'VIDEO', 'https://www.youtube.com/watch?v=demo62', 1200, FALSE, 1),
(63, 24, 'Index và Query Optimization',                  'VIDEO', 'https://www.youtube.com/watch?v=demo63', 1080, FALSE, 2);


-- ============================================================
-- 11. LESSON_RESOURCES
-- ============================================================
INSERT INTO lesson_resources (lesson_id, name, file_url, resource_type) VALUES
(3,  'Source code khởi tạo project',            'https://github.com/edulearn/spring-boot-starter',    'CODE'),
(6,  'Slides: REST API Design',                  'https://cdn.edulearn.vn/slides/rest-api.pdf',        'PDF'),
(12, 'Hibernate ORM Cheat Sheet',                'https://cdn.edulearn.vn/docs/hibernate-cheatsheet.pdf', 'PDF'),
(19, 'Source code JWT Authentication',           'https://github.com/edulearn/jwt-spring-security',   'CODE'),
(38, 'Source code Todo App hoàn chỉnh',          'https://github.com/edulearn/react-todo-app',        'CODE'),
(46, 'Dockerfile mẫu cho Spring Boot',           'https://cdn.edulearn.vn/files/Dockerfile.sample',   'CODE'),
(62, 'Script tạo database chuẩn 3NF',           'https://cdn.edulearn.vn/files/db_design.sql',       'CODE');


-- ============================================================
-- 12. QUIZZES
-- ============================================================
INSERT INTO quizzes (id, lesson_id, title, pass_score, time_limit_mins) VALUES
(1, 11, 'Kiểm tra kiến thức RESTful API',    70, 15),
(2, 22, 'Kiểm tra JWT & Spring Security',    80, 20),
(3, 41, 'Kiểm tra Linear Regression',        70, 10);


-- ============================================================
-- 13. QUESTIONS
-- ============================================================
INSERT INTO questions (id, quiz_id, content, type, points, order_index) VALUES
-- Quiz 1: RESTful API (4 câu)
(1,  1, 'HTTP method nào được dùng để tạo mới resource?',              'SINGLE',     25, 1),
(2,  1, 'Status code 404 có nghĩa là gì?',                             'SINGLE',     25, 2),
(3,  1, 'DTO (Data Transfer Object) có tác dụng gì?',                  'SINGLE',     25, 3),
(4,  1, 'Những HTTP method nào là idempotent?',                        'MULTIPLE',   25, 4),

-- Quiz 2: JWT (4 câu)
(5,  2, 'JWT gồm bao nhiêu phần, ngăn cách nhau bằng dấu gì?',        'SINGLE',     25, 1),
(6,  2, 'Phần nào của JWT chứa thông tin user (claims)?',              'SINGLE',     25, 2),
(7,  2, 'Access token thường có thời gian sống bao lâu?',              'SINGLE',     25, 3),
(8,  2, 'JWT có bị decode không nếu không có secret key?',             'TRUE_FALSE', 25, 4),

-- Quiz 3: Linear Regression (3 câu)
(9,  3, 'Linear Regression dùng để giải quyết bài toán loại gì?',     'SINGLE',     34, 1),
(10, 3, 'Mean Squared Error (MSE) càng nhỏ thì mô hình càng tốt?',    'TRUE_FALSE', 33, 2),
(11, 3, 'Gradient Descent là gì?',                                     'SINGLE',     33, 3);


-- ============================================================
-- 14. ANSWER_OPTIONS
-- ============================================================
INSERT INTO answer_options (question_id, content, is_correct, order_index) VALUES
-- Q1: HTTP method để tạo mới
(1, 'GET',    FALSE, 1),
(1, 'POST',   TRUE,  2),
(1, 'PUT',    FALSE, 3),
(1, 'DELETE', FALSE, 4),

-- Q2: Status code 404
(2, 'Internal Server Error',   FALSE, 1),
(2, 'Not Found',               TRUE,  2),
(2, 'Unauthorized',            FALSE, 3),
(2, 'Bad Request',             FALSE, 4),

-- Q3: DTO
(3, 'Kết nối trực tiếp database',                 FALSE, 1),
(3, 'Tách biệt Entity với dữ liệu trả về API',    TRUE,  2),
(3, 'Thay thế Service layer',                     FALSE, 3),
(3, 'Encrypt password',                           FALSE, 4),

-- Q4: Idempotent methods (multiple choice)
(4, 'GET',    TRUE,  1),
(4, 'POST',   FALSE, 2),
(4, 'PUT',    TRUE,  3),
(4, 'DELETE', TRUE,  4),

-- Q5: JWT gồm bao nhiêu phần
(5, '2 phần, ngăn cách bằng dấu .',  FALSE, 1),
(5, '3 phần, ngăn cách bằng dấu .', TRUE,  2),
(5, '4 phần, ngăn cách bằng dấu -', FALSE, 3),
(5, '1 phần duy nhất',               FALSE, 4),

-- Q6: Phần nào chứa claims
(6, 'Header',    FALSE, 1),
(6, 'Payload',   TRUE,  2),
(6, 'Signature', FALSE, 3),
(6, 'Footer',    FALSE, 4),

-- Q7: Access token sống bao lâu
(7, '1 ngày',     FALSE, 1),
(7, '30 phút',    TRUE,  2),
(7, '1 tháng',    FALSE, 3),
(7, 'Mãi mãi',   FALSE, 4),

-- Q8: JWT có decode được không (TRUE/FALSE)
(8, 'Đúng — Payload có thể decode được bằng Base64', TRUE,  1),
(8, 'Sai — Không thể đọc được nếu không có key',    FALSE, 2),

-- Q9: Linear Regression
(9, 'Classification',  FALSE, 1),
(9, 'Regression',      TRUE,  2),
(9, 'Clustering',      FALSE, 3),
(9, 'Reinforcement',   FALSE, 4),

-- Q10: MSE (TRUE/FALSE)
(10, 'Đúng',  TRUE,  1),
(10, 'Sai',   FALSE, 2),

-- Q11: Gradient Descent
(11, 'Thuật toán sắp xếp dữ liệu',                          FALSE, 1),
(11, 'Thuật toán tối ưu hóa tìm minimum của hàm loss',      TRUE,  2),
(11, 'Phương pháp chia dữ liệu train/test',                 FALSE, 3),
(11, 'Kỹ thuật normalize dữ liệu',                          FALSE, 4);


-- ============================================================
-- 15. ASSIGNMENTS
-- ============================================================
INSERT INTO assignments (lesson_id, title, instructions, max_score, due_days) VALUES
(6,  'Xây dựng CRUD API cho Product',
     'Tạo một Spring Boot project với đầy đủ CRUD API cho entity Product gồm: id, name, price, description, category. Áp dụng DTO pattern, Bean Validation và GlobalExceptionHandler. Submit link GitHub.',
     100, 7),
(19, 'Implement JWT Authentication cho project',
     'Thêm JWT Authentication vào project CRUD API đã làm. Bao gồm: Register, Login, Refresh Token, Protected endpoint. Test bằng Postman và submit collection + GitHub link.',
     100, 10),
(38, 'Dự án React Todo App',
     'Xây dựng Todo App với React + TypeScript bao gồm: thêm/sửa/xóa todo, filter theo status, lưu localStorage, responsive design. Bonus: kết nối với Spring Boot API backend.',
     100, 14);


-- ============================================================
-- 16. ENROLLMENTS
-- ============================================================
INSERT INTO enrollments (id, user_id, course_id, status, amount_paid, enrolled_at, completed_at) VALUES
-- Student 7 (Hoàng Văn An): học Java Spring Boot và Java OOP
(1,  7,  1, 'ACTIVE',    299000.00, '2024-09-05 09:00:00', NULL),
(2,  7,  7, 'COMPLETED',      0.00, '2024-08-10 10:00:00', '2024-08-25 15:00:00'),

-- Student 8 (Lý Thị Bình): học ReactJS và Figma
(3,  8,  3, 'ACTIVE',    349000.00, '2024-09-10 14:00:00', NULL),
(4,  8,  6, 'COMPLETED', 199000.00, '2024-07-01 08:00:00', '2024-07-20 16:00:00'),

-- Student 9 (Đặng Văn Cường): học Java Spring Boot (đã hoàn thành)
(5,  9,  1, 'COMPLETED', 299000.00, '2024-09-02 08:00:00', '2024-11-15 20:00:00'),

-- Student 10: học ML Python
(6,  10, 4, 'ACTIVE',    449000.00, '2024-08-01 11:00:00', NULL),

-- Student 11: học Docker và Java Spring Boot
(7,  11, 5, 'ACTIVE',    379000.00, '2024-11-05 09:00:00', NULL),
(8,  11, 1, 'ACTIVE',    299000.00, '2024-10-01 10:00:00', NULL),

-- Student 12: học SQL
(9,  12, 8, 'COMPLETED', 149000.00, '2024-05-01 08:00:00', '2024-05-20 17:00:00'),

-- Student 13: học Java OOP (miễn phí) và Microservices
(10, 13, 7, 'COMPLETED',     0.00, '2024-06-01 08:00:00', '2024-06-15 14:00:00'),
(11, 13, 2, 'ACTIVE',   499000.00, '2024-10-20 09:00:00', NULL),

-- Student 14: học ReactJS và Figma
(12, 14, 3, 'ACTIVE',   349000.00, '2024-09-15 10:00:00', NULL),
(13, 14, 6, 'ACTIVE',   199000.00, '2024-10-01 08:00:00', NULL),

-- Student 15: học SQL
(14, 15, 8, 'ACTIVE',   149000.00, '2024-06-10 09:00:00', NULL),

-- Student 16: học nhiều khóa
(15, 16, 4, 'ACTIVE',   449000.00, '2024-08-15 08:00:00', NULL),
(16, 16, 7, 'COMPLETED',    0.00, '2024-05-10 08:00:00', '2024-05-25 16:00:00');


-- ============================================================
-- 17. LESSON_PROGRESS (cho các enrollment đang ACTIVE)
-- ============================================================
-- Enrollment 1: Student 7, Course 1 (Java Spring Boot) - đang học, xong section 1-2
INSERT INTO lesson_progress (enrollment_id, lesson_id, completed, watched_seconds, last_accessed) VALUES
(1, 1,  TRUE,  480,  '2024-09-05 09:10:00'),
(1, 2,  TRUE,  720,  '2024-09-05 10:05:00'),
(1, 3,  TRUE,  900,  '2024-09-06 09:00:00'),
(1, 4,  TRUE,  600,  '2024-09-06 10:00:00'),
(1, 5,  TRUE,  840,  '2024-09-07 09:00:00'),
(1, 6,  TRUE, 1200,  '2024-09-07 10:30:00'),
(1, 7,  TRUE,  960,  '2024-09-08 09:00:00'),
(1, 8,  TRUE,  780,  '2024-09-08 10:00:00'),
(1, 9,  TRUE,  900,  '2024-09-09 09:00:00'),
(1, 10, TRUE, 1080,  '2024-09-09 10:30:00'),
(1, 11, FALSE,   0,  NULL),
(1, 12, FALSE,   0,  NULL),
(1, 13, FALSE,   0,  NULL),
(1, 14, FALSE,   0,  NULL),
(1, 15, FALSE,   0,  NULL),
(1, 16, FALSE,   0,  NULL),
(1, 17, FALSE,   0,  NULL),
(1, 18, FALSE,   0,  NULL),
(1, 19, FALSE,   0,  NULL),
(1, 20, FALSE,   0,  NULL),
(1, 21, FALSE,   0,  NULL),
(1, 22, FALSE,   0,  NULL),
(1, 23, FALSE,   0,  NULL),
(1, 24, FALSE,   0,  NULL),
(1, 25, FALSE,   0,  NULL);

-- Enrollment 2: Student 7, Course 7 (Java OOP) - ĐÃ HOÀN THÀNH
INSERT INTO lesson_progress (enrollment_id, lesson_id, completed, watched_seconds, last_accessed) VALUES
(2, 53, TRUE, 780, '2024-08-10 09:00:00'),
(2, 54, TRUE, 660, '2024-08-11 09:00:00'),
(2, 55, TRUE, 900, '2024-08-12 09:00:00'),
(2, 56, TRUE, 840, '2024-08-13 09:00:00'),
(2, 57, TRUE, 780, '2024-08-14 09:00:00'),
(2, 58, TRUE, 720, '2024-08-15 09:00:00');

-- Enrollment 3: Student 8, Course 3 (ReactJS) - đang học
INSERT INTO lesson_progress (enrollment_id, lesson_id, completed, watched_seconds, last_accessed) VALUES
(3, 31, TRUE,  780, '2024-09-10 14:10:00'),
(3, 32, TRUE,  660, '2024-09-11 14:00:00'),
(3, 33, TRUE,  900, '2024-09-12 14:00:00'),
(3, 34, FALSE, 400, '2024-09-13 14:00:00'),
(3, 35, FALSE,   0, NULL),
(3, 36, FALSE,   0, NULL),
(3, 37, FALSE,   0, NULL),
(3, 38, FALSE,   0, NULL);

-- Enrollment 4: Student 8, Course 6 (Figma) - ĐÃ HOÀN THÀNH
INSERT INTO lesson_progress (enrollment_id, lesson_id, completed, watched_seconds, last_accessed) VALUES
(4, 49, TRUE, 780, '2024-07-01 08:30:00'),
(4, 50, TRUE, 720, '2024-07-02 09:00:00'),
(4, 51, TRUE, 960, '2024-07-03 08:30:00'),
(4, 52, TRUE, 840, '2024-07-04 08:30:00');

-- Enrollment 5: Student 9, Course 1 (Java Spring Boot) - ĐÃ HOÀN THÀNH
INSERT INTO lesson_progress (enrollment_id, lesson_id, completed, watched_seconds, last_accessed) VALUES
(5, 1,  TRUE,  480, '2024-09-02 08:10:00'),
(5, 2,  TRUE,  720, '2024-09-02 09:00:00'),
(5, 3,  TRUE,  900, '2024-09-03 08:00:00'),
(5, 4,  TRUE,  600, '2024-09-03 09:00:00'),
(5, 5,  TRUE,  840, '2024-09-04 08:00:00'),
(5, 6,  TRUE, 1200, '2024-09-04 09:30:00'),
(5, 7,  TRUE,  960, '2024-09-05 08:00:00'),
(5, 8,  TRUE,  780, '2024-09-05 09:00:00'),
(5, 9,  TRUE,  900, '2024-09-06 08:00:00'),
(5, 10, TRUE, 1080, '2024-09-06 09:30:00'),
(5, 11, TRUE,    0, '2024-09-07 08:00:00'),
(5, 12, TRUE,  720, '2024-09-07 09:00:00'),
(5, 13, TRUE, 1080, '2024-09-08 08:00:00'),
(5, 14, TRUE,  960, '2024-09-09 08:00:00'),
(5, 15, TRUE,  840, '2024-09-09 10:00:00'),
(5, 16, TRUE,  900, '2024-09-10 08:00:00'),
(5, 17, TRUE,  780, '2024-09-11 08:00:00'),
(5, 18, TRUE,  660, '2024-09-11 09:00:00'),
(5, 19, TRUE, 1440, '2024-09-12 08:00:00'),
(5, 20, TRUE, 1200, '2024-09-13 08:00:00'),
(5, 21, TRUE, 1080, '2024-09-14 08:00:00'),
(5, 22, TRUE,    0, '2024-09-14 10:00:00'),
(5, 23, TRUE,  600, '2024-09-15 08:00:00'),
(5, 24, TRUE,  960, '2024-09-16 08:00:00'),
(5, 25, TRUE,  780, '2024-09-16 10:00:00');


-- ============================================================
-- 18. COURSE_CERTIFICATES (cho các enrollment COMPLETED)
-- ============================================================
INSERT INTO course_certificates (enrollment_id, certificate_code, pdf_url, issued_at) VALUES
(2,  'EDU-2024-JAVA-OOP-7001',   'https://cdn.edulearn.vn/certs/EDU-2024-JAVA-OOP-7001.pdf',   '2024-08-25 15:30:00'),
(4,  'EDU-2024-FIGMA-8002',      'https://cdn.edulearn.vn/certs/EDU-2024-FIGMA-8002.pdf',      '2024-07-20 16:30:00'),
(5,  'EDU-2024-SPRING-9003',     'https://cdn.edulearn.vn/certs/EDU-2024-SPRING-9003.pdf',     '2024-11-15 20:30:00'),
(9,  'EDU-2024-SQL-12004',       'https://cdn.edulearn.vn/certs/EDU-2024-SQL-12004.pdf',       '2024-05-20 17:30:00'),
(10, 'EDU-2024-JAVA-OOP-13005',  'https://cdn.edulearn.vn/certs/EDU-2024-JAVA-OOP-13005.pdf',  '2024-06-15 14:30:00'),
(16, 'EDU-2024-JAVA-OOP-16006',  'https://cdn.edulearn.vn/certs/EDU-2024-JAVA-OOP-16006.pdf',  '2024-05-25 16:30:00');


-- ============================================================
-- 19. QUIZ_ATTEMPTS
-- ============================================================
INSERT INTO quiz_attempts (id, user_id, quiz_id, score, passed, started_at, submitted_at) VALUES
-- Student 9 làm Quiz 1 (RESTful API): lần 1 trượt, lần 2 đậu
(1, 9, 1, 50, FALSE, '2024-09-07 07:55:00', '2024-09-07 08:10:00'),
(2, 9, 1, 75, TRUE,  '2024-09-07 08:15:00', '2024-09-07 08:28:00'),
-- Student 9 làm Quiz 2 (JWT): đậu ngay lần 1
(3, 9, 2, 100, TRUE, '2024-09-14 09:55:00', '2024-09-14 10:13:00'),
-- Student 7 làm thử Quiz 1 (chưa hoàn thành bài học)
(4, 7, 1, 25, FALSE, '2024-09-10 08:00:00', '2024-09-10 08:12:00'),
-- Student 13 làm Quiz 3 (Linear Regression)
(5, 13, 3, 67, FALSE, '2024-10-25 09:00:00', '2024-10-25 09:08:00'),
(6, 13, 3, 100, TRUE, '2024-10-25 09:10:00', '2024-10-25 09:17:00');


-- ============================================================
-- 20. QUIZ_ANSWERS (cho attempt id=2: Student 9, Quiz 1, score=75)
-- ============================================================
INSERT INTO quiz_answers (attempt_id, question_id, selected_option_id) VALUES
(2, 1, 6),  -- Q1: chọn POST (đúng, option id=6)
(2, 2, 10), -- Q2: chọn Not Found (đúng, option id=10)
(2, 3, 15), -- Q3: chọn "Tách biệt Entity" (đúng, option id=15... chỉ ví dụ)
(2, 4, 17); -- Q4: chọn GET (đúng một phần)


-- ============================================================
-- 21. REVIEWS
-- ============================================================
INSERT INTO reviews (id, user_id, course_id, rating, content, created_at) VALUES
-- Course 1: Java Spring Boot
(1,  9,  1, 5, 'Khóa học cực kỳ chất lượng! Thầy Hùng giảng rất dễ hiểu, có nhiều ví dụ thực tế. Sau khi học xong mình đã xin được việc làm Backend Java. Cảm ơn thầy rất nhiều!',  '2024-11-20 10:00:00'),
(2,  7,  1, 4, 'Nội dung hay, đặc biệt phần JWT và Spring Security. Mong thầy bổ sung thêm phần testing và deployment thực tế hơn.',                                                   '2024-11-10 14:00:00'),
(3,  11, 1, 5, 'Đây là khóa học Spring Boot tốt nhất mình từng học. Cấu trúc rõ ràng, code clean, đi từng bước rất logic.',                                                           '2024-11-05 09:00:00'),
-- Course 3: ReactJS
(4,  8,  3, 5, 'Cô Lan dạy ReactJS kết hợp TypeScript rất tốt. Bài tập thực hành sau mỗi section rất hữu ích.',                                                                      '2024-10-15 11:00:00'),
(5,  14, 3, 4, 'Khóa học tốt, nhưng cần thêm phần về React Testing Library và deployment lên Vercel.',                                                                                '2024-10-20 09:00:00'),
-- Course 7: Java OOP (miễn phí)
(6,  7,  7, 5, 'Khóa học miễn phí mà chất lượng không thua gì khóa trả phí! Rất phù hợp cho người mới bắt đầu.',                                                                    '2024-08-26 10:00:00'),
(7,  13, 7, 5, 'Rất hay! Thầy giải thích OOP rất dễ hiểu với nhiều ví dụ thực tế. Recommend cho tất cả người mới học Java.',                                                          '2024-06-16 09:00:00'),
(8,  16, 7, 4, 'Nội dung đầy đủ, tốc độ vừa phải. Mong có thêm bài tập thực hành.',                                                                                                  '2024-05-26 14:00:00'),
-- Course 6: Figma
(9,  8,  6, 5, 'Khóa học Figma hay nhất mình từng học! Cô Lan giảng rất trực quan.',                                                                                                  '2024-07-21 10:00:00'),
-- Course 8: SQL
(10, 12, 8, 5, 'Thầy Đức dạy SQL rất hay, từ cơ bản đến nâng cao. Phần index optimization rất thực tế.',                                                                             '2024-05-21 10:00:00'),
(11, 15, 8, 4, 'Học được nhiều thứ hay. Muốn có thêm bài tập với dữ liệu thực tế hơn.',                                                                                              '2024-10-15 09:00:00'),
-- Course 4: ML Python
(12, 10, 4, 4, 'Nội dung ML rất sâu sắc. Phần thực hành dự án cuối rất hay nhưng hơi khó với người mới.',                                                                            '2024-10-01 10:00:00');


-- ============================================================
-- 22. REVIEW_LIKES
-- ============================================================
INSERT INTO review_likes (user_id, review_id) VALUES
(7,  1),  -- Hoàng Văn An like review của Đặng Văn Cường
(8,  1),
(10, 1),
(11, 1),
(12, 1),
(7,  3),
(8,  6),
(9,  6),
(10, 6),
(7,  7),
(8,  7),
(9,  4),
(11, 4);


-- ============================================================
-- 23. COURSE_STATS (cập nhật theo reviews và enrollments)
-- ============================================================
INSERT INTO course_stats (course_id, total_enrollments, total_reviews, avg_rating, total_completions) VALUES
(1, 4, 3, 4.67, 1),
(2, 1, 0, 0.00, 0),
(3, 2, 2, 4.50, 0),
(4, 2, 1, 4.00, 0),
(5, 1, 0, 0.00, 0),
(6, 2, 1, 5.00, 1),
(7, 3, 3, 4.67, 3),
(8, 2, 2, 4.50, 1);


-- ============================================================
-- 24. ORDERS
-- ============================================================
INSERT INTO orders (id, user_id, total_amount, status, payment_method, transaction_id, created_at) VALUES
(1,  7,  299000.00, 'COMPLETED', 'VNPAY', 'VNPAY-20240905-001',  '2024-09-05 08:55:00'),
(2,  8,  349000.00, 'COMPLETED', 'MOMO',  'MOMO-20240910-002',   '2024-09-10 13:55:00'),
(3,  8,  199000.00, 'COMPLETED', 'MOMO',  'MOMO-20240701-003',   '2024-07-01 07:55:00'),
(4,  9,  299000.00, 'COMPLETED', 'VNPAY', 'VNPAY-20240902-004',  '2024-09-02 07:55:00'),
(5,  10, 449000.00, 'COMPLETED', 'MOMO',  'MOMO-20240801-005',   '2024-08-01 10:55:00'),
(6,  11, 379000.00, 'COMPLETED', 'VNPAY', 'VNPAY-20241105-006',  '2024-11-05 08:55:00'),
(7,  11, 299000.00, 'COMPLETED', 'VNPAY', 'VNPAY-20241001-007',  '2024-10-01 09:55:00'),
(8,  12, 149000.00, 'COMPLETED', 'FREE',  'FREE-20240501-008',   '2024-05-01 07:55:00'),
(9,  13, 499000.00, 'COMPLETED', 'VNPAY', 'VNPAY-20241020-009',  '2024-10-20 08:55:00'),
(10, 14, 348000.00, 'COMPLETED', 'MOMO',  'MOMO-20240915-010',   '2024-09-15 09:55:00'),
(11, 14, 199000.00, 'COMPLETED', 'MOMO',  'MOMO-20241001-011',   '2024-10-01 07:55:00'),
(12, 15, 149000.00, 'COMPLETED', 'VNPAY', 'VNPAY-20240610-012',  '2024-06-10 08:55:00'),
(13, 16, 449000.00, 'COMPLETED', 'MOMO',  'MOMO-20240815-013',   '2024-08-15 07:55:00');


-- ============================================================
-- 25. ORDER_ITEMS
-- ============================================================
INSERT INTO order_items (order_id, course_id, price) VALUES
(1,  1, 299000.00),
(2,  3, 349000.00),
(3,  6, 199000.00),
(4,  1, 299000.00),
(5,  4, 449000.00),
(6,  5, 379000.00),
(7,  1, 299000.00),
(8,  8, 149000.00),
(9,  2, 499000.00),
(10, 3, 349000.00),
(11, 6, 199000.00),
(12, 8, 149000.00),
(13, 4, 449000.00);


-- ============================================================
-- 26. CHAT_CONVERSATIONS
-- ============================================================
INSERT INTO chat_conversations (id, user_id, course_id, title, created_at, updated_at) VALUES
(1, 7,  1, 'Hỏi về JWT Authentication trong Spring Boot',     '2024-09-10 10:00:00', '2024-09-10 10:15:00'),
(2, 7,  1, 'Debug lỗi 403 Forbidden khi gọi API',            '2024-09-12 14:00:00', '2024-09-12 14:20:00'),
(3, 9,  1, 'Giải thích @Transactional hoạt động như thế nào', '2024-09-08 09:00:00', '2024-09-08 09:30:00'),
(4, 9,  NULL, 'So sánh Spring Boot vs Node.js cho Backend',   '2024-10-01 11:00:00', '2024-10-01 11:25:00'),
(5, 8,  3, 'Giải thích useEffect dependency array',           '2024-09-15 15:00:00', '2024-09-15 15:20:00'),
(6, 13, 4, 'Overfitting là gì và cách xử lý',                '2024-10-25 10:00:00', '2024-10-25 10:30:00');


-- ============================================================
-- 27. CHAT_MESSAGES
-- ============================================================
INSERT INTO chat_messages (conversation_id, role, content, tokens_used, created_at) VALUES
-- Conversation 1: JWT
(1, 'USER',      'Thầy ơi JWT Authentication hoạt động như thế nào vậy? Em đang bị confused về access token và refresh token.',        NULL, '2024-09-10 10:00:00'),
(1, 'ASSISTANT', 'JWT (JSON Web Token) là chuẩn mở để truyền thông tin an toàn giữa các bên dưới dạng JSON object. Cấu trúc gồm 3 phần: Header.Payload.Signature.\n\n**Access Token** thường có thời gian sống ngắn (15-30 phút) và được dùng để xác thực mỗi request. **Refresh Token** có thời gian sống dài hơn (7-14 ngày) và dùng để lấy access token mới khi hết hạn.\n\nFlow hoạt động:\n1. User login → server trả về access token + refresh token\n2. Client gửi access token trong header: `Authorization: Bearer <token>`\n3. Khi access token hết hạn (401), client dùng refresh token để lấy token mới\n4. Logout → revoke refresh token', 245, '2024-09-10 10:01:00'),
(1, 'USER',      'Vậy em nên lưu token ở đâu trong frontend? localStorage hay cookie?',                                                  NULL, '2024-09-10 10:05:00'),
(1, 'ASSISTANT', 'Đây là câu hỏi hay! Có 2 lựa chọn phổ biến:\n\n**localStorage:**\n- Ưu: Đơn giản, dễ implement\n- Nhược: Dễ bị XSS attack, JavaScript có thể đọc được\n\n**HttpOnly Cookie:**\n- Ưu: An toàn hơn, không thể đọc bằng JavaScript → chống XSS\n- Nhược: Cần handle CSRF, phức tạp hơn\n\n**Khuyến nghị thực tế:**\n- Access token → memory (JavaScript variable, không persist)\n- Refresh token → HttpOnly Secure Cookie\n\nTrong môi trường học tập, localStorage là OK để đơn giản hóa.',            198, '2024-09-10 10:06:00'),

-- Conversation 3: @Transactional
(3, 'USER',      '@Transactional hoạt động như thế nào và khi nào cần dùng?',                                                           NULL, '2024-09-08 09:00:00'),
(3, 'ASSISTANT', '@Transactional là annotation của Spring để quản lý transaction database. Khi một method được đánh dấu @Transactional:\n\n1. Spring tạo một transaction mới (hoặc tham gia vào transaction đang có)\n2. Nếu method thực thi thành công → COMMIT\n3. Nếu có RuntimeException → ROLLBACK tự động\n\n**Ví dụ quan trọng:**\n```java\n@Transactional\npublic void transferMoney(Long fromId, Long toId, BigDecimal amount) {\n    accountRepo.debit(fromId, amount);  // -tiền tài khoản A\n    accountRepo.credit(toId, amount);   // +tiền tài khoản B\n    // Nếu bước 2 fail → bước 1 cũng rollback\n}\n```\n\n**Khi nào dùng:** Bất kỳ operation nào cần tính atomic (all or nothing): chuyển tiền, enroll khóa học (tạo enrollment + lesson_progress cùng lúc).',  312, '2024-09-08 09:01:00'),

-- Conversation 5: useEffect
(5, 'USER',      'Em không hiểu dependency array trong useEffect, tại sao đôi khi để [] đôi khi để [variable]?',                         NULL, '2024-09-15 15:00:00'),
(5, 'ASSISTANT', 'Dependency array trong useEffect kiểm soát khi nào effect được chạy lại:\n\n```javascript\n// Chạy mỗi lần render\nuseEffect(() => { ... })\n\n// Chỉ chạy 1 lần khi mount (như componentDidMount)\nuseEffect(() => { ... }, [])\n\n// Chạy lại khi userId hoặc courseId thay đổi\nuseEffect(() => {\n  fetchCourse(userId, courseId)\n}, [userId, courseId])\n```\n\n**Rule quan trọng:** Mọi biến từ component scope được dùng bên trong useEffect đều phải có trong dependency array. ESLint rule `exhaustive-deps` sẽ nhắc bạn điều này.',  267, '2024-09-15 15:01:00');


-- ============================================================
-- VERIFY DATA
-- ============================================================
SELECT 'users'              AS table_name, COUNT(*) AS total FROM users
UNION ALL
SELECT 'user_roles',           COUNT(*) FROM user_roles
UNION ALL
SELECT 'user_profiles',        COUNT(*) FROM user_profiles
UNION ALL
SELECT 'categories',           COUNT(*) FROM categories
UNION ALL
SELECT 'courses',              COUNT(*) FROM courses
UNION ALL
SELECT 'course_categories',    COUNT(*) FROM course_categories
UNION ALL
SELECT 'sections',             COUNT(*) FROM sections
UNION ALL
SELECT 'lessons',              COUNT(*) FROM lessons
UNION ALL
SELECT 'quizzes',              COUNT(*) FROM quizzes
UNION ALL
SELECT 'questions',            COUNT(*) FROM questions
UNION ALL
SELECT 'answer_options',       COUNT(*) FROM answer_options
UNION ALL
SELECT 'assignments',          COUNT(*) FROM assignments
UNION ALL
SELECT 'enrollments',          COUNT(*) FROM enrollments
UNION ALL
SELECT 'lesson_progress',      COUNT(*) FROM lesson_progress
UNION ALL
SELECT 'course_certificates',  COUNT(*) FROM course_certificates
UNION ALL
SELECT 'quiz_attempts',        COUNT(*) FROM quiz_attempts
UNION ALL
SELECT 'quiz_answers',         COUNT(*) FROM quiz_answers
UNION ALL
SELECT 'reviews',              COUNT(*) FROM reviews
UNION ALL
SELECT 'review_likes',         COUNT(*) FROM review_likes
UNION ALL
SELECT 'course_stats',         COUNT(*) FROM course_stats
UNION ALL
SELECT 'orders',               COUNT(*) FROM orders
UNION ALL
SELECT 'order_items',          COUNT(*) FROM order_items
UNION ALL
SELECT 'chat_conversations',   COUNT(*) FROM chat_conversations
UNION ALL
SELECT 'chat_messages',        COUNT(*) FROM chat_messages;
