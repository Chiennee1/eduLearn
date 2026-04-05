package com.edulearn.course;

import edulearn.com.spring_web.EduLearnApplication;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EduLearnApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CoursePublishFlowIntegrationTest {

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
    void publishFlow_createCourseSectionLessonThenPublish_shouldSucceed() throws Exception {
        String instructorToken = createTokenForRoles(Set.of(RoleName.INSTRUCTOR));

        MvcResult categoryResult = mockMvc.perform(
                        post("/api/v1/categories")
                                .header("Authorization", "Bearer " + instructorToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                        "name", "Spring Backend",
                                        "slug", "spring-backend"
                                )))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        Integer categoryId = extractInt(categoryResult, "data.id");

        MvcResult courseResult = mockMvc.perform(
                        post("/api/v1/courses")
                                .header("Authorization", "Bearer " + instructorToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                        "title", "Spring Boot Fundamentals",
                                        "price", 199000,
                                        "level", "BEGINNER",
                                        "language", "vi",
                                        "durationHours", 12,
                                        "categoryIds", Set.of(categoryId)
                                )))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DRAFT"))
                .andReturn();

        Long courseId = extractLong(courseResult, "data.id");

        MvcResult sectionResult = mockMvc.perform(
                        post("/api/v1/courses/{courseId}/sections", courseId)
                                .header("Authorization", "Bearer " + instructorToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                        "title", "Introduction",
                                        "orderIndex", 1
                                )))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        Long sectionId = extractLong(sectionResult, "data.id");

        mockMvc.perform(
                        post("/api/v1/sections/{sectionId}/lessons", sectionId)
                                .header("Authorization", "Bearer " + instructorToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                        "title", "Welcome",
                                        "type", "VIDEO",
                                        "contentUrl", "https://cdn.edulearn.test/intro.mp4",
                                        "durationSeconds", 300,
                                        "preview", true,
                                        "orderIndex", 1
                                )))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        mockMvc.perform(
                        post("/api/v1/courses/{courseId}/publish", courseId)
                                .header("Authorization", "Bearer " + instructorToken)
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));

        mockMvc.perform(get("/api/v1/courses/{courseId}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("PUBLISHED"));
    }

    @Test
    void createCourse_withStudentRole_shouldBeForbidden() throws Exception {
        String studentToken = createTokenForRoles(Set.of(RoleName.STUDENT));

        mockMvc.perform(
                        post("/api/v1/courses")
                                .header("Authorization", "Bearer " + studentToken)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of(
                                        "title", "Unauthorized Course",
                                        "price", 100,
                                        "level", "BEGINNER",
                                        "language", "vi",
                                        "durationHours", 1
                                )))
                )
                .andExpect(status().isForbidden());
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

