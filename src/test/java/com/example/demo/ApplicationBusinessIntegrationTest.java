package com.example.demo;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.demo.config.AdminAuthFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Kiểm thử tích hợp các luồng nghiệp vụ chính (HTTP + filter + persistence H2).
 */
@SpringBootTest
@ActiveProfiles("test")
class ApplicationBusinessIntegrationTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private AdminAuthFilter adminAuthFilter;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				.addFilters(adminAuthFilter)
				.build();
	}

	@Test
	@DisplayName("IT-00: Spring context khởi tạo thành công (JPA + H2 profile test)")
	void it00_contextLoads() {
		// @SpringBootTest đã đảm bảo context không lỗi; method giữ làm mốc smoke test.
	}

	@Test
	@DisplayName("IT-01: Trang chủ — GET / trả 200 và view home")
	void it01_homePage() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(view().name("home"));
	}

	@Test
	@DisplayName("IT-02: Admin — chưa đăng nhập truy cập /admin bị chuyển tới /login")
	void it02_adminRequiresLogin() throws Exception {
		mockMvc.perform(get("/admin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	@DisplayName("IT-03: Đăng nhập admin — POST /login đúng mật khẩu rồi GET /admin trả dashboard")
	void it03_loginThenAccessAdminDashboard() throws Exception {
		MockHttpSession session = new MockHttpSession();
		mockMvc.perform(post("/login")
				.param("username", "itadmin")
				.param("password", "itsecret")
				.session(session))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/admin"));

		mockMvc.perform(get("/admin").session(session))
			.andExpect(status().isOk())
			.andExpect(view().name("admin/index"));
	}

	@Test
	@DisplayName("IT-04: Danh mục — GET /danhmuc/{maDM} hiển thị danh sách bài học")
	void it04_categoryLessonList() throws Exception {
		mockMvc.perform(get("/danhmuc/1"))
			.andExpect(status().isOk())
			.andExpect(view().name("baihoc-list"));
	}

	@Test
	@DisplayName("IT-05: Bài học — GET /baihoc/{maBH} không tồn tại chuyển về trang chủ")
	void it05_unknownLessonRedirectsHome() throws Exception {
		mockMvc.perform(get("/baihoc/999999"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/"));
	}

	@Test
	@DisplayName("IT-06: API trắc nghiệm — POST /api/quiz/check với mã đáp án không tồn tại")
	void it06_quizCheckUnknownAnswer() throws Exception {
		mockMvc.perform(post("/api/quiz/check")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"maDA\":-1}"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$.ok", is(false)));
	}

	@Test
	@DisplayName("IT-07: API lab — POST /api/lab/check với mã lab không tồn tại")
	void it07_labCheckUnknownLab() throws Exception {
		mockMvc.perform(post("/api/lab/check")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"maTH\":-1,\"userState\":\"{}\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.ok", is(false)));
	}
}
