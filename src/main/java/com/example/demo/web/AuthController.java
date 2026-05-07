package com.example.demo.web;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {
	@Value("${app.admin.username}")
	private String adminUsername;

	@Value("${app.admin.password}")
	private String adminPassword;

	@GetMapping("/login")
	public String loginPage() {
		return "login";
	}

	@PostMapping("/login")
	public String login(
		@RequestParam String username,
		@RequestParam String password,
		HttpSession session
	) {
		if (adminUsername.equals(username) && adminPassword.equals(password)) {
			session.setAttribute("ADMIN_AUTH", true);
			return "redirect:/admin";
		}
		return "redirect:/login?error";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session) {
		session.invalidate();
		return "redirect:/login?logout";
	}
}

