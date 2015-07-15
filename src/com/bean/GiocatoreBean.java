package com.bean;

import com.modello.Giocatore;;

public class GiocatoreBean {

	Giocatore giocatore;
	private String result;
	private String httpCode;
	
	public Giocatore getGiocatore() {
		return giocatore;
	}
	
	public void setGiocatore(Giocatore giocatore) {
		this.giocatore = giocatore;
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
