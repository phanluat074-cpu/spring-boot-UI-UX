package com.example.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import com.example.demo.baihoc.BaiHoc;
import com.example.demo.baihoc.BaiHocRepository;
import com.example.demo.baihoc.BaiHocService;
import com.example.demo.config.AdminAuthFilter;
import com.example.demo.config.AdminValidationHelper;
import com.example.demo.danhmuc.DanhMuc;
import com.example.demo.danhmuc.DanhMucRepository;
import com.example.demo.danhmuc.DanhMucService;
import com.example.demo.dapan.DapAn;
import com.example.demo.dapan.DapAnRepository;
import com.example.demo.lab.InteractiveLab;
import com.example.demo.lab.InteractiveLabRepository;
import com.example.demo.tainguyen.TaiNguyen;
import com.example.demo.tainguyen.TaiNguyenRepository;
import com.example.demo.tracnghiem.TracNghiem;
import com.example.demo.tracnghiem.TracNghiemRepository;
import com.example.demo.web.AuthController;
import com.example.demo.web.BaiHocController;
import jakarta.servlet.FilterChain;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * Kiểm thử hộp trắng cho toàn bộ logic nhánh/điều kiện/đường đi của dự án:
 * AdminValidationHelper, AdminPagingUtil, AdminAuthFilter, AuthController,
 * DanhMucService, BaiHocService, BaiHocController (private helpers), Admin CRUD.
 */
@SpringBootTest
@ActiveProfiles("test")
class WhiteBoxTest {

