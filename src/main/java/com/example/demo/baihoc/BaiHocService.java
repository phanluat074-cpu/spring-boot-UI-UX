package com.example.demo.baihoc;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class BaiHocService {
	private final BaiHocRepository baiHocRepository;

	public BaiHocService(BaiHocRepository baiHocRepository) {
		this.baiHocRepository = baiHocRepository;
	}

	public List<BaiHoc> findByDanhMuc(Integer maDM) {
		return baiHocRepository.findByMaDMOrderByNgayDangDesc(maDM);
	}

	public Optional<BaiHoc> findById(Integer maBH) {
		return baiHocRepository.findById(maBH);
	}
}

