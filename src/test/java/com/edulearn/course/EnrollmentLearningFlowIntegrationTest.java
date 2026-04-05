package com.edulearn.course;

import com.edulearn.EduLearnApplication;
import com.edulearn.auth.entity.Role;
import com.edulearn.auth.entity.RoleName;
import com.edulearn.auth.entity.User;
import com.edulearn.auth.entity.UserStatus;
import com.edulearn.auth.repository.RoleRepository;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.auth.security.UserPrincipal;
import com.edulearn.auth.service.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EduLearnApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EnrollmentLearningFlowIntegrationTest {

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

    @BeforeEach
    void setUp() {
        ensureRole(RoleName.ADMIN);
        ensureRole(RoleName.INSTRUCTOR);
        ensureRole(RoleName.STUDENT);
    }

    @Test
    void enrollLearnAndCertificateFlow_shouldSucceed() throws Exception {
        String instructorToken = createTokenForRoles(Set.of(RoleName.INSTRUCTOR));
        String studentToken = createTokenForRoles(Set.of(RoleName.STUDENT));

        Integer categoryId = createCategory(instructorToken);
        Long courseId = createPublishedCourse(instructorToken, categoryId);
        Long lessonId = createSectionAndLesson(instructorToken, courseId);

        MvcResult enrollResult = mockMvc.perform(post("/api/v1/courses/{courseId}/enrollments", courseId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.totalLessons").value(1))
                .andReturn();

        Long enrollmentId = extractLong(enrollResult, "data.id");

        mockMvc.perform(get("/api/v1/enrollments/{enrollmentId}/progress", enrollmentId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].lessonId").value(lessonId));

        mockMvc.perform(patch("/api/v1/enrollments/{enrollmentId}/lessons/{lessonId}/progress", enrollmentId, lessonId)
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "watchedSeconds", 600,
                                "completed", true
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.completed").value(true));

        mockMvc.perform(get("/api/v1/enrollments/{enrollmentId}/certificate", enrollmentId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.certificateCode").isNotEmpty());

        mockMvc.perform(get("/api/v1/learning/dashboard")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalEnrollments").value(1))
                .andExpect(jsonPath("$.data.completedEnrollments").value(1))
                .andExpect(jsonPath("$.data.courses[0].progressPercent").value(100));
    }

    @Test
    void enroll_withInstructorRole_shouldBeForbidden() throws Exception {
        String instructorToken = createTokenForRoles(Set.of(RoleName.INSTRUCTOR));

        mockMvc.perform(post("/api/v1/courses/{courseId}/enrollments", 999)
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    private Integer createCategory(String instructorToken) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/categories")
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Learning Flow " + UUID.randomUUID(),
                                "slug", "learning-flow-" + UUID.randomUUID()
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        return extractInt(result, "data.id");
    }

    private Long createPublishedCourse(String instructorToken, Integer categoryId) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/v1/courses")
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Enrollment Flow " + UUID.randomUUID(),
                                "price", 199000,
                                "level", "BEGINNER",
                                "language", "vi",
                                "durationHours", 8,
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
                                "title", "Section A",
                                "orderIndex", 1
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Long sectionId = extractLong(sectionResult, "data.id");

        MvcResult lessonResult = mockMvc.perform(post("/api/v1/sections/{sectionId}/lessons", sectionId)
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "title", "Lesson A",
                                "type", "VIDEO",
                                "contentUrl", "https://cdn.edulearn.test/lesson.mp4",
                                "durationSeconds", 600,
                                "preview", false,
                                "orderIndex", 1
                        ))))
                .andExpect(status().isOk())
                .andReturn();

        Long lessonId = extractLong(lessonResult, "data.id");

        mockMvc.perform(post("/api/v1/courses/{courseId}/publish", courseId)
                        .header("Authorization", "Bearer " + instructorToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));

        return lessonId;
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

