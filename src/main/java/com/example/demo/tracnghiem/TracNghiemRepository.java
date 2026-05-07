package com.example.demo.tracnghiem;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TracNghiemRepository extends JpaRepository<TracNghiem, Integer> {
	List<TracNghiem> findByMaBHOrderByMaTNAsc(Integer maBH);
}

