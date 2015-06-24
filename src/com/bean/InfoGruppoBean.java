package com.bean;

import java.util.Date;

import com.googlecode.objectify.annotation.Index;

public class InfoGruppoBean {
	
	private Long id;
	private String nome;
	private Date dataCreazione;
	private boolean aperto;
	
	public InfoGruppoBean() {
		this.aperto = false;
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

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		if(dataCreazione != null)
			this.dataCreazione = dataCreazione;
	}

	public boolean isAperto() {
		return aperto;
	}

	public void setAperto(boolean aperto) {
		this.aperto = aperto;
	}

}
