package com.bean;

public class NDisponibiliBean
{
	private String result;
	private String httpCode;
	
	private int nDisponibili;
	
	public String getReport() {
		return httpCode;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result)
	{
		this.result = result;
	}

	public String getHttpCode() {
		return httpCode;
	}

	public void setHttpCode(String httpCode) {
		this.httpCode = httpCode;
	}
	
	public int getnDisponibili() {
		return nDisponibili;
	}

	public void setnDisponibili(int nDisponibili) {
		this.nDisponibili = nDisponibili;
	}
}
