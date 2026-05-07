package com.example.demo.danhmuc;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class DanhMucService {
	private static final Set<String> HIDDEN_CATEGORY_NAMES = Set.of(
		"Thiết kế UI/UX cơ bản",
		"Công cụ Figma"
	);

	private final DanhMucRepository danhMucRepository;

	public DanhMucService(DanhMucRepository danhMucRepository) {
		this.danhMucRepository = danhMucRepository;
	}

	public List<DanhMuc> findAllForHome() {
		return danhMucRepository.findAll(Sort.by(Sort.Direction.ASC, "maDM"))
			.stream()
			.filter(dm -> dm.getTenDanhMuc() != null && !HIDDEN_CATEGORY_NAMES.contains(dm.getTenDanhMuc().trim()))
			.toList();
	}

	public Optional<DanhMuc> findById(Integer maDM) {
		return danhMucRepository.findById(maDM);
	}
}

