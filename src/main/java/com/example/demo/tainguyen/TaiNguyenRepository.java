package com.example.demo.tainguyen;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaiNguyenRepository extends JpaRepository<TaiNguyen, Integer> {
	List<TaiNguyen> findByMaBHOrderByMaTaiNguyenAsc(Integer maBH);
}

