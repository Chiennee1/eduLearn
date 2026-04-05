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
import com.edulearn.common.Constants;
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
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = EduLearnApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CourseCachingIntegrationTest {

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
	private CacheManager cacheManager;

	@BeforeEach
	void setUp() {
		ensureRole(RoleName.ADMIN);
		ensureRole(RoleName.INSTRUCTOR);
		ensureRole(RoleName.STUDENT);
	}

	@Test
	void publishedCourseList_shouldUseCacheAndEvictAfterUpdate() throws Exception {
		String instructorToken = createTokenForRoles(Set.of(RoleName.INSTRUCTOR));
		Integer categoryId = createCategory(instructorToken);
		Long courseId = createPublishedCourse(instructorToken, categoryId);

		Cache cache = cacheManager.getCache(Constants.CACHE_PUBLISHED_COURSES);
		assertNotNull(cache);
		cache.clear();

		mockMvc.perform(get("/api/v1/courses"))
				.andExpect(status().isOk());
		assertNotNull(cache.get("all"));

		mockMvc.perform(get("/api/v1/courses"))
				.andExpect(status().isOk());

		mockMvc.perform(put("/api/v1/courses/{courseId}", courseId)
						.header("Authorization", "Bearer " + instructorToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of("title", "Updated " + UUID.randomUUID()))))
				.andExpect(status().isOk());

		assertNull(cache.get("all"));

		mockMvc.perform(get("/api/v1/courses"))
				.andExpect(status().isOk());

		assertNotNull(cache.get("all"));
	}

	private Integer createCategory(String instructorToken) throws Exception {
		MvcResult result = mockMvc.perform(post("/api/v1/categories")
						.header("Authorization", "Bearer " + instructorToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of(
								"name", "Cache Test " + UUID.randomUUID(),
								"slug", "cache-test-" + UUID.randomUUID()
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
								"title", "Cache Flow " + UUID.randomUUID(),
								"price", 100000,
								"level", "BEGINNER",
								"language", "vi",
								"durationHours", 2,
								"categoryIds", Set.of(categoryId)
						))))
				.andExpect(status().isOk())
				.andReturn();

		Long courseId = extractLong(result, "data.id");

		MvcResult sectionResult = mockMvc.perform(post("/api/v1/courses/{courseId}/sections", courseId)
						.header("Authorization", "Bearer " + instructorToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of(
								"title", "Cache Section",
								"orderIndex", 1
						))))
				.andExpect(status().isOk())
				.andReturn();

		Long sectionId = extractLong(sectionResult, "data.id");

		mockMvc.perform(post("/api/v1/sections/{sectionId}/lessons", sectionId)
						.header("Authorization", "Bearer " + instructorToken)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(Map.of(
								"title", "Cache Lesson",
								"type", "VIDEO",
								"contentUrl", "https://cdn.edulearn.test/cache.mp4",
								"durationSeconds", 120,
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
				.fullName("Cache Test User")
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


