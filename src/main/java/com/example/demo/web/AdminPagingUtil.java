package com.example.demo.web;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

final class AdminPagingUtil {
	private AdminPagingUtil() {
	}

	static Pageable pageable(int page, int size, Sort sort) {
		int p = Math.max(0, page);
		int s = size < 1 ? 10 : Math.min(size, 50);
		return PageRequest.of(p, s, sort);
	}

	static String normalizeSearch(String q) {
		return q == null ? "" : q.trim();
	}
}
