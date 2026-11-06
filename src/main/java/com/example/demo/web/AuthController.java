package com.example.demo.web;

import com.example.demo.config.AdminValidationHelper;
import jakarta.servlet.http.HttpSession;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
	private static final String SESSION_ADMIN = "ADMIN_AUTH";
	@Value("${app.admin.username}")
	private String adminUsername;

	@Value("${app.admin.password}")
	private String adminPassword;

	@GetMapping("/login")
	public String loginPage(HttpSession session) {
		if (Boolean.TRUE.equals(session.getAttribute(SESSION_ADMIN))) {
			return "redirect:/admin";
		}
		return "login";
	}

	@PostMapping("/login")
	public String login(
		@RequestParam String username,
		@RequestParam String password,
		HttpSession session
	) {
		// ── Server-side validation ──
		Map<String, String> errors = AdminValidationHelper.validateLogin(username, password);
		if (!errors.isEmpty()) {
			return "redirect:/login?error";
		}

		String trimmedUser = username.trim();
		String trimmedPass = password.trim();

		if (adminUsername.equals(trimmedUser) && adminPassword.equals(trimmedPass)) {
			session.setAttribute(SESSION_ADMIN, true);
			return "redirect:/admin";
		}
		return "redirect:/login?error";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		if (session != null) {
			session.invalidate();
		}
		return "redirect:/login?logout";
	}
}
