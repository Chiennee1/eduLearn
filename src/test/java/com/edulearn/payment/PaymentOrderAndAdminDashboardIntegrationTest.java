package com.edulearn.payment;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EduLearnApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PaymentOrderAndAdminDashboardIntegrationTest {

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
	void checkoutOrder_shouldCreateCompletedOrderAndEnrollment() throws Exception {
		String instructorToken = createTokenForRoles(Set.of(RoleName.INSTRUCTOR));
		String studentToken = createTokenForRoles(Set.of(RoleName.STUDENT));

		Integer categoryId = createCategory(instructorToken);
		Long courseId = createPublishedCourse(instructorToken, categoryId);

		mockMvc.perform(post("/api/v1/courses/{courseId}/orders", courseId)
						.header("Authorization", "Bearer " + studentToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("paymentMethod", "MOMO"))))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.order.status").value("COMPLETED"))
				.andExpect(jsonPath("$.data.enrollment.status").value("ACTIVE"));

		mockMvc.perform(get("/api/v1/orders/me")
						.header("Authorization", "Bearer " + studentToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data[0].status").value("COMPLETED"))
				.andExpect(jsonPath("$.data[0].items[0].courseId").value(courseId));
	}

	@Test
	void adminDashboard_shouldRequireAdminAndReturnStats() throws Exception {
		String adminToken = createTokenForRoles(Set.of(RoleName.ADMIN));
		String studentToken = createTokenForRoles(Set.of(RoleName.STUDENT));

		mockMvc.perform(get("/api/v1/admin/dashboard")
						.header("Authorization", "Bearer " + studentToken))
				.andExpect(status().isForbidden());

		mockMvc.perform(get("/api/v1/admin/dashboard")
						.header("Authorization", "Bearer " + adminToken))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.data.totalUsers").isNumber())
				.andExpect(jsonPath("$.data.totalRevenue").exists());
	}

	private Integer createCategory(String instructorToken) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/categories")
						.header("Authorization", "Bearer " + instructorToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of(
								"name", "Payment Flow " + UUID.randomUUID(),
								"slug", "payment-flow-" + UUID.randomUUID()
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
								"title", "Order Flow " + UUID.randomUUID(),
								"price", 99000,
								"level", "BEGINNER",
								"language", "vi",
								"durationHours", 6,
								"categoryIds", Set.of(categoryId)
						))))
				.andExpect(status().isOk())
				.andReturn();

		Long courseId = extractLong(result, "data.id");

		MvcResult sectionResult = mockMvc.perform(post("/api/v1/courses/{courseId}/sections", courseId)
						.header("Authorization", "Bearer " + instructorToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of(
								"title", "Section Payment",
								"orderIndex", 1
						))))
				.andExpect(status().isOk())
				.andReturn();

		Long sectionId = extractLong(sectionResult, "data.id");

		mockMvc.perform(post("/api/v1/sections/{sectionId}/lessons", sectionId)
						.header("Authorization", "Bearer " + instructorToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of(
								"title", "Lesson Payment",
								"type", "VIDEO",
								"contentUrl", "https://cdn.edulearn.test/payment.mp4",
								"durationSeconds", 300,
								"preview", false,
								"orderIndex", 1
						))))
				.andExpect(status().isOk());

		mockMvc.perform(post("/api/v1/courses/{courseId}/publish", courseId)
						.header("Authorization", "Bearer " + instructorToken)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());

		return courseId;
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
				.fullName("Payment Test User")
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

