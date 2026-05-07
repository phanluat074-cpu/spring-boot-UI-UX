package com.example.demo.lab;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InteractiveLabRepository extends JpaRepository<InteractiveLab, Integer> {
	List<InteractiveLab> findByMaBHOrderByMaTHAsc(Integer maBH);
}

