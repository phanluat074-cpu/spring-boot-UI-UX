package com.example.demo.dapan;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DapAnRepository extends JpaRepository<DapAn, Integer> {
	List<DapAn> findByMaTNOrderByMaDAAsc(Integer maTN);
}

