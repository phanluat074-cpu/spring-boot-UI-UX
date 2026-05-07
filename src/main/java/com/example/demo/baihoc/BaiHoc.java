package com.example.demo.baihoc;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "BAIHOC")
public class BaiHoc {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MaBH")
	private Integer maBH;

	@Column(name = "TenBH")
	private String tenBH;

	@Column(name = "NoiDungBH")
	private String noiDungBH;

	@Column(name = "LinkURL")
	private String linkURL;

	@Column(name = "HinhAnh")
	private String hinhAnh;

	@Column(name = "NgayDang")
	private LocalDateTime ngayDang;

	@Column(name = "MaDM")
	private Integer maDM;

	public Integer getMaBH() {
		return maBH;
	}

	public void setMaBH(Integer maBH) {
		this.maBH = maBH;
	}

	public String getTenBH() {
		return tenBH;
	}

	public void setTenBH(String tenBH) {
		this.tenBH = tenBH;
	}

	public String getNoiDungBH() {
		return noiDungBH;
	}

	public void setNoiDungBH(String noiDungBH) {
		this.noiDungBH = noiDungBH;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

	public String getHinhAnh() {
		return hinhAnh;
	}

	public void setHinhAnh(String hinhAnh) {
		this.hinhAnh = hinhAnh;
	}

	public LocalDateTime getNgayDang() {
		return ngayDang;
	}

	public void setNgayDang(LocalDateTime ngayDang) {
		this.ngayDang = ngayDang;
	}

	public Integer getMaDM() {
		return maDM;
	}

	public void setMaDM(Integer maDM) {
		this.maDM = maDM;
	}
}