	@Autowired
	private WebApplicationContext webApplicationContext;
	@Autowired
	private AdminAuthFilter adminAuthFilter;
	@Autowired
	private AuthController authController;
	@Autowired
	private BaiHocController baiHocController;
	@Autowired
	private DanhMucService danhMucService;
	@Autowired
	private BaiHocService baiHocService;
	@Autowired
	private DanhMucRepository danhMucRepository;
	@Autowired
	private BaiHocRepository baiHocRepository;
	@Autowired
	private TracNghiemRepository tracNghiemRepository;
	@Autowired
	private DapAnRepository dapAnRepository;
	@Autowired
	private TaiNguyenRepository taiNguyenRepository;
	@Autowired
	private InteractiveLabRepository interactiveLabRepository;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
			.addFilters(adminAuthFilter)
			.build();
		dapAnRepository.deleteAll();
		tracNghiemRepository.deleteAll();
		taiNguyenRepository.deleteAll();
		interactiveLabRepository.deleteAll();
		baiHocRepository.deleteAll();
		danhMucRepository.deleteAll();
	}

	private MockHttpSession adminSession() throws Exception {
		MockHttpSession session = new MockHttpSession();
		mockMvc.perform(post("/login")
				.param("username", "itadmin")
				.param("password", "itsecret")
				.session(session))
			.andExpect(redirectedUrl("/admin"));
		return session;
	}

	private static Object invoke(Object target, String name, Class<?>[] types, Object... args) throws Exception {
		Class<?> type = target instanceof Class<?> c ? c : target.getClass();
		Method method = type.getDeclaredMethod(name, types);
		method.setAccessible(true);
		return method.invoke(target instanceof Class<?> ? null : target, args);
	}

	private DanhMuc saveDm(String ten) {
		DanhMuc dm = new DanhMuc();
		dm.setTenDanhMuc(ten);
		dm.setIconURL("https://example.com/i.png");
		return danhMucRepository.save(dm);
	}

	private BaiHoc saveBh(Integer maDM, String ten, String link) {
		BaiHoc b = new BaiHoc();
		b.setTenBH(ten);
		b.setNoiDungBH("Nội dung " + ten);
		b.setLinkURL(link);
		b.setMaDM(maDM);
		b.setNgayDang(LocalDateTime.now());
		return baiHocRepository.save(b);
	}

	/* ═══════════════════════════════════════════════════════════
	 *  AdminValidationHelper — statement / branch / condition
	 * ═══════════════════════════════════════════════════════════ */

	@Nested
	@DisplayName("WB: AdminValidationHelper")
	class ValidationHelperWhiteBox {

		@Test
		@DisplayName("Constructor private vẫn gọi được (phủ câu lệnh khởi tạo)")
		void privateConstructor() throws Exception {
			Constructor<AdminValidationHelper> ctor = AdminValidationHelper.class.getDeclaredConstructor();
			ctor.setAccessible(true);
			assertNotNull(ctor.newInstance());
		}

		@Test
		@DisplayName("validateDanhMuc: trống / quá dài / icon dài / icon không URL / hợp lệ")
		void validateDanhMucBranches() {
			assertTrue(AdminValidationHelper.validateDanhMuc(null, null).containsKey("tenDanhMuc"));
			assertTrue(AdminValidationHelper.validateDanhMuc("   ", "").containsKey("tenDanhMuc"));
			assertTrue(AdminValidationHelper.validateDanhMuc("x".repeat(201), null).containsKey("tenDanhMuc"));
			assertFalse(AdminValidationHelper.validateDanhMuc("UI", null).containsKey("iconURL"));
			assertFalse(AdminValidationHelper.validateDanhMuc("UI", "  ").containsKey("iconURL"));
			assertTrue(AdminValidationHelper.validateDanhMuc("UI", "x".repeat(501)).containsKey("iconURL"));
			assertTrue(AdminValidationHelper.validateDanhMuc("UI", "ftp://x").containsKey("iconURL"));
			assertTrue(AdminValidationHelper.validateDanhMuc("UI", "https://ok").isEmpty());
			assertTrue(AdminValidationHelper.validateDanhMuc("UI", "http://ok").isEmpty());
		}

		@Test
		@DisplayName("validateBaiHoc: từng điều kiện độc lập")
		void validateBaiHocBranches() {
			Map<String, String> all = AdminValidationHelper.validateBaiHoc(null, null, null, null, null);
			assertTrue(all.containsKey("tenBH"));
			assertTrue(all.containsKey("noiDungBH"));
			assertTrue(all.containsKey("maDM"));

			assertTrue(AdminValidationHelper.validateBaiHoc("x".repeat(201), "nd", null, null, 1)
				.containsKey("tenBH"));
			assertTrue(AdminValidationHelper.validateBaiHoc("ten", "nd", "not-url", null, 1)
				.containsKey("linkURL"));
			assertTrue(AdminValidationHelper.validateBaiHoc("ten", "nd", null, "not-url", 1)
				.containsKey("hinhAnh"));
			assertTrue(AdminValidationHelper.validateBaiHoc("ten", "nd", "https://a", "https://b", 1).isEmpty());
			assertTrue(AdminValidationHelper.validateBaiHoc("ten", "nd", "  ", "  ", 1).isEmpty());
		}

		@Test
		@DisplayName("validateTracNghiem / DapAn / TaiNguyen / Lab / Login")
		void remainingValidators() {
			assertEquals(3, AdminValidationHelper.validateTracNghiem(" ", " ", null).size());
			assertTrue(AdminValidationHelper.validateTracNghiem("q", "e", 1).isEmpty());

			assertEquals(2, AdminValidationHelper.validateDapAn(null, null).size());
			assertTrue(AdminValidationHelper.validateDapAn("A", 1).isEmpty());

			Map<String, String> tnBlank = AdminValidationHelper.validateTaiNguyen(" ", " ", null);
			assertEquals(3, tnBlank.size());
			assertTrue(AdminValidationHelper.validateTaiNguyen("x".repeat(201), "https://a", 1)
				.containsKey("tenTaiNguyen"));
			assertTrue(AdminValidationHelper.validateTaiNguyen("file", "ftp://x", 1).containsKey("linkURL"));
			assertTrue(AdminValidationHelper.validateTaiNguyen("file", "https://a", 1).isEmpty());

			assertEquals(5, AdminValidationHelper.validateLab(" ", " ", " ", " ", null).size());
			assertTrue(AdminValidationHelper.validateLab("x".repeat(201), "m", "s", "e", 1)
				.containsKey("tenThucHanh"));
			assertTrue(AdminValidationHelper.validateLab("lab", "m", "s", "e", 1).isEmpty());

			assertEquals(2, AdminValidationHelper.validateLogin(" ", null).size());
			assertTrue(AdminValidationHelper.validateLogin("u", "p").isEmpty());
		}
	}

	/* ═══════════════════════════════════════════════════════════
	 *  AdminPagingUtil (package-private) qua reflection
	 * ═══════════════════════════════════════════════════════════ */

	@Nested
	@DisplayName("WB: AdminPagingUtil")
	class PagingUtilWhiteBox {

		@Test
		@DisplayName("pageable: page<0 → 0; size<1 → 10; size>50 → 50")
		void pageableBounds() throws Exception {
			Class<?> util = Class.forName("com.example.demo.web.AdminPagingUtil");
			Pageable neg = (Pageable) invoke(util, "pageable",
				new Class<?>[] {int.class, int.class, Sort.class}, -5, 10, Sort.by("id"));
			assertEquals(0, neg.getPageNumber());
			assertEquals(10, neg.getPageSize());

			Pageable tiny = (Pageable) invoke(util, "pageable",
				new Class<?>[] {int.class, int.class, Sort.class}, 1, 0, Sort.unsorted());
			assertEquals(10, tiny.getPageSize());

			Pageable huge = (Pageable) invoke(util, "pageable",
				new Class<?>[] {int.class, int.class, Sort.class}, 2, 99, Sort.unsorted());
			assertEquals(50, huge.getPageSize());
			assertEquals(2, huge.getPageNumber());
		}

		@Test
		@DisplayName("normalizeSearch: null → \"\"; có khoảng trắng thì trim")
		void normalizeSearch() throws Exception {
			Class<?> util = Class.forName("com.example.demo.web.AdminPagingUtil");
			assertEquals("", invoke(util, "normalizeSearch", new Class<?>[] {String.class}, new Object[] {null}));
			assertEquals("ui", invoke(util, "normalizeSearch", new Class<?>[] {String.class}, "  ui  "));
		}

		@Test
		@DisplayName("Constructor private")
		void privateCtor() throws Exception {
			Class<?> util = Class.forName("com.example.demo.web.AdminPagingUtil");
			Constructor<?> ctor = util.getDeclaredConstructor();
			ctor.setAccessible(true);
			assertNotNull(ctor.newInstance());
		}
	}

	/* ═══════════════════════════════════════════════════════════
	 *  AdminAuthFilter — mọi nhánh URI / session / attribute
	 * ═══════════════════════════════════════════════════════════ */

	@Nested
	@DisplayName("WB: AdminAuthFilter")
	class AuthFilterWhiteBox {

		@Test
		@DisplayName("URI không phải /admin → luôn chain.doFilter")
		void nonAdminPasses() throws Exception {
			MockHttpServletRequest req = new MockHttpServletRequest("GET", "/");
			req.setRequestURI("/");
			MockHttpServletResponse res = new MockHttpServletResponse();
			MockFilterChain chain = new MockFilterChain();
			adminAuthFilter.doFilter(req, res, chain);
			assertNotNull(chain.getRequest());
			assertEquals(200, res.getStatus() == 0 ? 200 : res.getStatus());
		}

		@Test
		@DisplayName("/admin không session → redirect /login, không gọi chain")
		void adminNoSessionRedirects() throws Exception {
			MockHttpServletRequest req = new MockHttpServletRequest("GET", "/admin");
			req.setRequestURI("/admin");
			MockHttpServletResponse res = new MockHttpServletResponse();
			FilterChain chain = (r, s) -> {
				throw new AssertionError("chain không được gọi khi chưa auth");
			};
			adminAuthFilter.doFilter(req, res, chain);
			assertEquals("/login", res.getRedirectedUrl());
		}

		@Test
		@DisplayName("/admin session nhưng ADMIN_AUTH != true → redirect")
		void adminSessionNotAuth() throws Exception {
			MockHttpServletRequest req = new MockHttpServletRequest("GET", "/admin/lab");
			req.setRequestURI("/admin/lab");
			req.setSession(new MockHttpSession());
			MockHttpServletResponse res = new MockHttpServletResponse();
			adminAuthFilter.doFilter(req, res, new MockFilterChain());
			assertEquals("/login", res.getRedirectedUrl());
		}

		@Test
		@DisplayName("/admin + ADMIN_AUTH=true → đi tiếp chain")
		void adminAuthenticatedPasses() throws Exception {
			MockHttpServletRequest req = new MockHttpServletRequest("GET", "/admin");
			req.setRequestURI("/admin");
			MockHttpSession session = new MockHttpSession();
			session.setAttribute("ADMIN_AUTH", true);
			req.setSession(session);
			MockHttpServletResponse res = new MockHttpServletResponse();
			MockFilterChain chain = new MockFilterChain();
			adminAuthFilter.doFilter(req, res, chain);
			assertNull(res.getRedirectedUrl());
			assertNotNull(chain.getRequest());
		}
	}

	/* ═══════════════════════════════════════════════════════════
	 *  AuthController
	 * ═══════════════════════════════════════════════════════════ */

	@Nested
	@DisplayName("WB: AuthController")
	class AuthControllerWhiteBox {

		@Test
		@DisplayName("GET /login: chưa login → view login; đã login → redirect /admin")
		void loginPageBranches() throws Exception {
			mockMvc.perform(get("/login"))
				.andExpect(status().isOk())
				.andExpect(view().name("login"));

			MockHttpSession session = adminSession();
			mockMvc.perform(get("/login").session(session))
				.andExpect(redirectedUrl("/admin"));
		}

		@Test
		@DisplayName("POST /login: validate fail / sai mật khẩu / đúng mật khẩu")
		void loginPostBranches() throws Exception {
			mockMvc.perform(post("/login").param("username", "  ").param("password", "  "))
				.andExpect(redirectedUrl("/login?error"));
			mockMvc.perform(post("/login").param("username", "itadmin").param("password", "wrong"))
				.andExpect(redirectedUrl("/login?error"));
			mockMvc.perform(post("/login").param("username", " itadmin ").param("password", " itsecret "))
				.andExpect(redirectedUrl("/admin"));
		}

		@Test
		@DisplayName("logout: session thật invalidate; session null vẫn redirect")
		void logoutBranches() throws Exception {
			MockHttpSession session = adminSession();
			mockMvc.perform(get("/logout").session(session))
				.andExpect(redirectedUrl("/login?logout"));
			assertTrue(session.isInvalid());
			assertEquals("redirect:/login?logout", authController.logout(null));
		}
	}

	/* ═══════════════════════════════════════════════════════════
	 *  DanhMucService / BaiHocService
	 * ═══════════════════════════════════════════════════════════ */

	@Nested
	@DisplayName("WB: DanhMucService + BaiHocService")
	class ServiceWhiteBox {

		@Test
		@DisplayName("findAllForHome: ẩn tên cố định, ẩn ten=null, trim trước khi so")
		void homeFilterBranches() {
			saveDm("Hiển thị");
			saveDm("Thiết kế UI/UX cơ bản");
			saveDm("Công cụ Figma");
			DanhMuc padded = new DanhMuc();
			padded.setTenDanhMuc("  Thiết kế UI/UX cơ bản  ");
			danhMucRepository.save(padded);
			DanhMuc noName = new DanhMuc();
			noName.setTenDanhMuc(null);
			danhMucRepository.save(noName);

			List<DanhMuc> home = danhMucService.findAllForHome();
			assertEquals(1, home.size());
			assertEquals("Hiển thị", home.get(0).getTenDanhMuc());
			assertTrue(danhMucService.findById(home.get(0).getMaDM()).isPresent());
			assertTrue(danhMucService.findById(-1).isEmpty());
		}

		@Test
		@DisplayName("BaiHocService: theo danh mục và findById có/không")
		void baiHocService() {
			DanhMuc dm = saveDm("UX Track");
			BaiHoc saved = saveBh(dm.getMaDM(), "Bài 1", null);
			assertEquals(1, baiHocService.findByDanhMuc(dm.getMaDM()).size());
			assertTrue(baiHocService.findById(saved.getMaBH()).isPresent());
			assertTrue(baiHocService.findById(999999).isEmpty());
		}
	}

	/* ═══════════════════════════════════════════════════════════
	 *  BaiHocController: resolveTrackTheme, toEmbedUrl, JSON lab/quiz
	 * ═══════════════════════════════════════════════════════════ */

	@Nested
	@DisplayName("WB: BaiHocController helpers + API")
	class BaiHocControllerWhiteBox {

		@Test
		@DisplayName("resolveTrackTheme: 1003 / 1004 / tên chứa ux / null / mặc định ui")
		void resolveTrackThemeBranches() throws Exception {
			assertEquals("ux", invoke(baiHocController, "resolveTrackTheme",
				new Class<?>[] {Integer.class, DanhMuc.class}, 1003, null));
			assertEquals("ui", invoke(baiHocController, "resolveTrackTheme",
				new Class<?>[] {Integer.class, DanhMuc.class}, 1004, null));

			DanhMuc ux = new DanhMuc();
			ux.setTenDanhMuc("Học UX nâng cao");
			assertEquals("ux", invoke(baiHocController, "resolveTrackTheme",
				new Class<?>[] {Integer.class, DanhMuc.class}, 1, ux));

			DanhMuc noName = new DanhMuc();
			noName.setTenDanhMuc(null);
			assertEquals("ui", invoke(baiHocController, "resolveTrackTheme",
				new Class<?>[] {Integer.class, DanhMuc.class}, 1, noName));
			assertEquals("ui", invoke(baiHocController, "resolveTrackTheme",
				new Class<?>[] {Integer.class, DanhMuc.class}, 1, null));

			DanhMuc uiName = new DanhMuc();
			uiName.setTenDanhMuc("Giao diện");
			assertEquals("ui", invoke(baiHocController, "resolveTrackTheme",
				new Class<?>[] {Integer.class, DanhMuc.class}, 1, uiName));
		}

		@Test
		@DisplayName("toEmbedUrl: null/blank, watch?v=, watch?v= + &, youtu.be, URL khác")
		void toEmbedUrlBranches() throws Exception {
			assertEquals("", invoke(baiHocController, "toEmbedUrl", new Class<?>[] {String.class}, new Object[] {null}));
			assertEquals("", invoke(baiHocController, "toEmbedUrl", new Class<?>[] {String.class}, "  "));
			assertEquals("https://www.youtube.com/embed/abc",
				invoke(baiHocController, "toEmbedUrl", new Class<?>[] {String.class},
					"https://www.youtube.com/watch?v=abc"));
			assertEquals("https://www.youtube.com/embed/abc",
				invoke(baiHocController, "toEmbedUrl", new Class<?>[] {String.class},
					"https://www.youtube.com/watch?v=abc&t=12"));
			assertEquals("https://www.youtube.com/embed/xyz",
				invoke(baiHocController, "toEmbedUrl", new Class<?>[] {String.class}, "https://youtu.be/xyz"));
			assertEquals("https://vimeo.com/1",
				invoke(baiHocController, "toEmbedUrl", new Class<?>[] {String.class}, "https://vimeo.com/1"));
		}

		@Test
		@DisplayName("canonicalizeSimpleJsonObject + compareState mọi nhánh")
		void jsonCanonicalAndCompare() throws Exception {
			assertEquals("", invoke(baiHocController, "canonicalizeSimpleJsonObject",
				new Class<?>[] {String.class}, new Object[] {null}));
			assertEquals("plain", invoke(baiHocController, "canonicalizeSimpleJsonObject",
				new Class<?>[] {String.class}, "plain"));
			assertEquals("{}", invoke(baiHocController, "canonicalizeSimpleJsonObject",
				new Class<?>[] {String.class}, "{  }"));
			String sorted = (String) invoke(baiHocController, "canonicalizeSimpleJsonObject",
				new Class<?>[] {String.class}, "{ \"b\":1, \"a\":2 }");
			assertTrue(sorted.startsWith("{"));
			assertTrue(sorted.contains("\"a\":2"));

			assertTrue((Boolean) invoke(baiHocController, "compareState",
				new Class<?>[] {String.class, String.class}, null, null));
			assertTrue((Boolean) invoke(baiHocController, "compareState",
				new Class<?>[] {String.class, String.class}, "{ \"a\":1, \"b\":2 }", "{ \"b\":2, \"a\":1 }"));
			assertFalse((Boolean) invoke(baiHocController, "compareState",
				new Class<?>[] {String.class, String.class}, "{a:1}", "{a:2}"));
		}

		@Test
		@DisplayName("GET /danhmuc: theme ux theo tên; GET /baihoc: tồn tại / không / nextLesson")
		void lessonPages() throws Exception {
			DanhMuc ux = saveDm("Khóa UX");
			BaiHoc first = saveBh(ux.getMaDM(), "Bài A", "https://www.youtube.com/watch?v=abc&t=1");
			BaiHoc second = saveBh(ux.getMaDM(), "Bài B", null);
			TracNghiem q = new TracNghiem();
			q.setNoiDungCauHoi("Câu?");
			q.setLoiGiaiThich("Vì...");
			q.setMaBH(first.getMaBH());
			q = tracNghiemRepository.save(q);
			DapAn da = new DapAn();
			da.setNoiDungDapAn("Đúng");
			da.setIsCorrect(true);
			da.setMaTN(q.getMaTN());
			dapAnRepository.save(da);

			mockMvc.perform(get("/danhmuc/" + ux.getMaDM()))
				.andExpect(status().isOk())
				.andExpect(view().name("baihoc-list"))
				.andExpect(model().attribute("trackTheme", "ux"));

			mockMvc.perform(get("/danhmuc/1004"))
				.andExpect(status().isOk())
				.andExpect(model().attribute("trackTheme", "ui"));

			mockMvc.perform(get("/baihoc/" + first.getMaBH()))
				.andExpect(status().isOk())
				.andExpect(view().name("baihoc-detail"))
				.andExpect(model().attribute("videoEmbedUrl", "https://www.youtube.com/embed/abc"))
				.andExpect(model().attributeExists("nextLesson"));

			mockMvc.perform(get("/baihoc/" + second.getMaBH()))
				.andExpect(status().isOk());

			mockMvc.perform(get(" /baihoc/999999".trim()))
				.andExpect(redirectedUrl("/"));
		}

		@Test
		@DisplayName("quiz/check: không thấy đáp án; đúng; sai; isCorrect=null; câu hỏi mất")
		void quizCheckBranches() throws Exception {
			mockMvc.perform(post("/api/quiz/check")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"maDA\":-1}"))
				.andExpect(jsonPath("$.ok").value(false));

			TracNghiem q = new TracNghiem();
			q.setNoiDungCauHoi("Q");
			q.setLoiGiaiThich("E");
			q.setMaBH(1);
			q = tracNghiemRepository.save(q);

			DapAn right = new DapAn();
			right.setNoiDungDapAn("R");
			right.setIsCorrect(true);
			right.setMaTN(q.getMaTN());
			right = dapAnRepository.save(right);

			DapAn wrong = new DapAn();
			wrong.setNoiDungDapAn("W");
			wrong.setIsCorrect(false);
			wrong.setMaTN(q.getMaTN());
			wrong = dapAnRepository.save(wrong);

			DapAn unknown = new DapAn();
			unknown.setNoiDungDapAn("?");
			unknown.setIsCorrect(null);
			unknown.setMaTN(q.getMaTN());
			unknown = dapAnRepository.save(unknown);

			mockMvc.perform(post("/api/quiz/check")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"maDA\":" + right.getMaDA() + "}"))
				.andExpect(jsonPath("$.ok").value(true))
				.andExpect(jsonPath("$.correct").value(true))
				.andExpect(jsonPath("$.explanation").value("E"));

			mockMvc.perform(post("/api/quiz/check")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"maDA\":" + wrong.getMaDA() + "}"))
				.andExpect(jsonPath("$.correct").value(false));

			mockMvc.perform(post("/api/quiz/check")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"maDA\":" + unknown.getMaDA() + "}"))
				.andExpect(jsonPath("$.correct").value(false));

			DapAn orphan = new DapAn();
			orphan.setNoiDungDapAn("o");
			orphan.setIsCorrect(true);
			orphan.setMaTN(888888);
			orphan = dapAnRepository.save(orphan);
			mockMvc.perform(post("/api/quiz/check")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"maDA\":" + orphan.getMaDA() + "}"))
				.andExpect(jsonPath("$.ok").value(true))
				.andExpect(jsonPath("$.explanation").value(""));
		}

		@Test
		@DisplayName("lab/check: không thấy lab; khớp JSON; lệch; goiY/expected null")
		void labCheckBranches() throws Exception {
			mockMvc.perform(post("/api/lab/check")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"maTH\":-1,\"userState\":\"{}\"}"))
				.andExpect(jsonPath("$.ok").value(false));

			InteractiveLab lab = new InteractiveLab();
			lab.setTenThucHanh("Lab");
			lab.setMoTaThucHanh("m");
			lab.setTrangThaiBanDau("{}");
			lab.setTrangThaiDung("{ \"color\":\"red\", \"n\":1 }");
			lab.setGoiY("gợi ý");
			lab.setMaBH(1);
			lab = interactiveLabRepository.save(lab);

			mockMvc.perform(post("/api/lab/check")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"maTH\":" + lab.getMaTH() + ",\"userState\":\"{ \\\"n\\\":1, \\\"color\\\":\\\"red\\\" }\"}"))
				.andExpect(jsonPath("$.ok").value(true))
				.andExpect(jsonPath("$.correct").value(true));

			mockMvc.perform(post("/api/lab/check")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"maTH\":" + lab.getMaTH() + ",\"userState\":\"{nope}\"}"))
				.andExpect(jsonPath("$.correct").value(false))
				.andExpect(jsonPath("$.hint").value("gợi ý"));

			InteractiveLab emptyHint = new InteractiveLab();
			emptyHint.setTenThucHanh("L2");
			emptyHint.setMoTaThucHanh("m");
			emptyHint.setTrangThaiBanDau("x");
			emptyHint.setTrangThaiDung(null);
			emptyHint.setGoiY(null);
			emptyHint.setMaBH(1);
			emptyHint = interactiveLabRepository.save(emptyHint);
			mockMvc.perform(post("/api/lab/check")
					.contentType(MediaType.APPLICATION_JSON)
					.content("{\"maTH\":" + emptyHint.getMaTH() + "}"))
				.andExpect(jsonPath("$.ok").value(true))
				.andExpect(jsonPath("$.hint").value(""))
				.andExpect(jsonPath("$.expectedState").value(""));
		}
	}

	/* ═══════════════════════════════════════════════════════════
	 *  Admin CRUD: parseIdOrNull, joinErrors, save/delete nhánh
	 * ═══════════════════════════════════════════════════════════ */

	@Nested
	@DisplayName("WB: AdminDanhMucController")
	class AdminControllerWhiteBox {

		@Test
		@DisplayName("joinErrors: map rỗng → null; 1 lỗi; nhiều lỗi có <br>")
		void joinErrors() throws Exception {
			Class<?> admin = Class.forName("com.example.demo.web.AdminDanhMucController");
			assertNull(invoke(admin, "joinErrors", new Class<?>[] {Map.class}, Map.of()));
			assertEquals("• a", invoke(admin, "joinErrors", new Class<?>[] {Map.class}, Map.of("k", "a")));
			Map<String, String> two = new LinkedHashMap<>();
			two.put("a", "one");
			two.put("b", "two");
			assertEquals("• one<br>• two", invoke(admin, "joinErrors", new Class<?>[] {Map.class}, two));
		}

		@Test
		@DisplayName("parseIdOrNull: null / blank / không phải số / số hợp lệ")
		void parseIdOrNull() throws Exception {
			Class<?> admin = Class.forName("com.example.demo.web.AdminDanhMucController");
			assertNull(invoke(admin, "parseIdOrNull", new Class<?>[] {String.class}, new Object[] {null}));
			assertNull(invoke(admin, "parseIdOrNull", new Class<?>[] {String.class}, "  "));
			assertNull(invoke(admin, "parseIdOrNull", new Class<?>[] {String.class}, "abc"));
			assertEquals(12, invoke(admin, "parseIdOrNull", new Class<?>[] {String.class}, " 12 "));
		}

		@Test
		@DisplayName("Trang list: không editId / editId có / editId không tồn tại / filter sai / page size biên")
		void listPageBranches() throws Exception {
			MockHttpSession session = adminSession();
			DanhMuc dm = saveDm("Cat");
			mockMvc.perform(get("/admin/danhmuc").session(session))
				.andExpect(view().name("admin/danhmuc"));
			mockMvc.perform(get("/admin/danhmuc").param("editId", String.valueOf(dm.getMaDM())).session(session))
				.andExpect(status().isOk());
			mockMvc.perform(get("/admin/danhmuc").param("editId", "99999").session(session))
				.andExpect(status().isOk());
			mockMvc.perform(get("/admin/danhmuc").param("page", "-1").param("size", "0").param("q", "  ").session(session))
				.andExpect(status().isOk());
			mockMvc.perform(get("/admin/danhmuc").param("size", "100").session(session))
				.andExpect(status().isOk());

			mockMvc.perform(get("/admin/baihoc").param("maDM", "abc").session(session))
				.andExpect(view().name("admin/baihoc"));
			mockMvc.perform(get("/admin/baihoc").param("editId", "1").session(session))
				.andExpect(status().isOk());
			mockMvc.perform(get("/admin/tracnghiem").session(session)).andExpect(view().name("admin/tracnghiem"));
			mockMvc.perform(get("/admin/tracnghiem").param("editId", "1").param("maBH", "x").session(session))
				.andExpect(status().isOk());
			mockMvc.perform(get("/admin/dapan").session(session)).andExpect(view().name("admin/dapan"));
			mockMvc.perform(get("/admin/dapan").param("editId", "1").session(session)).andExpect(status().isOk());
			mockMvc.perform(get("/admin/tainguyen").session(session)).andExpect(view().name("admin/tainguyen"));
			mockMvc.perform(get("/admin/tainguyen").param("editId", "1").session(session)).andExpect(status().isOk());
			mockMvc.perform(get("/admin/lab").session(session)).andExpect(view().name("admin/lab"));
			mockMvc.perform(get("/admin/lab").param("editId", "1").session(session)).andExpect(status().isOk());
		}

		@Test
		@DisplayName("save: lỗi validate (có/không id) và lưu mới / cập nhật")
		void saveBranches() throws Exception {
			MockHttpSession session = adminSession();

			mockMvc.perform(post("/admin/danhmuc/save").param("tenDanhMuc", " ").session(session))
				.andExpect(redirectedUrl("/admin/danhmuc"));
			mockMvc.perform(post("/admin/danhmuc/save").param("maDM", "9").param("tenDanhMuc", " ").session(session))
				.andExpect(redirectedUrl("/admin/danhmuc?editId=9"));
			mockMvc.perform(post("/admin/danhmuc/save")
					.param("tenDanhMuc", "Mới")
					.param("iconURL", " https://i ")
					.session(session))
				.andExpect(redirectedUrl("/admin/danhmuc"));
			DanhMuc saved = danhMucRepository.findAll().get(0);
			mockMvc.perform(post("/admin/danhmuc/save")
					.param("maDM", String.valueOf(saved.getMaDM()))
					.param("tenDanhMuc", "Sửa")
					.session(session))
				.andExpect(redirectedUrl("/admin/danhmuc"));

			mockMvc.perform(post("/admin/baihoc/save")
					.param("tenBH", " ").param("noiDungBH", " ").session(session))
				.andExpect(redirectedUrl("/admin/baihoc"));
			mockMvc.perform(post("/admin/baihoc/save")
					.param("maBH", "3").param("tenBH", " ").param("noiDungBH", "x").session(session))
				.andExpect(redirectedUrl("/admin/baihoc?editId=3"));
			mockMvc.perform(post("/admin/baihoc/save")
					.param("tenBH", "BH")
					.param("noiDungBH", "ND")
					.param("linkURL", " https://a ")
					.param("hinhAnh", " https://b ")
					.param("maDM", String.valueOf(saved.getMaDM()))
					.session(session))
				.andExpect(redirectedUrl("/admin/baihoc"));
			BaiHoc bh = baiHocRepository.findAll().get(0);
			mockMvc.perform(post("/admin/baihoc/save")
					.param("maBH", String.valueOf(bh.getMaBH()))
					.param("tenBH", "BH2")
					.param("noiDungBH", "ND2")
					.param("maDM", String.valueOf(saved.getMaDM()))
					.session(session))
				.andExpect(redirectedUrl("/admin/baihoc"));

			mockMvc.perform(post("/admin/tracnghiem/save")
					.param("noiDungCauHoi", " ").param("loiGiaiThich", " ").session(session))
				.andExpect(redirectedUrl("/admin/tracnghiem"));
			mockMvc.perform(post("/admin/tracnghiem/save")
					.param("maTN", "4").param("noiDungCauHoi", " ").param("loiGiaiThich", "e").session(session))
				.andExpect(redirectedUrl("/admin/tracnghiem?editId=4"));
			mockMvc.perform(post("/admin/tracnghiem/save")
					.param("noiDungCauHoi", "Q")
					.param("loiGiaiThich", "E")
					.param("maBH", String.valueOf(bh.getMaBH()))
					.session(session))
				.andExpect(redirectedUrl("/admin/tracnghiem"));
			TracNghiem tn = tracNghiemRepository.findAll().get(0);
			mockMvc.perform(post("/admin/tracnghiem/save")
					.param("maTN", String.valueOf(tn.getMaTN()))
					.param("noiDungCauHoi", "Q2")
					.param("loiGiaiThich", "E2")
					.param("maBH", String.valueOf(bh.getMaBH()))
					.session(session))
				.andExpect(redirectedUrl("/admin/tracnghiem"));

			mockMvc.perform(post("/admin/dapan/save").param("noiDungDapAn", " ").session(session))
				.andExpect(redirectedUrl("/admin/dapan"));
			mockMvc.perform(post("/admin/dapan/save")
					.param("maDA", "5").param("noiDungDapAn", " ").session(session))
				.andExpect(redirectedUrl("/admin/dapan?editId=5"));
			mockMvc.perform(post("/admin/dapan/save")
					.param("noiDungDapAn", "A")
					.param("isCorrect", "true")
					.param("maTN", String.valueOf(tn.getMaTN()))
					.session(session))
				.andExpect(redirectedUrl("/admin/dapan"));
			DapAn da = dapAnRepository.findAll().get(0);
			mockMvc.perform(post("/admin/dapan/save")
					.param("maDA", String.valueOf(da.getMaDA()))
					.param("noiDungDapAn", "A2")
					.param("maTN", String.valueOf(tn.getMaTN()))
					.session(session))
				.andExpect(redirectedUrl("/admin/dapan"));

			mockMvc.perform(post("/admin/tainguyen/save")
					.param("tenTaiNguyen", " ").param("linkURL", " ").session(session))
				.andExpect(redirectedUrl("/admin/tainguyen"));
			mockMvc.perform(post("/admin/tainguyen/save")
					.param("maTaiNguyen", "6").param("tenTaiNguyen", " ").param("linkURL", "https://x").session(session))
				.andExpect(redirectedUrl("/admin/tainguyen?editId=6"));
			mockMvc.perform(post("/admin/tainguyen/save")
					.param("tenTaiNguyen", "File")
					.param("linkURL", " https://f ")
					.param("maBH", String.valueOf(bh.getMaBH()))
					.session(session))
				.andExpect(redirectedUrl("/admin/tainguyen"));
			TaiNguyen tnRes = taiNguyenRepository.findAll().get(0);
			mockMvc.perform(post("/admin/tainguyen/save")
					.param("maTaiNguyen", String.valueOf(tnRes.getMaTaiNguyen()))
					.param("tenTaiNguyen", "File2")
					.param("linkURL", "https://f2")
					.param("maBH", String.valueOf(bh.getMaBH()))
					.session(session))
				.andExpect(redirectedUrl("/admin/tainguyen"));

			mockMvc.perform(post("/admin/lab/save")
					.param("tenThucHanh", " ").param("moTaThucHanh", " ")
					.param("trangThaiBanDau", " ").param("trangThaiDung", " ").session(session))
				.andExpect(redirectedUrl("/admin/lab"));
			mockMvc.perform(post("/admin/lab/save")
					.param("maTH", "7").param("tenThucHanh", " ").param("moTaThucHanh", "m")
					.param("trangThaiBanDau", "s").param("trangThaiDung", "e").session(session))
				.andExpect(redirectedUrl("/admin/lab?editId=7"));
			mockMvc.perform(post("/admin/lab/save")
					.param("tenThucHanh", "Lab")
					.param("moTaThucHanh", "m")
					.param("trangThaiBanDau", "s")
					.param("trangThaiDung", "e")
					.param("goiY", " g ")
					.param("maBH", String.valueOf(bh.getMaBH()))
					.session(session))
				.andExpect(redirectedUrl("/admin/lab"));
			InteractiveLab lab = interactiveLabRepository.findAll().get(0);
			mockMvc.perform(post("/admin/lab/save")
					.param("maTH", String.valueOf(lab.getMaTH()))
					.param("tenThucHanh", "Lab2")
					.param("moTaThucHanh", "m2")
					.param("trangThaiBanDau", "s2")
					.param("trangThaiDung", "e2")
					.param("maBH", String.valueOf(bh.getMaBH()))
					.session(session))
				.andExpect(redirectedUrl("/admin/lab"));
		}

		@Test
		@DisplayName("delete: thành công từng entity; xóa danh mục khi còn bài → nhánh catch")
		void deleteBranches() throws Exception {
			MockHttpSession session = adminSession();
			DanhMuc dm = saveDm("Xóa");
			BaiHoc bh = saveBh(dm.getMaDM(), "BH", null);
			TracNghiem q = new TracNghiem();
			q.setNoiDungCauHoi("q");
			q.setLoiGiaiThich("e");
			q.setMaBH(bh.getMaBH());
			q = tracNghiemRepository.save(q);
			DapAn da = new DapAn();
			da.setNoiDungDapAn("a");
			da.setIsCorrect(false);
			da.setMaTN(q.getMaTN());
			da = dapAnRepository.save(da);
			TaiNguyen res = new TaiNguyen();
			res.setTenTaiNguyen("f");
			res.setLinkURL("https://x");
			res.setMaBH(bh.getMaBH());
			res = taiNguyenRepository.save(res);
			InteractiveLab lab = new InteractiveLab();
			lab.setTenThucHanh("l");
			lab.setMoTaThucHanh("m");
			lab.setTrangThaiBanDau("s");
			lab.setTrangThaiDung("e");
			lab.setMaBH(bh.getMaBH());
			lab = interactiveLabRepository.save(lab);

			mockMvc.perform(post("/admin/dapan/" + da.getMaDA() + "/delete").session(session))
				.andExpect(redirectedUrl("/admin/dapan"));
			mockMvc.perform(post("/admin/tainguyen/" + res.getMaTaiNguyen() + "/delete").session(session))
				.andExpect(redirectedUrl("/admin/tainguyen"));
			mockMvc.perform(post("/admin/lab/" + lab.getMaTH() + "/delete").session(session))
				.andExpect(redirectedUrl("/admin/lab"));
			mockMvc.perform(post("/admin/tracnghiem/" + q.getMaTN() + "/delete").session(session))
				.andExpect(redirectedUrl("/admin/tracnghiem"));
			mockMvc.perform(post("/admin/baihoc/" + bh.getMaBH() + "/delete").session(session))
				.andExpect(redirectedUrl("/admin/baihoc"));
			mockMvc.perform(post("/admin/danhmuc/" + dm.getMaDM() + "/delete").session(session))
				.andExpect(redirectedUrl("/admin/danhmuc"));

			DanhMuc parent = saveDm("P");
			saveBh(parent.getMaDM(), "child", null);
			mockMvc.perform(post("/admin/danhmuc/" + parent.getMaDM() + "/delete").session(session))
				.andExpect(redirectedUrl("/admin/danhmuc"));
		}
	}
}
