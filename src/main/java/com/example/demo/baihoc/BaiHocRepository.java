package com.example.demo.baihoc;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BaiHocRepository extends JpaRepository<BaiHoc, Integer> {
	List<BaiHoc> findByMaDMOrderByNgayDangDesc(Integer maDM);

	@Query("SELECT b FROM BaiHoc b WHERE (COALESCE(:q, '') = '' OR LOWER(b.tenBH) LIKE LOWER(CONCAT('%', :q, '%')) "
		+ "OR LOWER(COALESCE(b.noiDungBH, '')) LIKE LOWER(CONCAT('%', :q, '%')) "
		+ "OR LOWER(COALESCE(b.linkURL, '')) LIKE LOWER(CONCAT('%', :q, '%'))) "
		+ "AND (:maDM IS NULL OR b.maDM = :maDM)")
	Page<BaiHoc> searchPage(@Param("q") String q, @Param("maDM") Integer maDM, Pageable pageable);
}

