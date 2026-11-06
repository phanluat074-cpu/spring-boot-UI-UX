package com.example.demo.tracnghiem;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TracNghiemRepository extends JpaRepository<TracNghiem, Integer> {
	List<TracNghiem> findByMaBHOrderByMaTNAsc(Integer maBH);

	@Query("SELECT t FROM TracNghiem t WHERE (COALESCE(:q, '') = '' OR LOWER(t.noiDungCauHoi) LIKE LOWER(CONCAT('%', :q, '%')) "
		+ "OR LOWER(COALESCE(t.loiGiaiThich, '')) LIKE LOWER(CONCAT('%', :q, '%'))) "
		+ "AND (:maBH IS NULL OR t.maBH = :maBH)")
	Page<TracNghiem> searchPage(@Param("q") String q, @Param("maBH") Integer maBH, Pageable pageable);
}

