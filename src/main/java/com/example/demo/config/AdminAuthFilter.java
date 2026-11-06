package com.example.demo.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.stereotype.Component;

/**
 * Servlet Filter chặn mọi request đến /admin/** nếu chưa đăng nhập.
 * Chạy ở tầng Servlet container, trước khi request đến DispatcherServlet.
 */
@Component
public class AdminAuthFilter implements Filter {

	private static final String SESSION_ADMIN = "ADMIN_AUTH";

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		String uri = request.getRequestURI();

		// Chỉ chặn các URL bắt đầu bằng /admin
		if (uri.startsWith("/admin")) {
			HttpSession session = request.getSession(false);
			boolean isAuth = session != null
					&& Boolean.TRUE.equals(session.getAttribute(SESSION_ADMIN));
			if (!isAuth) {
				response.sendRedirect("/login");
				return;
			}
		}

		chain.doFilter(req, res);
	}
}
