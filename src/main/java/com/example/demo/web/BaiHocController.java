package com.example.demo.web;

import com.example.demo.baihoc.BaiHocService;
import com.example.demo.dapan.DapAn;
import com.example.demo.dapan.DapAnRepository;
import com.example.demo.danhmuc.DanhMucService;
import com.example.demo.lab.InteractiveLabRepository;
import com.example.demo.tainguyen.TaiNguyenRepository;
import com.example.demo.tracnghiem.TracNghiem;
import com.example.demo.tracnghiem.TracNghiemRepository;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class BaiHocController {
	private final BaiHocService baiHocService;
	private final DanhMucService danhMucService;
	private final TracNghiemRepository tracNghiemRepository;
	private final DapAnRepository dapAnRepository;
	private final TaiNguyenRepository taiNguyenRepository;
	private final InteractiveLabRepository interactiveLabRepository;

	public BaiHocController(
		BaiHocService baiHocService,
		DanhMucService danhMucService,
		TracNghiemRepository tracNghiemRepository,
		DapAnRepository dapAnRepository,
		TaiNguyenRepository taiNguyenRepository,
		InteractiveLabRepository interactiveLabRepository
	) {
		this.baiHocService = baiHocService;
		this.danhMucService = danhMucService;
		this.tracNghiemRepository = tracNghiemRepository;
		this.dapAnRepository = dapAnRepository;
		this.taiNguyenRepository = taiNguyenRepository;
		this.interactiveLabRepository = interactiveLabRepository;
	}

	@GetMapping("/danhmuc/{maDM}")
	public String danhSachBaiHoc(@PathVariable Integer maDM, Model model) {
		model.addAttribute("maDM", maDM);
		model.addAttribute("danhMuc", danhMucService.findById(maDM).orElse(null));
		model.addAttribute("baiHocs", baiHocService.findByDanhMuc(maDM));
		return "baihoc-list";
	}

	@GetMapping("/baihoc/{maBH}")
	public String chiTietBaiHoc(@PathVariable Integer maBH, Model model) {
		var baiHocOpt = baiHocService.findById(maBH);
		if (baiHocOpt.isEmpty()) {
			return "redirect:/";
		}

		var baiHoc = baiHocOpt.get();
		var danhMuc = danhMucService.findById(baiHoc.getMaDM()).orElse(null);
		List<TracNghiem> questions = tracNghiemRepository.findByMaBHOrderByMaTNAsc(maBH);
		List<QuizItem> quizItems = questions.stream()
			.map(q -> new QuizItem(q, dapAnRepository.findByMaTNOrderByMaDAAsc(q.getMaTN())))
			.toList();

		model.addAttribute("baiHoc", baiHoc);
		model.addAttribute("danhMuc", danhMuc);
		model.addAttribute("videoEmbedUrl", toEmbedUrl(baiHoc.getLinkURL()));
		model.addAttribute("quizItems", quizItems);
		model.addAttribute("taiNguyens", taiNguyenRepository.findByMaBHOrderByMaTaiNguyenAsc(maBH));
		model.addAttribute("labs", interactiveLabRepository.findByMaBHOrderByMaTHAsc(maBH));
		model.addAttribute("nextLessons", baiHocService.findByDanhMuc(baiHoc.getMaDM()).stream().limit(5).toList());
		return "baihoc-detail";
	}

	private String toEmbedUrl(String originalUrl) {
		if (originalUrl == null || originalUrl.isBlank()) {
			return "";
		}
		if (originalUrl.contains("youtube.com/watch?v=")) {
			String videoId = originalUrl.substring(originalUrl.indexOf("v=") + 2);
			int ampIndex = videoId.indexOf('&');
			if (ampIndex > -1) {
				videoId = videoId.substring(0, ampIndex);
			}
			return "https://www.youtube.com/embed/" + videoId;
		}
		if (originalUrl.contains("youtu.be/")) {
			String videoId = originalUrl.substring(originalUrl.lastIndexOf('/') + 1);
			return "https://www.youtube.com/embed/" + videoId;
		}
		return originalUrl;
	}

	@PostMapping("/api/quiz/check")
	@ResponseBody
	public Map<String, Object> checkQuizAnswer(@RequestBody QuizCheckRequest request) {
		var answerOpt = dapAnRepository.findById(request.maDA());
		if (answerOpt.isEmpty()) {
			return Map.of("ok", false, "message", "Không tìm thấy đáp án.");
		}

		var answer = answerOpt.get();
		var questionOpt = tracNghiemRepository.findById(answer.getMaTN());
		String explanation = questionOpt.map(TracNghiem::getLoiGiaiThich).orElse("");
		boolean correct = Boolean.TRUE.equals(answer.getIsCorrect());

		return Map.of(
			"ok", true,
			"correct", correct,
			"explanation", explanation
		);
	}

	@PostMapping("/api/lab/check")
	@ResponseBody
	public Map<String, Object> checkLabState(@RequestBody LabCheckRequest request) {
		var labOpt = interactiveLabRepository.findById(request.maTH());
		if (labOpt.isEmpty()) {
			return Map.of("ok", false, "message", "Không tìm thấy bài lab.");
		}

		var lab = labOpt.get();
		boolean matched = compareState(request.userState(), lab.getTrangThaiDung());

		return Map.of(
			"ok", true,
			"correct", matched,
			"hint", lab.getGoiY() == null ? "" : lab.getGoiY(),
			"expectedState", lab.getTrangThaiDung() == null ? "" : lab.getTrangThaiDung()
		);
	}

	private boolean compareState(String userState, String expectedState) {
		String user = userState == null ? "" : userState.trim();
		String expected = expectedState == null ? "" : expectedState.trim();
		String canonicalUser = canonicalizeSimpleJsonObject(user);
		String canonicalExpected = canonicalizeSimpleJsonObject(expected);
		return canonicalUser.equalsIgnoreCase(canonicalExpected);
	}

	// Canonicalizes simple JSON object strings so key order/whitespace doesn't affect comparison.
	private String canonicalizeSimpleJsonObject(String raw) {
		if (raw == null) {
			return "";
		}
		String compact = raw.trim().replaceAll("\\s+", "");
		if (!compact.startsWith("{") || !compact.endsWith("}")) {
			return compact;
		}
		String inner = compact.substring(1, compact.length() - 1);
		if (inner.isBlank()) {
			return "{}";
		}
		String[] parts = inner.split(",");
		Arrays.sort(parts, Comparator.naturalOrder());
		return "{" + String.join(",", parts) + "}";
	}

	public record QuizItem(TracNghiem question, List<DapAn> answers) {}
	public record QuizCheckRequest(Integer maDA) {}
	public record LabCheckRequest(Integer maTH, String userState) {}
}

