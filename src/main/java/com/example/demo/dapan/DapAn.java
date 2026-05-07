package com.example.demo.dapan;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "DAPAN")
public class DapAn {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "MaDA")
	private Integer maDA;

	@Column(name = "NoiDungDapAn")
	private String noiDungDapAn;

	@Column(name = "IsCorrect")
	private Boolean isCorrect;

	@Column(name = "MaTN")
	private Integer maTN;

	public Integer getMaDA() {
		return maDA;
	}

	public void setMaDA(Integer maDA) {
		this.maDA = maDA;
	}

	public String getNoiDungDapAn() {
		return noiDungDapAn;
	}

	public void setNoiDungDapAn(String noiDungDapAn) {
		this.noiDungDapAn = noiDungDapAn;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean correct) {
		isCorrect = correct;
	}

	public Integer getMaTN() {
		return maTN;
	}

	public void setMaTN(Integer maTN) {
		this.maTN = maTN;
	}
}

