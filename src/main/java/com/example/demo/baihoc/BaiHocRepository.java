package com.example.demo.baihoc;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BaiHocRepository extends JpaRepository<BaiHoc, Integer> {
	List<BaiHoc> findByMaDMOrderByNgayDangDesc(Integer maDM);
}

