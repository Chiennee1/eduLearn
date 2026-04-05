package com.edulearn.quiz;

import com.edulearn.EduLearnApplication;
import com.edulearn.auth.entity.Role;
import com.edulearn.auth.entity.RoleName;
import com.edulearn.auth.entity.User;
import com.edulearn.auth.entity.UserStatus;
import com.edulearn.auth.repository.RoleRepository;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.auth.security.UserPrincipal;
import com.edulearn.auth.service.JwtService;
import com.edulearn.review.entity.CourseStats;
import com.edulearn.review.repository.CourseStatsRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EduLearnApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuizReviewIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private CourseStatsRepository courseStatsRepository;

    @BeforeEach
    void setUp() {
        ensureRole(RoleName.ADMIN);
        ensureRole(RoleName.INSTRUCTOR);
        ensureRole(RoleName.STUDENT);
    }

    @Test
    void quizAndReviewFlow_shouldPersistAttemptsAndCourseStats() throws Exception {
        String instructorToken = createTokenForRoles(Set.of(RoleName.INSTRUCTOR));
        String studentToken = createTokenForRoles(Set.of(RoleName.STUDENT));

        Integer categoryId = createCategory(instructorToken);
        Long courseId = createCourse(instructorToken, categoryId);
        Long lessonId = createSectionAndLesson(instructorToken, courseId);
        publishCourse(instructorToken, courseId);
        enrollCourse(studentToken, courseId);

        Long quizId = createQuiz(instructorToken, lessonId);
        Long adminQuizId = createQuiz(instructorToken, lessonId);

        MvcResult quizBeforeUpdateResult = mockMvc.perform(get("/api/v1/quizzes/{quizId}", quizId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode quizBeforeUpdate = objectMapper.readTree(quizBeforeUpdateResult.getResponse().getContentAsString())
                .path("data");
        long updateQuestionId = quizBeforeUpdate.path("questions").get(0).path("id").asLong();
        long updateOptionId = quizBeforeUpdate.path("questions").get(0).path("options").get(0).path("id").asLong();

        mockMvc.perform(put("/api/v1/quizzes/{quizId}", quizId)
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Final Quiz Updated",
                                "passScore", 75,
                                "timeLimitMins", 45
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Final Quiz Updated"));

        mockMvc.perform(put("/api/v1/quizzes/{quizId}/questions/{questionId}", quizId, updateQuestionId)
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "content", "Spring Boot is based on?",
                                "type", "SINGLE",
                                "points", 50,
                                "orderIndex", 1
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questions[0].content").value("Spring Boot is based on?"));

        mockMvc.perform(put("/api/v1/quizzes/{quizId}/questions/{questionId}/options/{optionId}",
                        quizId,
                        updateQuestionId,
                        updateOptionId)
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "content", "Spring Framework Core",
                                "correct", true,
                                "orderIndex", 1
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questions[0].options[0].content").value("Spring Framework Core"));

        MvcResult quizResult = mockMvc.perform(get("/api/v1/quizzes/{quizId}", quizId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode quizPayload = objectMapper.readTree(quizResult.getResponse().getContentAsString());
        JsonNode questions = quizPayload.path("data").path("questions");

        long q1 = questions.get(0).path("id").asLong();
        long q2 = questions.get(1).path("id").asLong();
        long q1Correct = questions.get(0).path("options").get(0).path("id").asLong();
        long q1Wrong = questions.get(0).path("options").get(1).path("id").asLong();
        long q2Correct = questions.get(1).path("options").get(0).path("id").asLong();

        mockMvc.perform(post("/api/v1/quizzes/{quizId}/submit", quizId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "answers", List.of(
                                        Map.of("questionId", q1, "selectedOptionId", q1Wrong),
                                        Map.of("questionId", q2, "selectedOptionId", q2Correct)
                                )
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.passed").value(false));

        mockMvc.perform(post("/api/v1/quizzes/{quizId}/submit", quizId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "answers", List.of(
                                        Map.of("questionId", q1, "selectedOptionId", q1Correct),
                                        Map.of("questionId", q2, "selectedOptionId", q2Correct)
                                )
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.passed").value(true));

        mockMvc.perform(get("/api/v1/quizzes/history")
                        .header("Authorization", "Bearer " + studentToken)
                        .param("quizId", String.valueOf(quizId))
                        .param("passed", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].quizId").value(quizId))
                .andExpect(jsonPath("$.data.content[0].passed").value(true));

        mockMvc.perform(get("/api/v1/quizzes/history")
                        .header("Authorization", "Bearer " + studentToken)
                        .param("sortBy", "hack"))
                .andExpect(status().isBadRequest());

        MvcResult adminQuizResult = mockMvc.perform(get("/api/v1/quizzes/{quizId}", adminQuizId)
                        .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode adminQuizData = objectMapper.readTree(adminQuizResult.getResponse().getContentAsString()).path("data");
        long adminQuestionToDelete = adminQuizData.path("questions").get(1).path("id").asLong();
        long adminQuestionForOption = adminQuizData.path("questions").get(0).path("id").asLong();
        long adminOptionToDelete = adminQuizData.path("questions").get(0).path("options").get(1).path("id").asLong();

        mockMvc.perform(delete("/api/v1/quizzes/{quizId}/questions/{questionId}/options/{optionId}",
                        adminQuizId,
                        adminQuestionForOption,
                        adminOptionToDelete)
                        .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/quizzes/{quizId}/questions/{questionId}", adminQuizId, adminQuestionToDelete)
                        .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk());

        MvcResult reviewResult = mockMvc.perform(post("/api/v1/courses/{courseId}/reviews", courseId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "rating", 5,
                                "content", "Excellent course"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.rating").value(5))
                .andReturn();

        Long reviewId = extractLong(reviewResult, "data.id");

        mockMvc.perform(get("/api/v1/courses/{courseId}/reviews", courseId)
                        .param("page", "0")
                        .param("size", "5")
                        .param("sortBy", "rating")
                        .param("sortDir", "desc")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.page").value(0))
                .andExpect(jsonPath("$.data.size").value(5));

        mockMvc.perform(get("/api/v1/courses/{courseId}/reviews", courseId)
                        .param("sortBy", "hack")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/v1/reviews/{reviewId}/like", reviewId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(1));

        mockMvc.perform(delete("/api/v1/reviews/{reviewId}/like", reviewId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.likeCount").value(0));

        CourseStats stats = courseStatsRepository.findByCourseId(courseId).orElseThrow();
        assertEquals(1, stats.getTotalReviews());
        assertEquals(new BigDecimal("5.00"), stats.getAvgRating());
        assertTrue(stats.getTotalEnrollments() >= 1);
    }

    private Integer createCategory(String instructorToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Quiz Review " + UUID.randomUUID(),
                                "slug", "quiz-review-" + UUID.randomUUID()
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        return extractInt(result, "data.id");
    }

    private Long createCourse(String instructorToken, Integer categoryId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/courses")
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Quiz Course " + UUID.randomUUID(),
                                "price", 149000,
                                "level", "BEGINNER",
                                "language", "vi",
                                "durationHours", 10,
                                "categoryIds", Set.of(categoryId)
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        return extractLong(result, "data.id");
    }

    private Long createSectionAndLesson(String instructorToken, Long courseId) throws Exception {
        MvcResult sectionResult = mockMvc.perform(post("/api/v1/courses/{courseId}/sections", courseId)
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Section Quiz",
                                "orderIndex", 1
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Long sectionId = extractLong(sectionResult, "data.id");

        MvcResult lessonResult = mockMvc.perform(post("/api/v1/sections/{sectionId}/lessons", sectionId)
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Lesson Quiz",
                                "type", "VIDEO",
                                "contentUrl", "https://cdn.edulearn.test/quiz.mp4",
                                "durationSeconds", 900,
                                "preview", false,
                                "orderIndex", 1
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        return extractLong(lessonResult, "data.id");
    }

    private void publishCourse(String instructorToken, Long courseId) throws Exception {
        mockMvc.perform(post("/api/v1/courses/{courseId}/publish", courseId)
                        .header("Authorization", "Bearer " + instructorToken))
                .andExpect(status().isOk());
    }

    private void enrollCourse(String studentToken, Long courseId) throws Exception {
        mockMvc.perform(post("/api/v1/courses/{courseId}/enrollments", courseId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk());
    }

    private Long createQuiz(String instructorToken, Long lessonId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/lessons/{lessonId}/quizzes", lessonId)
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Final Quiz",
                                "passScore", 80,
                                "questions", List.of(
                                        Map.of(
                                                "content", "Spring Boot is built on?",
                                                "type", "SINGLE",
                                                "points", 50,
                                                "orderIndex", 1,
                                                "options", List.of(
                                                        Map.of("content", "Spring Framework", "correct", true, "orderIndex", 1),
                                                        Map.of("content", "Express", "correct", false, "orderIndex", 2)
                                                )
                                        ),
                                        Map.of(
                                                "content", "HTTP status for success?",
                                                "type", "SINGLE",
                                                "points", 50,
                                                "orderIndex", 2,
                                                "options", List.of(
                                                        Map.of("content", "200", "correct", true, "orderIndex", 1),
                                                        Map.of("content", "500", "correct", false, "orderIndex", 2)
                                                )
                                        )
                                )
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        return extractLong(result, "data.id");
    }

    private void ensureRole(RoleName roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            roleRepository.save(Role.builder().name(roleName).description(roleName.name()).build());
        }
    }

    private String createTokenForRoles(Set<RoleName> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));
            roles.add(role);
        }

        User user = User.builder()
                .email("user-" + UUID.randomUUID() + "@edulearn.com")
                .passwordHash(passwordEncoder.encode("123456"))
                .fullName("Test User")
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .roles(roles)
                .build();
        user = userRepository.save(user);

        return jwtService.generateAccessToken(UserPrincipal.from(user));
    }

    private Long extractLong(MvcResult result, String path) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.at("/" + path.replace('.', '/')).asLong();
    }

    private Integer extractInt(MvcResult result, String path) throws Exception {
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        return root.at("/" + path.replace('.', '/')).asInt();
    }
}


