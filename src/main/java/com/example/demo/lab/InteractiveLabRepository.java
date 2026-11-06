package com.example.demo.lab;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InteractiveLabRepository extends JpaRepository<InteractiveLab, Integer> {
	List<InteractiveLab> findByMaBHOrderByMaTHAsc(Integer maBH);

	@Query("SELECT l FROM InteractiveLab l WHERE (COALESCE(:q, '') = '' OR LOWER(l.tenThucHanh) LIKE LOWER(CONCAT('%', :q, '%')) "
		+ "OR LOWER(COALESCE(l.moTaThucHanh, '')) LIKE LOWER(CONCAT('%', :q, '%'))) "
		+ "AND (:maBH IS NULL OR l.maBH = :maBH)")
	Page<InteractiveLab> searchPage(@Param("q") String q, @Param("maBH") Integer maBH, Pageable pageable);
}

