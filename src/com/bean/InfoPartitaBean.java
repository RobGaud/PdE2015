package com.bean;

import java.util.Date;

public class InfoPartitaBean {
	
	//public enum tipoPartita {CALCIO, CALCIOTTO, CALCETTO};
	//private tipoPartita tipo;
	
	private Long id;
	private Date dataOraPartita;
	private int tipo;

	public InfoPartitaBean() {
		this.tipo = 0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if(id != null)
			this.id = id;
	}
	
	public Date getDataOraPartita() {
		return dataOraPartita;
	}

	public void setDataOraPartita(Date dataOraPartita) {
		if(dataOraPartita != null)
			this.dataOraPartita = dataOraPartita;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		if(tipo >=1 && tipo <= 3)
			this.tipo = tipo;
	}
}
