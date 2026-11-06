package com.example.demo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	// AdminAuthFilter (Servlet Filter) đã thay thế Interceptor.
	// Không cần đăng ký interceptor ở đây nữa.
}
