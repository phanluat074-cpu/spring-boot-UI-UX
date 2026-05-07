package com.example.demo.web;

import com.example.demo.danhmuc.DanhMucService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
	private final DanhMucService danhMucService;

	public HomeController(DanhMucService danhMucService) {
		this.danhMucService = danhMucService;
	}

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("danhMucs", danhMucService.findAllForHome());
		return "home";
	}
}

