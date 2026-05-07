package com.example.demo.web;

import com.example.demo.baihoc.BaiHoc;
import com.example.demo.baihoc.BaiHocRepository;
import com.example.demo.danhmuc.DanhMuc;
import com.example.demo.danhmuc.DanhMucRepository;
import com.example.demo.dapan.DapAn;
import com.example.demo.dapan.DapAnRepository;
import com.example.demo.lab.InteractiveLab;
import com.example.demo.lab.InteractiveLabRepository;
import com.example.demo.tainguyen.TaiNguyen;
import com.example.demo.tainguyen.TaiNguyenRepository;
import com.example.demo.tracnghiem.TracNghiem;
import com.example.demo.tracnghiem.TracNghiemRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AdminDanhMucController {
	private final DanhMucRepository danhMucRepository;
	private final BaiHocRepository baiHocRepository;
	private final TracNghiemRepository tracNghiemRepository;
	private final DapAnRepository dapAnRepository;
	private final TaiNguyenRepository taiNguyenRepository;
	private final InteractiveLabRepository interactiveLabRepository;

	public AdminDanhMucController(
		DanhMucRepository danhMucRepository,
		BaiHocRepository baiHocRepository,
		TracNghiemRepository tracNghiemRepository,
		DapAnRepository dapAnRepository,
		TaiNguyenRepository taiNguyenRepository,
		InteractiveLabRepository interactiveLabRepository
	) {
		this.danhMucRepository = danhMucRepository;
		this.baiHocRepository = baiHocRepository;
		this.tracNghiemRepository = tracNghiemRepository;
		this.dapAnRepository = dapAnRepository;
		this.taiNguyenRepository = taiNguyenRepository;
		this.interactiveLabRepository = interactiveLabRepository;
	}

	@GetMapping("/admin")
	public String adminHome() {
		return "admin/index";
	}

	@GetMapping("/admin/danhmuc")
	public String danhMucPage(@RequestParam(required = false) Integer editId, Model model) {
		DanhMuc form = (editId == null)
			? new DanhMuc()
			: danhMucRepository.findById(editId).orElse(new DanhMuc());
		model.addAttribute("items", danhMucRepository.findAll());
		model.addAttribute("form", form);
		return "admin/danhmuc";
	}

	@PostMapping("/admin/danhmuc/save")
	public String saveDanhMuc(
		@RequestParam(required = false) Integer maDM,
		@RequestParam String tenDanhMuc,
		@RequestParam(required = false) String iconURL
	) {
		DanhMuc dm = (maDM == null) ? new DanhMuc() : danhMucRepository.findById(maDM).orElse(new DanhMuc());
		dm.setTenDanhMuc(tenDanhMuc);
		dm.setIconURL(iconURL);
		danhMucRepository.save(dm);
		return "redirect:/admin/danhmuc";
	}

	@PostMapping("/admin/danhmuc/{id}/delete")
	public String deleteDanhMuc(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			danhMucRepository.deleteById(id);
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("error", "Không thể xóa danh mục vì đang có dữ liệu liên quan.");
		}
		return "redirect:/admin/danhmuc";
	}

	@GetMapping("/admin/baihoc")
	public String baiHocPage(@RequestParam(required = false) Integer editId, Model model) {
		BaiHoc form = (editId == null) ? new BaiHoc() : baiHocRepository.findById(editId).orElse(new BaiHoc());
		model.addAttribute("items", baiHocRepository.findAll());
		model.addAttribute("danhMucs", danhMucRepository.findAll());
		model.addAttribute("form", form);
		return "admin/baihoc";
	}

	@PostMapping("/admin/baihoc/save")
	public String saveBaiHoc(
		@RequestParam(required = false) Integer maBH,
		@RequestParam String tenBH,
		@RequestParam String noiDungBH,
		@RequestParam(required = false) String linkURL,
		@RequestParam(required = false) String hinhAnh,
		@RequestParam Integer maDM
	) {
		BaiHoc b = (maBH == null) ? new BaiHoc() : baiHocRepository.findById(maBH).orElse(new BaiHoc());
		b.setTenBH(tenBH);
		b.setNoiDungBH(noiDungBH);
		b.setLinkURL(linkURL);
		b.setHinhAnh(hinhAnh);
		b.setMaDM(maDM);
		if (b.getNgayDang() == null) {
			b.setNgayDang(LocalDateTime.now());
		}
		baiHocRepository.save(b);
		return "redirect:/admin/baihoc";
	}

	@PostMapping("/admin/baihoc/{id}/delete")
	public String deleteBaiHoc(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			baiHocRepository.deleteById(id);
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("error", "Không thể xóa bài học vì còn dữ liệu con.");
		}
		return "redirect:/admin/baihoc";
	}

	@GetMapping("/admin/tracnghiem")
	public String tracNghiemPage(@RequestParam(required = false) Integer editId, Model model) {
		TracNghiem form = (editId == null)
			? new TracNghiem()
			: tracNghiemRepository.findById(editId).orElse(new TracNghiem());
		model.addAttribute("items", tracNghiemRepository.findAll());
		model.addAttribute("baiHocs", baiHocRepository.findAll());
		model.addAttribute("form", form);
		return "admin/tracnghiem";
	}

	@PostMapping("/admin/tracnghiem/save")
	public String saveTracNghiem(
		@RequestParam(required = false) Integer maTN,
		@RequestParam String noiDungCauHoi,
		@RequestParam String loiGiaiThich,
		@RequestParam Integer maBH
	) {
		TracNghiem t = (maTN == null) ? new TracNghiem() : tracNghiemRepository.findById(maTN).orElse(new TracNghiem());
		t.setNoiDungCauHoi(noiDungCauHoi);
		t.setLoiGiaiThich(loiGiaiThich);
		t.setMaBH(maBH);
		tracNghiemRepository.save(t);
		return "redirect:/admin/tracnghiem";
	}

	@PostMapping("/admin/tracnghiem/{id}/delete")
	public String deleteTracNghiem(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
		try {
			tracNghiemRepository.deleteById(id);
		} catch (Exception ex) {
			redirectAttributes.addFlashAttribute("error", "Không thể xóa câu hỏi vì còn đáp án liên quan.");
		}
		return "redirect:/admin/tracnghiem";
	}

	@GetMapping("/admin/dapan")
	public String dapAnPage(@RequestParam(required = false) Integer editId, Model model) {
		DapAn form = (editId == null) ? new DapAn() : dapAnRepository.findById(editId).orElse(new DapAn());
		model.addAttribute("items", dapAnRepository.findAll());
		model.addAttribute("tracNghiems", tracNghiemRepository.findAll());
		model.addAttribute("form", form);
		return "admin/dapan";
	}

	@PostMapping("/admin/dapan/save")
	public String saveDapAn(
		@RequestParam(required = false) Integer maDA,
		@RequestParam String noiDungDapAn,
		@RequestParam(defaultValue = "false") boolean isCorrect,
		@RequestParam Integer maTN
	) {
		DapAn d = (maDA == null) ? new DapAn() : dapAnRepository.findById(maDA).orElse(new DapAn());
		d.setNoiDungDapAn(noiDungDapAn);
		d.setIsCorrect(isCorrect);
		d.setMaTN(maTN);
		dapAnRepository.save(d);
		return "redirect:/admin/dapan";
	}

	@PostMapping("/admin/dapan/{id}/delete")
	public String deleteDapAn(@PathVariable Integer id) {
		dapAnRepository.deleteById(id);
		return "redirect:/admin/dapan";
	}

	@GetMapping("/admin/tainguyen")
	public String taiNguyenPage(@RequestParam(required = false) Integer editId, Model model) {
		TaiNguyen form = (editId == null)
			? new TaiNguyen()
			: taiNguyenRepository.findById(editId).orElse(new TaiNguyen());
		model.addAttribute("items", taiNguyenRepository.findAll());
		model.addAttribute("baiHocs", baiHocRepository.findAll());
		model.addAttribute("form", form);
		return "admin/tainguyen";
	}

	@PostMapping("/admin/tainguyen/save")
	public String saveTaiNguyen(
		@RequestParam(required = false) Integer maTaiNguyen,
		@RequestParam String tenTaiNguyen,
		@RequestParam String linkURL,
		@RequestParam Integer maBH
	) {
		TaiNguyen t = (maTaiNguyen == null)
			? new TaiNguyen()
			: taiNguyenRepository.findById(maTaiNguyen).orElse(new TaiNguyen());
		t.setTenTaiNguyen(tenTaiNguyen);
		t.setLinkURL(linkURL);
		t.setMaBH(maBH);
		taiNguyenRepository.save(t);
		return "redirect:/admin/tainguyen";
	}

	@PostMapping("/admin/tainguyen/{id}/delete")
	public String deleteTaiNguyen(@PathVariable Integer id) {
		taiNguyenRepository.deleteById(id);
		return "redirect:/admin/tainguyen";
	}

	@GetMapping("/admin/lab")
	public String labPage(@RequestParam(required = false) Integer editId, Model model) {
		InteractiveLab form = (editId == null)
			? new InteractiveLab()
			: interactiveLabRepository.findById(editId).orElse(new InteractiveLab());
		model.addAttribute("items", interactiveLabRepository.findAll());
		model.addAttribute("baiHocs", baiHocRepository.findAll());
		model.addAttribute("form", form);
		return "admin/lab";
	}

	@PostMapping("/admin/lab/save")
	public String saveLab(
		@RequestParam(required = false) Integer maTH,
		@RequestParam String tenThucHanh,
		@RequestParam String moTaThucHanh,
		@RequestParam String trangThaiBanDau,
		@RequestParam String trangThaiDung,
		@RequestParam(required = false) String goiY,
		@RequestParam Integer maBH
	) {
		InteractiveLab l = (maTH == null)
			? new InteractiveLab()
			: interactiveLabRepository.findById(maTH).orElse(new InteractiveLab());
		l.setTenThucHanh(tenThucHanh);
		l.setMoTaThucHanh(moTaThucHanh);
		l.setTrangThaiBanDau(trangThaiBanDau);
		l.setTrangThaiDung(trangThaiDung);
		l.setGoiY(goiY);
		l.setMaBH(maBH);
		interactiveLabRepository.save(l);
		return "redirect:/admin/lab";
	}

	@PostMapping("/admin/lab/{id}/delete")
	public String deleteLab(@PathVariable Integer id) {
		interactiveLabRepository.deleteById(id);
		return "redirect:/admin/lab";
	}
}

