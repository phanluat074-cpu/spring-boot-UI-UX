package com.example.demo.tainguyen;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TAINGUYEN")
public class TaiNguyen {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MaTaiNguyen")
	private Integer maTaiNguyen;

	@Column(name = "TenTaiNguyen")
	private String tenTaiNguyen;

	@Column(name = "LinkURL")
	private String linkURL;

	@Column(name = "MaBH")
	private Integer maBH;

	public Integer getMaTaiNguyen() {
		return maTaiNguyen;
	}

	public void setMaTaiNguyen(Integer maTaiNguyen) {
		this.maTaiNguyen = maTaiNguyen;
	}

	public String getTenTaiNguyen() {
		return tenTaiNguyen;
	}

	public void setTenTaiNguyen(String tenTaiNguyen) {
		this.tenTaiNguyen = tenTaiNguyen;
	}

	public String getLinkURL() {
		return linkURL;
	}

	public void setLinkURL(String linkURL) {
		this.linkURL = linkURL;
	}

	public Integer getMaBH() {
		return maBH;
	}

	public void setMaBH(Integer maBH) {
		this.maBH = maBH;
	}
}

