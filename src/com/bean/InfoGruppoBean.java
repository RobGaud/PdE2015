package com.bean;

import java.util.Date;

public class InfoGruppoBean {
	
	private Long id;
	private String nome;
	private String citta;
	private boolean aperto;
	
	public InfoGruppoBean() {
		this.aperto = false;
	}
	
	public String getCitta() {
		return citta;
	}

	public void setCitta(String citta) {
		this.citta = citta;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if(id != null)
			this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		if(nome != null)
			this.nome = nome;
	}

	public boolean isAperto() {
		return aperto;
	}

	public void setAperto(boolean aperto) {
		this.aperto = aperto;
	}

}
