package com.bean;

public class DefaultBean
{
	private String result;
	private String httpCode;
	private Long idCreated;
	private boolean answer;
	
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

	public Long getIdCreated() {
		return idCreated;
	}

	public void setIdCreated(Long idCreated) {
		this.idCreated = idCreated;
	}

	public boolean isAnswer() {
		return answer;
	}

	public void setAnswer(boolean answer) {
		this.answer = answer;
	}
	
}
