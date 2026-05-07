package com.example.demo.lab;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "INTERACTIVE_LAB")
public class InteractiveLab {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MaTH")
	private Integer maTH;

	@Column(name = "TenThucHanh")
	private String tenThucHanh;

	@Column(name = "MoTaThucHanh")
	private String moTaThucHanh;

	@Column(name = "TrangThaiBanDau")
	private String trangThaiBanDau;

	@Column(name = "TrangThaiDung")
	private String trangThaiDung;

	@Column(name = "GoiY")
	private String goiY;

	@Column(name = "MaBH")
	private Integer maBH;

	public Integer getMaTH() {
		return maTH;
	}

	public void setMaTH(Integer maTH) {
		this.maTH = maTH;
	}

	public String getTenThucHanh() {
		return tenThucHanh;
	}

	public void setTenThucHanh(String tenThucHanh) {
		this.tenThucHanh = tenThucHanh;
	}

	public String getMoTaThucHanh() {
		return moTaThucHanh;
	}

	public void setMoTaThucHanh(String moTaThucHanh) {
		this.moTaThucHanh = moTaThucHanh;
	}

	public String getTrangThaiBanDau() {
		return trangThaiBanDau;
	}

	public void setTrangThaiBanDau(String trangThaiBanDau) {
		this.trangThaiBanDau = trangThaiBanDau;
	}

	public String getTrangThaiDung() {
		return trangThaiDung;
	}

	public void setTrangThaiDung(String trangThaiDung) {
		this.trangThaiDung = trangThaiDung;
	}

	public String getGoiY() {
		return goiY;
	}

	public void setGoiY(String goiY) {
		this.goiY = goiY;
	}

	public Integer getMaBH() {
		return maBH;
	}

	public void setMaBH(Integer maBH) {
		this.maBH = maBH;
	}
}

