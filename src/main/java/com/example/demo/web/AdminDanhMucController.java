package com.example.demo.web;

import com.example.demo.baihoc.BaiHoc;
import com.example.demo.baihoc.BaiHocRepository;
import com.example.demo.config.AdminValidationHelper;
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
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

	private static Integer parseIdOrNull(String raw) {
		if (raw == null || raw.isBlank()) {
			return null;
		}
		try {
			return Integer.parseInt(raw.trim());
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private static void addListToolbar(Model model, Page<?> resultPage, String searchQ, int pageSize) {
		model.addAttribute("resultPage", resultPage);
		model.addAttribute("items", resultPage.getContent());
		model.addAttribute("searchQ", searchQ);
		model.addAttribute("pageSize", pageSize);
	}

	/** Gộp Map<String, String> lỗi thành một chuỗi HTML hiển thị */
	private static String joinErrors(Map<String, String> errors) {
		if (errors.isEmpty()) return null;
		StringBuilder sb = new StringBuilder();
		for (String msg : errors.values()) {
			if (!sb.isEmpty()) sb.append("<br>");
			sb.append("• ").append(msg);
		}
		return sb.toString();
	}

	/* ── Dashboard ────────────────────────────────────────────── */

	@GetMapping("/admin")
	public String adminHome() {
		return "admin/index";
	}

	/* ══════════════════════════════════════════════════════════
	 *  DANH MỤC
	 * ══════════════════════════════════════════════════════════ */

	@GetMapping("/admin/danhmuc")
	public String danhMucPage(
		@RequestParam(required = false) Integer editId,
		@RequestParam(required = false) String q,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		Model model
	) {
		String searchQ = AdminPagingUtil.normalizeSearch(q);
		Pageable pageable = AdminPagingUtil.pageable(page, size, Sort.by("maDM").ascending());
		Page<DanhMuc> resultPage = danhMucRepository.searchPage(searchQ, pageable);

		DanhMuc form = (editId == null)
			? new DanhMuc()
			: danhMucRepository.findById(editId).orElse(new DanhMuc());
		addListToolbar(model, resultPage, searchQ, pageable.getPageSize());
		model.addAttribute("form", form);
		return "admin/danhmuc";
	}

	@PostMapping("/admin/danhmuc/save")
	public String saveDanhMuc(
		@RequestParam(required = false) Integer maDM,
		@RequestParam String tenDanhMuc,
		@RequestParam(required = false) String iconURL,
		RedirectAttributes redirectAttributes
	) {
		// ── Server-side validation ──
		Map<String, String> errors = AdminValidationHelper.validateDanhMuc(tenDanhMuc, iconURL);
		if (!errors.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", joinErrors(errors));
			return "redirect:/admin/danhmuc" + (maDM != null ? "?editId=" + maDM : "");
		}

		DanhMuc dm = (maDM == null) ? new DanhMuc() : danhMucRepository.findById(maDM).orElse(new DanhMuc());
		dm.setTenDanhMuc(tenDanhMuc.trim());
		dm.setIconURL(iconURL != null ? iconURL.trim() : null);
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

	/* ══════════════════════════════════════════════════════════
	 *  BÀI HỌC
	 * ══════════════════════════════════════════════════════════ */

	@GetMapping("/admin/baihoc")
	public String baiHocPage(
		@RequestParam(required = false) Integer editId,
		@RequestParam(required = false) String q,
		@RequestParam(required = false) String maDM,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		Model model
	) {
		String searchQ = AdminPagingUtil.normalizeSearch(q);
		Integer maDMFilter = parseIdOrNull(maDM);
		Pageable pageable = AdminPagingUtil.pageable(page, size, Sort.by("ngayDang").descending());
		Page<BaiHoc> resultPage = baiHocRepository.searchPage(searchQ, maDMFilter, pageable);

		BaiHoc form = (editId == null) ? new BaiHoc() : baiHocRepository.findById(editId).orElse(new BaiHoc());
		addListToolbar(model, resultPage, searchQ, pageable.getPageSize());
		model.addAttribute("filterMaDM", maDMFilter);
		model.addAttribute("danhMucs", danhMucRepository.findAll(Sort.by("maDM").ascending()));
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
		@RequestParam(required = false) Integer maDM,
		RedirectAttributes redirectAttributes
	) {
		// ── Server-side validation ──
		Map<String, String> errors = AdminValidationHelper.validateBaiHoc(tenBH, noiDungBH, linkURL, hinhAnh, maDM);
		if (!errors.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", joinErrors(errors));
			return "redirect:/admin/baihoc" + (maBH != null ? "?editId=" + maBH : "");
		}

		BaiHoc b = (maBH == null) ? new BaiHoc() : baiHocRepository.findById(maBH).orElse(new BaiHoc());
		b.setTenBH(tenBH.trim());
		b.setNoiDungBH(noiDungBH.trim());
		b.setLinkURL(linkURL != null ? linkURL.trim() : null);
		b.setHinhAnh(hinhAnh != null ? hinhAnh.trim() : null);
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

	/* ══════════════════════════════════════════════════════════
	 *  TRẮC NGHIỆM
	 * ══════════════════════════════════════════════════════════ */

	@GetMapping("/admin/tracnghiem")
	public String tracNghiemPage(
		@RequestParam(required = false) Integer editId,
		@RequestParam(required = false) String q,
		@RequestParam(required = false) String maBH,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		Model model
	) {
		String searchQ = AdminPagingUtil.normalizeSearch(q);
		Integer maBHFilter = parseIdOrNull(maBH);
		Pageable pageable = AdminPagingUtil.pageable(page, size, Sort.by("maTN").ascending());
		Page<TracNghiem> resultPage = tracNghiemRepository.searchPage(searchQ, maBHFilter, pageable);

		TracNghiem form = (editId == null)
			? new TracNghiem()
			: tracNghiemRepository.findById(editId).orElse(new TracNghiem());
		addListToolbar(model, resultPage, searchQ, pageable.getPageSize());
		model.addAttribute("filterMaBH", maBHFilter);
		model.addAttribute("baiHocs", baiHocRepository.findAll(Sort.by("maBH").ascending()));
		model.addAttribute("form", form);
		return "admin/tracnghiem";
	}

	@PostMapping("/admin/tracnghiem/save")
	public String saveTracNghiem(
		@RequestParam(required = false) Integer maTN,
		@RequestParam String noiDungCauHoi,
		@RequestParam String loiGiaiThich,
		@RequestParam(required = false) Integer maBH,
		RedirectAttributes redirectAttributes
	) {
		// ── Server-side validation ──
		Map<String, String> errors = AdminValidationHelper.validateTracNghiem(noiDungCauHoi, loiGiaiThich, maBH);
		if (!errors.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", joinErrors(errors));
			return "redirect:/admin/tracnghiem" + (maTN != null ? "?editId=" + maTN : "");
		}

		TracNghiem t = (maTN == null) ? new TracNghiem() : tracNghiemRepository.findById(maTN).orElse(new TracNghiem());
		t.setNoiDungCauHoi(noiDungCauHoi.trim());
		t.setLoiGiaiThich(loiGiaiThich.trim());
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

	/* ══════════════════════════════════════════════════════════
	 *  ĐÁP ÁN
	 * ══════════════════════════════════════════════════════════ */

	@GetMapping("/admin/dapan")
	public String dapAnPage(
		@RequestParam(required = false) Integer editId,
		@RequestParam(required = false) String q,
		@RequestParam(required = false) String maTN,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		Model model
	) {
		String searchQ = AdminPagingUtil.normalizeSearch(q);
		Integer maTNFilter = parseIdOrNull(maTN);
		Pageable pageable = AdminPagingUtil.pageable(page, size, Sort.by("maDA").ascending());
		Page<DapAn> resultPage = dapAnRepository.searchPage(searchQ, maTNFilter, pageable);

		DapAn form = (editId == null) ? new DapAn() : dapAnRepository.findById(editId).orElse(new DapAn());
		addListToolbar(model, resultPage, searchQ, pageable.getPageSize());
		model.addAttribute("filterMaTN", maTNFilter);
		model.addAttribute("tracNghiems", tracNghiemRepository.findAll(Sort.by("maTN").ascending()));
		model.addAttribute("form", form);
		return "admin/dapan";
	}

	@PostMapping("/admin/dapan/save")
	public String saveDapAn(
		@RequestParam(required = false) Integer maDA,
		@RequestParam String noiDungDapAn,
		@RequestParam(defaultValue = "false") boolean isCorrect,
		@RequestParam(required = false) Integer maTN,
		RedirectAttributes redirectAttributes
	) {
		// ── Server-side validation ──
		Map<String, String> errors = AdminValidationHelper.validateDapAn(noiDungDapAn, maTN);
		if (!errors.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", joinErrors(errors));
			return "redirect:/admin/dapan" + (maDA != null ? "?editId=" + maDA : "");
		}

		DapAn d = (maDA == null) ? new DapAn() : dapAnRepository.findById(maDA).orElse(new DapAn());
		d.setNoiDungDapAn(noiDungDapAn.trim());
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

	/* ══════════════════════════════════════════════════════════
	 *  TÀI NGUYÊN
	 * ══════════════════════════════════════════════════════════ */

	@GetMapping("/admin/tainguyen")
	public String taiNguyenPage(
		@RequestParam(required = false) Integer editId,
		@RequestParam(required = false) String q,
		@RequestParam(required = false) String maBH,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		Model model
	) {
		String searchQ = AdminPagingUtil.normalizeSearch(q);
		Integer maBHFilter = parseIdOrNull(maBH);
		Pageable pageable = AdminPagingUtil.pageable(page, size, Sort.by("maTaiNguyen").ascending());
		Page<TaiNguyen> resultPage = taiNguyenRepository.searchPage(searchQ, maBHFilter, pageable);

		TaiNguyen form = (editId == null)
			? new TaiNguyen()
			: taiNguyenRepository.findById(editId).orElse(new TaiNguyen());
		addListToolbar(model, resultPage, searchQ, pageable.getPageSize());
		model.addAttribute("filterMaBH", maBHFilter);
		model.addAttribute("baiHocs", baiHocRepository.findAll(Sort.by("maBH").ascending()));
		model.addAttribute("form", form);
		return "admin/tainguyen";
	}

	@PostMapping("/admin/tainguyen/save")
	public String saveTaiNguyen(
		@RequestParam(required = false) Integer maTaiNguyen,
		@RequestParam String tenTaiNguyen,
		@RequestParam String linkURL,
		@RequestParam(required = false) Integer maBH,
		RedirectAttributes redirectAttributes
	) {
		// ── Server-side validation ──
		Map<String, String> errors = AdminValidationHelper.validateTaiNguyen(tenTaiNguyen, linkURL, maBH);
		if (!errors.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", joinErrors(errors));
			return "redirect:/admin/tainguyen" + (maTaiNguyen != null ? "?editId=" + maTaiNguyen : "");
		}

		TaiNguyen t = (maTaiNguyen == null)
			? new TaiNguyen()
			: taiNguyenRepository.findById(maTaiNguyen).orElse(new TaiNguyen());
		t.setTenTaiNguyen(tenTaiNguyen.trim());
		t.setLinkURL(linkURL.trim());
		t.setMaBH(maBH);
		taiNguyenRepository.save(t);
		return "redirect:/admin/tainguyen";
	}

	@PostMapping("/admin/tainguyen/{id}/delete")
	public String deleteTaiNguyen(@PathVariable Integer id) {
		taiNguyenRepository.deleteById(id);
		return "redirect:/admin/tainguyen";
	}

	/* ══════════════════════════════════════════════════════════
	 *  INTERACTIVE LAB
	 * ══════════════════════════════════════════════════════════ */

	@GetMapping("/admin/lab")
	public String labPage(
		@RequestParam(required = false) Integer editId,
		@RequestParam(required = false) String q,
		@RequestParam(required = false) String maBH,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		Model model
	) {
		String searchQ = AdminPagingUtil.normalizeSearch(q);
		Integer maBHFilter = parseIdOrNull(maBH);
		Pageable pageable = AdminPagingUtil.pageable(page, size, Sort.by("maTH").ascending());
		Page<InteractiveLab> resultPage = interactiveLabRepository.searchPage(searchQ, maBHFilter, pageable);

		InteractiveLab form = (editId == null)
			? new InteractiveLab()
			: interactiveLabRepository.findById(editId).orElse(new InteractiveLab());
		addListToolbar(model, resultPage, searchQ, pageable.getPageSize());
		model.addAttribute("filterMaBH", maBHFilter);
		model.addAttribute("baiHocs", baiHocRepository.findAll(Sort.by("maBH").ascending()));
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
		@RequestParam(required = false) Integer maBH,
		RedirectAttributes redirectAttributes
	) {
		// ── Server-side validation ──
		Map<String, String> errors = AdminValidationHelper.validateLab(
				tenThucHanh, moTaThucHanh, trangThaiBanDau, trangThaiDung, maBH);
		if (!errors.isEmpty()) {
			redirectAttributes.addFlashAttribute("error", joinErrors(errors));
			return "redirect:/admin/lab" + (maTH != null ? "?editId=" + maTH : "");
		}

		InteractiveLab l = (maTH == null)
			? new InteractiveLab()
			: interactiveLabRepository.findById(maTH).orElse(new InteractiveLab());
		l.setTenThucHanh(tenThucHanh.trim());
		l.setMoTaThucHanh(moTaThucHanh.trim());
		l.setTrangThaiBanDau(trangThaiBanDau.trim());
		l.setTrangThaiDung(trangThaiDung.trim());
		l.setGoiY(goiY != null ? goiY.trim() : null);
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
