package com.bean;

import com.modello.Partita;

public class PartitaBean {

	Partita partita;
	int tipo;
	String dataString;
	
	private String result;
	private String httpCode;
	
	public Partita getPartita() {
		return partita;
	}
	
	public void setPartita(Partita partita) {
		this.partita = partita;
	}
	
	public String getDataString() {
		return dataString;
	}

	public void setDataString(String dataString) {
		this.dataString = dataString;
	}

	public int getTipo() {
		return tipo;
	}

	public void setTipo(int tipo) {
		this.tipo = tipo;
	}
	
	public String getResult() {
		return result;
	}
	
	public void setResult(String result) {
		this.result = result;
	}
	
	public String getHttpCode() {
		return httpCode;
	}
	
	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}

}
