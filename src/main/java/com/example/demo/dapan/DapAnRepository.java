package com.example.demo.dapan;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DapAnRepository extends JpaRepository<DapAn, Integer> {
	List<DapAn> findByMaTNOrderByMaDAAsc(Integer maTN);

	@Query("SELECT d FROM DapAn d WHERE (COALESCE(:q, '') = '' OR LOWER(d.noiDungDapAn) LIKE LOWER(CONCAT('%', :q, '%'))) "
		+ "AND (:maTN IS NULL OR d.maTN = :maTN)")
	Page<DapAn> searchPage(@Param("q") String q, @Param("maTN") Integer maTN, Pageable pageable);
}

