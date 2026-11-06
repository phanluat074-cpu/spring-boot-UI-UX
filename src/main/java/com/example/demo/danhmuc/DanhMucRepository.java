package com.example.demo.danhmuc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DanhMucRepository extends JpaRepository<DanhMuc, Integer> {
	@Query("SELECT d FROM DanhMuc d WHERE (COALESCE(:q, '') = '' OR LOWER(d.tenDanhMuc) LIKE LOWER(CONCAT('%', :q, '%')) "
		+ "OR LOWER(COALESCE(d.iconURL, '')) LIKE LOWER(CONCAT('%', :q, '%')))")
	Page<DanhMuc> searchPage(@Param("q") String q, Pageable pageable);
}

