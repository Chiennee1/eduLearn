package com.edulearn.chatbot;

import com.edulearn.EduLearnApplication;
import com.edulearn.auth.entity.Role;
import com.edulearn.auth.entity.RoleName;
import com.edulearn.auth.entity.User;
import com.edulearn.auth.entity.UserStatus;
import com.edulearn.auth.repository.RoleRepository;
import com.edulearn.auth.repository.UserRepository;
import com.edulearn.auth.security.UserPrincipal;
import com.edulearn.auth.service.JwtService;
import com.edulearn.course.entity.Course;
import com.edulearn.course.entity.Enrollment;
import com.edulearn.course.repository.CourseRepository;
import com.edulearn.course.repository.EnrollmentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
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

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EduLearnApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ChatControllerIntegrationTest {

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
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @BeforeEach
    void setUp() {
        ensureRole(RoleName.ADMIN);
        ensureRole(RoleName.INSTRUCTOR);
        ensureRole(RoleName.STUDENT);
    }

    @Test
    void ask_shouldSaveConversationAndMessages() throws Exception {
        String studentToken = createTokenForRoles(Set.of(RoleName.STUDENT));

        MvcResult askResult = mockMvc.perform(post("/api/v1/chat/ask")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("message", "Giup toi hoc Java"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.assistantReply").value(org.hamcrest.Matchers.containsString("[MOCK]")))
                .andReturn();

        Long conversationId = objectMapper.readTree(askResult.getResponse().getContentAsString())
                .path("data")
                .path("conversationId")
                .asLong();

        mockMvc.perform(get("/api/v1/chat/conversations")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(conversationId));

        mockMvc.perform(get("/api/v1/chat/conversations/{conversationId}/messages", conversationId)
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].role").value("USER"))
                .andExpect(jsonPath("$.data[1].role").value("ASSISTANT"));
    }

    @Test
    void ask_shouldEnforceRateLimit() throws Exception {
        String studentToken = createTokenForRoles(Set.of(RoleName.STUDENT));

        mockMvc.perform(post("/api/v1/chat/ask")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("message", "Lan 1"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/chat/ask")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("message", "Lan 2"))))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/v1/chat/ask")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("message", "Lan 3"))))
                .andExpect(status().isTooManyRequests());
    }

    @Test
    void stream_shouldReturnSseChunksAndDoneEvent() throws Exception {
        String studentToken = createTokenForRoles(Set.of(RoleName.STUDENT));

        MvcResult streamResult = mockMvc.perform(get("/api/v1/chat/stream")
                        .header("Authorization", "Bearer " + studentToken)
                        .param("message", "Xin chao ban"))
                .andExpect(request().asyncStarted())
                .andExpect(status().isOk())
                .andReturn();

        String sseBody = "";
        for (int i = 0; i < 40; i++) {
            Thread.sleep(50);
            sseBody = streamResult.getResponse().getContentAsString();
            if (sseBody.contains("event:done")) {
                break;
            }
        }

        assertTrue(sseBody.contains("event:chunk"));
        assertTrue(sseBody.contains("event:done"));
    }

    @Test
    void getConversationMessages_shouldNotAllowOtherUserAccess() throws Exception {
        String ownerToken = createTokenForRoles(Set.of(RoleName.STUDENT));
        String attackerToken = createTokenForRoles(Set.of(RoleName.STUDENT));

        MvcResult askResult = mockMvc.perform(post("/api/v1/chat/ask")
                        .header("Authorization", "Bearer " + ownerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("message", "Hello owner conversation"))))
                .andExpect(status().isOk())
                .andReturn();

        Long conversationId = objectMapper.readTree(askResult.getResponse().getContentAsString())
                .path("data")
                .path("conversationId")
                .asLong();

        mockMvc.perform(get("/api/v1/chat/conversations/{conversationId}/messages", conversationId)
                        .header("Authorization", "Bearer " + attackerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void ask_withCourseContext_shouldRespectPermissionMatrix() throws Exception {
        User admin = createUserForRoles(Set.of(RoleName.ADMIN));
        User instructorOwner = createUserForRoles(Set.of(RoleName.INSTRUCTOR));
        User instructorOther = createUserForRoles(Set.of(RoleName.INSTRUCTOR));
        User enrolledStudent = createUserForRoles(Set.of(RoleName.STUDENT));
        User notEnrolledStudent = createUserForRoles(Set.of(RoleName.STUDENT));

        Course course = courseRepository.save(Course.builder()
                .instructor(instructorOwner)
                .title("Chat Perm Course " + UUID.randomUUID())
                .description("Course used by chatbot permission test")
                .price(BigDecimal.ZERO)
                .build());

        enrollmentRepository.save(Enrollment.builder()
                .user(enrolledStudent)
                .course(course)
                .build());

        assertAskWithCourseStatus(tokenForUser(admin), course.getId(), 200);
        assertAskWithCourseStatus(tokenForUser(instructorOwner), course.getId(), 200);
        assertAskWithCourseStatus(tokenForUser(enrolledStudent), course.getId(), 200);

        assertAskWithCourseStatus(tokenForUser(instructorOther), course.getId(), 403);
        assertAskWithCourseStatus(tokenForUser(notEnrolledStudent), course.getId(), 403);
    }

    private void ensureRole(RoleName roleName) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            roleRepository.save(Role.builder().name(roleName).description(roleName.name()).build());
        }
    }

    private String createTokenForRoles(Set<RoleName> roleNames) {
        User user = createUserForRoles(roleNames);
        return tokenForUser(user);
    }

    private User createUserForRoles(Set<RoleName> roleNames) {
        Set<Role> roles = new HashSet<>();
        for (RoleName roleName : roleNames) {
            Role role = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalStateException("Role not found: " + roleName));
            roles.add(role);
        }

        User user = User.builder()
                .email("user-" + UUID.randomUUID() + "@edulearn.com")
                .passwordHash(passwordEncoder.encode("123456"))
                .fullName("Chat Test User")
                .status(UserStatus.ACTIVE)
                .emailVerified(true)
                .roles(roles)
                .build();
        return userRepository.save(user);
    }

    private String tokenForUser(User user) {
        return jwtService.generateAccessToken(UserPrincipal.from(user));
    }

    private void assertAskWithCourseStatus(String token, Long courseId, int statusCode) throws Exception {
        mockMvc.perform(post("/api/v1/chat/ask")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "courseId", courseId,
                                "message", "Cho toi goi y hoc tap"
                        ))))
                .andExpect(status().is(statusCode));
    }
}

