package com.example.demo.config;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Lớp tiện ích validate dữ liệu phía server cho tất cả entity admin.
 * Mỗi method trả về Map trống nếu hợp lệ, hoặc Map(fieldName → errorMessage) nếu lỗi.
 */
public final class AdminValidationHelper {

	private AdminValidationHelper() {
	}

	/* ── helpers ───────────────────────────────────────────────── */

	private static boolean isBlank(String s) {
		return s == null || s.trim().isEmpty();
	}

	private static boolean isValidUrl(String s) {
		if (isBlank(s)) return false;
		String lower = s.trim().toLowerCase();
		return lower.startsWith("http://") || lower.startsWith("https://");
	}

	/* ── Danh mục ─────────────────────────────────────────────── */

	public static Map<String, String> validateDanhMuc(String tenDanhMuc, String iconURL) {
		Map<String, String> errors = new LinkedHashMap<>();

		if (isBlank(tenDanhMuc)) {
			errors.put("tenDanhMuc", "Tên danh mục không được để trống.");
		} else if (tenDanhMuc.trim().length() > 200) {
			errors.put("tenDanhMuc", "Tên danh mục không được vượt quá 200 ký tự.");
		}

		if (!isBlank(iconURL)) {
			if (iconURL.trim().length() > 500) {
				errors.put("iconURL", "Icon URL không được vượt quá 500 ký tự.");
			} else if (!isValidUrl(iconURL)) {
				errors.put("iconURL", "Icon URL phải bắt đầu bằng http:// hoặc https://.");
			}
		}

		return errors;
	}

	/* ── Bài học ──────────────────────────────────────────────── */

	public static Map<String, String> validateBaiHoc(
			String tenBH, String noiDungBH, String linkURL, String hinhAnh, Integer maDM) {
		Map<String, String> errors = new LinkedHashMap<>();

		if (isBlank(tenBH)) {
			errors.put("tenBH", "Tên bài học không được để trống.");
		} else if (tenBH.trim().length() > 200) {
			errors.put("tenBH", "Tên bài học không được vượt quá 200 ký tự.");
		}

		if (isBlank(noiDungBH)) {
			errors.put("noiDungBH", "Nội dung bài học không được để trống.");
		}

		if (!isBlank(linkURL) && !isValidUrl(linkURL)) {
			errors.put("linkURL", "Link URL phải bắt đầu bằng http:// hoặc https://.");
		}

		if (!isBlank(hinhAnh) && !isValidUrl(hinhAnh)) {
			errors.put("hinhAnh", "Hình ảnh URL phải bắt đầu bằng http:// hoặc https://.");
		}

		if (maDM == null) {
			errors.put("maDM", "Vui lòng chọn danh mục.");
		}

		return errors;
	}

	/* ── Trắc nghiệm ─────────────────────────────────────────── */

	public static Map<String, String> validateTracNghiem(
			String noiDungCauHoi, String loiGiaiThich, Integer maBH) {
		Map<String, String> errors = new LinkedHashMap<>();

		if (isBlank(noiDungCauHoi)) {
			errors.put("noiDungCauHoi", "Nội dung câu hỏi không được để trống.");
		}

		if (isBlank(loiGiaiThich)) {
			errors.put("loiGiaiThich", "Lời giải thích không được để trống.");
		}

		if (maBH == null) {
			errors.put("maBH", "Vui lòng chọn bài học.");
		}

		return errors;
	}

	/* ── Đáp án ───────────────────────────────────────────────── */

	public static Map<String, String> validateDapAn(String noiDungDapAn, Integer maTN) {
		Map<String, String> errors = new LinkedHashMap<>();

		if (isBlank(noiDungDapAn)) {
			errors.put("noiDungDapAn", "Nội dung đáp án không được để trống.");
		}

		if (maTN == null) {
			errors.put("maTN", "Vui lòng chọn câu hỏi.");
		}

		return errors;
	}

	/* ── Tài nguyên ───────────────────────────────────────────── */

	public static Map<String, String> validateTaiNguyen(String tenTaiNguyen, String linkURL, Integer maBH) {
		Map<String, String> errors = new LinkedHashMap<>();

		if (isBlank(tenTaiNguyen)) {
			errors.put("tenTaiNguyen", "Tên tài nguyên không được để trống.");
		} else if (tenTaiNguyen.trim().length() > 200) {
			errors.put("tenTaiNguyen", "Tên tài nguyên không được vượt quá 200 ký tự.");
		}

		if (isBlank(linkURL)) {
			errors.put("linkURL", "Link URL không được để trống.");
		} else if (!isValidUrl(linkURL)) {
			errors.put("linkURL", "Link URL phải bắt đầu bằng http:// hoặc https://.");
		}

		if (maBH == null) {
			errors.put("maBH", "Vui lòng chọn bài học.");
		}

		return errors;
	}

	/* ── Interactive Lab ──────────────────────────────────────── */

	public static Map<String, String> validateLab(
			String tenThucHanh, String moTaThucHanh,
			String trangThaiBanDau, String trangThaiDung, Integer maBH) {
		Map<String, String> errors = new LinkedHashMap<>();

		if (isBlank(tenThucHanh)) {
			errors.put("tenThucHanh", "Tên thực hành không được để trống.");
		} else if (tenThucHanh.trim().length() > 200) {
			errors.put("tenThucHanh", "Tên thực hành không được vượt quá 200 ký tự.");
		}

		if (isBlank(moTaThucHanh)) {
			errors.put("moTaThucHanh", "Mô tả thực hành không được để trống.");
		}

		if (isBlank(trangThaiBanDau)) {
			errors.put("trangThaiBanDau", "Trạng thái ban đầu không được để trống.");
		}

		if (isBlank(trangThaiDung)) {
			errors.put("trangThaiDung", "Trạng thái đúng không được để trống.");
		}

		if (maBH == null) {
			errors.put("maBH", "Vui lòng chọn bài học.");
		}

		return errors;
	}

	/* ── Login ────────────────────────────────────────────────── */

	public static Map<String, String> validateLogin(String username, String password) {
		Map<String, String> errors = new LinkedHashMap<>();

		if (isBlank(username)) {
			errors.put("username", "Tên đăng nhập không được để trống.");
		}

		if (isBlank(password)) {
			errors.put("password", "Mật khẩu không được để trống.");
		}

		return errors;
	}
}
