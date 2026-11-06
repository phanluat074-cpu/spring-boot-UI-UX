package com.example.demo.tainguyen;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TaiNguyenRepository extends JpaRepository<TaiNguyen, Integer> {
	List<TaiNguyen> findByMaBHOrderByMaTaiNguyenAsc(Integer maBH);

	@Query("SELECT t FROM TaiNguyen t WHERE (COALESCE(:q, '') = '' OR LOWER(t.tenTaiNguyen) LIKE LOWER(CONCAT('%', :q, '%')) "
		+ "OR LOWER(COALESCE(t.linkURL, '')) LIKE LOWER(CONCAT('%', :q, '%'))) "
		+ "AND (:maBH IS NULL OR t.maBH = :maBH)")
	Page<TaiNguyen> searchPage(@Param("q") String q, @Param("maBH") Integer maBH, Pageable pageable);
}

