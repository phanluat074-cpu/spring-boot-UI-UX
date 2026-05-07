package com.example.demo.tracnghiem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "TRACNGHIEM")
public class TracNghiem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MaTN")
	private Integer maTN;

	@Column(name = "NoiDungCauHoi")
	private String noiDungCauHoi;

	@Column(name = "LoiGiaiThich")
	private String loiGiaiThich;

	@Column(name = "MaBH")
	private Integer maBH;

	public Integer getMaTN() {
		return maTN;
	}

	public void setMaTN(Integer maTN) {
		this.maTN = maTN;
	}

	public String getNoiDungCauHoi() {
		return noiDungCauHoi;
	}

	public void setNoiDungCauHoi(String noiDungCauHoi) {
		this.noiDungCauHoi = noiDungCauHoi;
	}

	public String getLoiGiaiThich() {
		return loiGiaiThich;
	}

	public void setLoiGiaiThich(String loiGiaiThich) {
		this.loiGiaiThich = loiGiaiThich;
	}

	public Integer getMaBH() {
		return maBH;
	}

	public void setMaBH(Integer maBH) {
		this.maBH = maBH;
	}
}

