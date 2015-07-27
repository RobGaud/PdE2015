package com.bean;

import com.modello.Gruppo;

public class GruppoBean {

	Gruppo gruppo;
	private String result;
	private String httpCode;
	private String emailAdmin;
	
	public Gruppo getGruppo() {
		return gruppo;
	}
	
	public void setGruppo(Gruppo gruppo) {
		this.gruppo = gruppo;
	}
	
	public String getEmailAdmin() {
		return emailAdmin;
	}

	public void setEmailAdmin(String emailAdmin) {
		this.emailAdmin = emailAdmin;
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
